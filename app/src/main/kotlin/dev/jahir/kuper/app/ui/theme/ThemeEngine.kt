package dev.jahir.kuper.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import dev.jahir.kuper.app.R

/**
 * 主题引擎
 * 负责管理应用主题和动态颜色
 */
object ThemeEngine {
    private const val TAG = "ThemeEngine"
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    
    private var prefs: SharedPreferences? = null
    private var currentColorScheme: ColorScheme? = null
    
    /**
     * 初始化主题引擎
     * 必须在 Application.onCreate() 中调用
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        Log.d(TAG, "ThemeEngine initialized")
    }
    
    /**
     * 获取当前主题模式
     */
    fun getCurrentThemeMode(): ThemeMode {
        val modeName = prefs?.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
            ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(modeName)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Invalid theme mode: $modeName, using SYSTEM")
            ThemeMode.SYSTEM
        }
    }
    
    /**
     * 设置主题模式
     * 保存用户偏好到 SharedPreferences
     */
    fun setThemeMode(mode: ThemeMode) {
        prefs?.edit()?.putString(KEY_THEME_MODE, mode.name)?.apply()
        Log.d(TAG, "Theme mode set to: $mode")
        
        // 应用系统级主题设置
        applySystemThemeMode(mode)
    }
    
    /**
     * 应用系统级主题模式
     */
    private fun applySystemThemeMode(mode: ThemeMode) {
        val nightMode = when (mode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK, ThemeMode.AMOLED -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
    
    /**
     * 检查是否支持 Material You (Android 12+)
     */
    fun supportsMaterialYou(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
    
    /**
     * 提取动态颜色
     * Android 12+ 使用系统动态颜色
     * Android 11- 使用预设配色方案
     */
    fun extractDynamicColors(context: Context): ColorScheme {
        return if (supportsMaterialYou()) {
            // Android 12+ 使用动态颜色
            // 注意：实际的动态颜色提取需要使用 Material3 的 DynamicColors API
            // 这里返回默认配色，实际实现在 applyTheme() 中通过主题资源实现
            Log.d(TAG, "Using Material You dynamic colors")
            getCurrentColorScheme(context)
        } else {
            // Android 11- 使用预设配色
            Log.d(TAG, "Using preset color scheme")
            getCurrentColorScheme(context)
        }
    }
    
    /**
     * 获取当前颜色方案
     */
    private fun getCurrentColorScheme(context: Context): ColorScheme {
        if (currentColorScheme != null) {
            return currentColorScheme!!
        }
        
        val mode = getCurrentThemeMode()
        val isDark = when (mode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK, ThemeMode.AMOLED -> true
            ThemeMode.SYSTEM -> {
                val nightMode = context.resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
        
        currentColorScheme = when {
            mode == ThemeMode.AMOLED -> ColorScheme.DEFAULT_AMOLED
            isDark -> ColorScheme.DEFAULT_DARK
            else -> ColorScheme.DEFAULT_LIGHT
        }
        
        return currentColorScheme!!
    }
    
    /**
     * 应用主题到 Activity
     */
    fun applyTheme(activity: Activity) {
        val mode = getCurrentThemeMode()
        val themeResId = getThemeResourceId(mode)
        
        activity.setTheme(themeResId)
        Log.d(TAG, "Applied theme: $mode (resId: $themeResId)")
    }
    
    /**
     * Theme Usage Mode
     */
    enum class UsageMode {
        DYNAMIC, // Monet (Android 12+)
        STATIC   // MikuBox
    }

    /**
     * Color Style for Monet
     */
    enum class ColorStyle {
        EMPHASIS,
        PASTEL
    }

    private const val KEY_USAGE_MODE = "usage_mode"
    private const val KEY_COLOR_STYLE = "color_style"

    /**
     * 获取当前 UsageMode
     */
    fun getUsageMode(): UsageMode {
        if (!supportsMaterialYou()) return UsageMode.STATIC // Force static on old devices
        
        val name = prefs?.getString(KEY_USAGE_MODE, UsageMode.DYNAMIC.name) 
            ?: UsageMode.DYNAMIC.name
        return try {
            UsageMode.valueOf(name)
        } catch (e: Exception) {
            UsageMode.DYNAMIC
        }
    }

    fun setUsageMode(mode: UsageMode) {
        prefs?.edit()?.putString(KEY_USAGE_MODE, mode.name)?.apply()
    }

    /**
     * 获取当前 ColorStyle
     */
    fun getColorStyle(): ColorStyle {
        val name = prefs?.getString(KEY_COLOR_STYLE, ColorStyle.EMPHASIS.name)
            ?: ColorStyle.EMPHASIS.name
        return try {
            ColorStyle.valueOf(name)
        } catch (e: Exception) {
            ColorStyle.EMPHASIS
        }
    }

    fun setColorStyle(style: ColorStyle) {
        prefs?.edit()?.putString(KEY_COLOR_STYLE, style.name)?.apply()
    }

    /**
     * 获取主题资源 ID
     */
    private fun getThemeResourceId(mode: ThemeMode): Int {
        val usageMode = getUsageMode()
        
        // If Static mode or device doesn't support Material You, use MikuBox
        if (usageMode == UsageMode.STATIC || !supportsMaterialYou()) {
            return when (mode) {
                ThemeMode.LIGHT -> R.style.Theme_MikuBox_Light
                ThemeMode.DARK -> R.style.Theme_MikuBox_Dark
                ThemeMode.AMOLED -> R.style.Theme_MikuBox_Amoled
                ThemeMode.SYSTEM -> R.style.Theme_MikuBox_Light
            }
        }

        // Dynamic Mode (Material You)
        val colorStyle = getColorStyle()
        return if (colorStyle == ColorStyle.PASTEL) {
             when (mode) {
                ThemeMode.LIGHT -> R.style.Theme_Monet_Pastel_Light
                ThemeMode.DARK -> R.style.Theme_Monet_Pastel_Dark
                ThemeMode.AMOLED -> R.style.Theme_Monet_Pastel_Amoled
                ThemeMode.SYSTEM -> R.style.Theme_Monet_Pastel_Light
            }
        } else {
            // Emphasis (Default)
            when (mode) {
                ThemeMode.LIGHT -> R.style.Theme_Monet_Emphasis_Light
                ThemeMode.DARK -> R.style.Theme_Monet_Emphasis_Dark
                ThemeMode.AMOLED -> R.style.Theme_Monet_Emphasis_Amoled
                ThemeMode.SYSTEM -> R.style.Theme_Monet_Emphasis_Light
            }
        }
    }
    
    /**
     * 清除缓存的颜色方案
     * 在主题切换时调用
     */
    fun clearColorSchemeCache() {
        currentColorScheme = null
    }
}
