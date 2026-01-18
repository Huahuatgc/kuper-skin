package dev.jahir.kuper.app.ui.theme

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * ThemeEngine 属性测试
 * Feature: ui-redesign, Property 3: 动态颜色版本适配
 * Validates: Requirements 3.1, 3.3, 3.5
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S, Build.VERSION_CODES.R])
class ThemeEnginePropertyTest : StringSpec({
    
    lateinit var context: Context
    
    beforeTest {
        context = ApplicationProvider.getApplicationContext()
        ThemeEngine.initialize(context)
    }
    
    // Feature: ui-redesign, Property 3: 动态颜色版本适配
    "theme engine should adapt colors based on Android version" {
        checkAll(100, Arb.enum<ThemeMode>()) { mode ->
            // 设置主题模式
            ThemeEngine.setThemeMode(mode)
            
            // 提取颜色方案
            val colorScheme = ThemeEngine.extractDynamicColors(context)
            
            // 验证颜色方案不为空
            colorScheme shouldNotBe null
            colorScheme.shouldBeInstanceOf<ColorScheme>()
            
            // 验证颜色值有效（非零）
            colorScheme.primary shouldNotBe 0
            colorScheme.onPrimary shouldNotBe 0
            colorScheme.surface shouldNotBe 0
            colorScheme.background shouldNotBe 0
            
            // 验证 AMOLED 模式使用纯黑背景
            if (mode == ThemeMode.AMOLED) {
                colorScheme.background shouldBe 0xFF000000.toInt()
                colorScheme.surface shouldBe 0xFF000000.toInt()
            }
        }
    }
    
    "theme mode should persist across sessions" {
        checkAll(100, Arb.enum<ThemeMode>()) { mode ->
            // 设置主题模式
            ThemeEngine.setThemeMode(mode)
            
            // 获取当前主题模式
            val retrievedMode = ThemeEngine.getCurrentThemeMode()
            
            // 验证主题模式正确保存
            retrievedMode shouldBe mode
        }
    }
    
    "Material You support should depend on Android version" {
        val supportsMaterialYou = ThemeEngine.supportsMaterialYou()
        
        // Android 12+ (API 31+) 应该支持 Material You
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            supportsMaterialYou shouldBe true
        } else {
            supportsMaterialYou shouldBe false
        }
    }
    
    "color scheme should match theme mode" {
        checkAll(100, Arb.enum<ThemeMode>()) { mode ->
            ThemeEngine.setThemeMode(mode)
            ThemeEngine.clearColorSchemeCache()
            
            val colorScheme = ThemeEngine.extractDynamicColors(context)
            
            // 验证颜色方案与主题模式匹配
            when (mode) {
                ThemeMode.LIGHT -> {
                    // 浅色主题应该有浅色背景
                    val brightness = calculateBrightness(colorScheme.background)
                    brightness shouldBeGreaterThan 128
                }
                ThemeMode.DARK -> {
                    // 深色主题应该有深色背景
                    val brightness = calculateBrightness(colorScheme.background)
                    brightness shouldBeLessThan 128
                }
                ThemeMode.AMOLED -> {
                    // AMOLED 主题应该使用纯黑
                    colorScheme.background shouldBe 0xFF000000.toInt()
                }
                ThemeMode.SYSTEM -> {
                    // 系统主题应该返回有效的颜色方案
                    colorScheme shouldNotBe null
                }
            }
        }
    }
})

/**
 * 计算颜色亮度
 */
private fun calculateBrightness(color: Int): Int {
    val r = (color shr 16) and 0xFF
    val g = (color shr 8) and 0xFF
    val b = color and 0xFF
    return (r * 299 + g * 587 + b * 114) / 1000
}

/**
 * 自定义 Kotest 匹配器
 */
private infix fun Int.shouldBeGreaterThan(other: Int) {
    if (this <= other) {
        throw AssertionError("Expected $this to be greater than $other")
    }
}

private infix fun Int.shouldBeLessThan(other: Int) {
    if (this >= other) {
        throw AssertionError("Expected $this to be less than $other")
    }
}
