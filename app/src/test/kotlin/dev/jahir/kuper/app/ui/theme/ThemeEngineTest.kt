package dev.jahir.kuper.app.ui.theme

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * ThemeEngine 单元测试
 * 测试主题引擎的核心功能
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class ThemeEngineTest {
    
    private lateinit var context: Context
    
    @BeforeEach
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        ThemeEngine.initialize(context)
    }
    
    @AfterEach
    fun tearDown() {
        // 清理：重置为默认主题
        ThemeEngine.setThemeMode(ThemeMode.SYSTEM)
        ThemeEngine.clearColorSchemeCache()
    }
    
    @Test
    fun `initialize should not throw exception`() {
        assertDoesNotThrow {
            ThemeEngine.initialize(context)
        }
    }
    
    @Test
    fun `getCurrentThemeMode should return default SYSTEM mode initially`() {
        // 清除之前的设置
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            .edit().clear().commit()
        ThemeEngine.initialize(context)
        
        val mode = ThemeEngine.getCurrentThemeMode()
        assertEquals(ThemeMode.SYSTEM, mode)
    }
    
    @Test
    fun `setThemeMode should persist theme mode`() {
        ThemeEngine.setThemeMode(ThemeMode.DARK)
        val mode = ThemeEngine.getCurrentThemeMode()
        assertEquals(ThemeMode.DARK, mode)
    }
    
    @Test
    fun `setThemeMode should work for all theme modes`() {
        for (mode in ThemeMode.values()) {
            ThemeEngine.setThemeMode(mode)
            val retrievedMode = ThemeEngine.getCurrentThemeMode()
            assertEquals(mode, retrievedMode, "Failed for mode: $mode")
        }
    }
    
    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun `supportsMaterialYou should return true on Android 12+`() {
        assertTrue(ThemeEngine.supportsMaterialYou())
    }
    
    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun `supportsMaterialYou should return false on Android 11-`() {
        assertFalse(ThemeEngine.supportsMaterialYou())
    }
    
    @Test
    fun `extractDynamicColors should return valid color scheme`() {
        val colorScheme = ThemeEngine.extractDynamicColors(context)
        
        assertNotNull(colorScheme)
        assertNotEquals(0, colorScheme.primary)
        assertNotEquals(0, colorScheme.onPrimary)
        assertNotEquals(0, colorScheme.surface)
        assertNotEquals(0, colorScheme.background)
    }
    
    @Test
    fun `extractDynamicColors should return AMOLED colors for AMOLED mode`() {
        ThemeEngine.setThemeMode(ThemeMode.AMOLED)
        ThemeEngine.clearColorSchemeCache()
        
        val colorScheme = ThemeEngine.extractDynamicColors(context)
        
        assertEquals(0xFF000000.toInt(), colorScheme.background)
        assertEquals(0xFF000000.toInt(), colorScheme.surface)
    }
    
    @Test
    fun `extractDynamicColors should return light colors for LIGHT mode`() {
        ThemeEngine.setThemeMode(ThemeMode.LIGHT)
        ThemeEngine.clearColorSchemeCache()
        
        val colorScheme = ThemeEngine.extractDynamicColors(context)
        
        // 验证是浅色主题（背景应该是浅色）
        val brightness = calculateBrightness(colorScheme.background)
        assertTrue(brightness > 128, "Background should be light, but brightness was $brightness")
    }
    
    @Test
    fun `extractDynamicColors should return dark colors for DARK mode`() {
        ThemeEngine.setThemeMode(ThemeMode.DARK)
        ThemeEngine.clearColorSchemeCache()
        
        val colorScheme = ThemeEngine.extractDynamicColors(context)
        
        // 验证是深色主题（背景应该是深色）
        val brightness = calculateBrightness(colorScheme.background)
        assertTrue(brightness < 128, "Background should be dark, but brightness was $brightness")
    }
    
    @Test
    fun `clearColorSchemeCache should clear cached color scheme`() {
        // 第一次提取
        val colorScheme1 = ThemeEngine.extractDynamicColors(context)
        
        // 清除缓存
        ThemeEngine.clearColorSchemeCache()
        
        // 第二次提取应该重新计算
        val colorScheme2 = ThemeEngine.extractDynamicColors(context)
        
        // 两次提取的结果应该相同（因为主题模式没变）
        assertEquals(colorScheme1, colorScheme2)
    }
    
    @Test
    fun `theme mode persistence should survive reinitialization`() {
        // 设置主题
        ThemeEngine.setThemeMode(ThemeMode.DARK)
        
        // 重新初始化
        ThemeEngine.initialize(context)
        
        // 验证主题仍然保持
        val mode = ThemeEngine.getCurrentThemeMode()
        assertEquals(ThemeMode.DARK, mode)
    }
    
    @Test
    fun `ColorScheme DEFAULT_LIGHT should have valid colors`() {
        val scheme = ColorScheme.DEFAULT_LIGHT
        
        assertNotEquals(0, scheme.primary)
        assertNotEquals(0, scheme.onPrimary)
        assertNotEquals(0, scheme.surface)
        assertNotEquals(0, scheme.background)
        
        // 验证是浅色主题
        val brightness = calculateBrightness(scheme.background)
        assertTrue(brightness > 128)
    }
    
    @Test
    fun `ColorScheme DEFAULT_DARK should have valid colors`() {
        val scheme = ColorScheme.DEFAULT_DARK
        
        assertNotEquals(0, scheme.primary)
        assertNotEquals(0, scheme.onPrimary)
        assertNotEquals(0, scheme.surface)
        assertNotEquals(0, scheme.background)
        
        // 验证是深色主题
        val brightness = calculateBrightness(scheme.background)
        assertTrue(brightness < 128)
    }
    
    @Test
    fun `ColorScheme DEFAULT_AMOLED should use pure black`() {
        val scheme = ColorScheme.DEFAULT_AMOLED
        
        assertEquals(0xFF000000.toInt(), scheme.background)
        assertEquals(0xFF000000.toInt(), scheme.surface)
    }
    
    private fun calculateBrightness(color: Int): Int {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return (r * 299 + g * 587 + b * 114) / 1000
    }
}
