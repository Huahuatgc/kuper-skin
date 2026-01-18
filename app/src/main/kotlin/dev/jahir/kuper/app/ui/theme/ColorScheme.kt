package dev.jahir.kuper.app.ui.theme

import androidx.annotation.ColorInt

/**
 * 颜色方案数据类
 * 包含应用主题的所有颜色定义
 */
data class ColorScheme(
    @ColorInt val primary: Int,
    @ColorInt val onPrimary: Int,
    @ColorInt val primaryContainer: Int,
    @ColorInt val onPrimaryContainer: Int,
    @ColorInt val secondary: Int,
    @ColorInt val onSecondary: Int,
    @ColorInt val secondaryContainer: Int,
    @ColorInt val onSecondaryContainer: Int,
    @ColorInt val surface: Int,
    @ColorInt val onSurface: Int,
    @ColorInt val background: Int,
    @ColorInt val onBackground: Int
) {
    companion object {
        /**
         * 默认浅色主题配色方案
         */
        val DEFAULT_LIGHT = ColorScheme(
            primary = 0xFF6750A4.toInt(),
            onPrimary = 0xFFFFFFFF.toInt(),
            primaryContainer = 0xFFEADDFF.toInt(),
            onPrimaryContainer = 0xFF21005D.toInt(),
            secondary = 0xFF625B71.toInt(),
            onSecondary = 0xFFFFFFFF.toInt(),
            secondaryContainer = 0xFFE8DEF8.toInt(),
            onSecondaryContainer = 0xFF1D192B.toInt(),
            surface = 0xFFFEF7FF.toInt(),
            onSurface = 0xFF1D1B20.toInt(),
            background = 0xFFFEF7FF.toInt(),
            onBackground = 0xFF1D1B20.toInt()
        )
        
        /**
         * 默认深色主题配色方案
         */
        val DEFAULT_DARK = ColorScheme(
            primary = 0xFFD0BCFF.toInt(),
            onPrimary = 0xFF381E72.toInt(),
            primaryContainer = 0xFF4F378B.toInt(),
            onPrimaryContainer = 0xFFEADDFF.toInt(),
            secondary = 0xFFCCC2DC.toInt(),
            onSecondary = 0xFF332D41.toInt(),
            secondaryContainer = 0xFF4A4458.toInt(),
            onSecondaryContainer = 0xFFE8DEF8.toInt(),
            surface = 0xFF1D1B20.toInt(),
            onSurface = 0xFFE6E0E9.toInt(),
            background = 0xFF1D1B20.toInt(),
            onBackground = 0xFFE6E0E9.toInt()
        )
        
        /**
         * AMOLED 纯黑主题配色方案
         */
        val DEFAULT_AMOLED = DEFAULT_DARK.copy(
            surface = 0xFF000000.toInt(),
            background = 0xFF000000.toInt()
        )
    }
}
