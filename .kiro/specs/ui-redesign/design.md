# Design Document: Kuper UI Redesign

## Overview

本设计文档描述了 Kuper Android 应用的 UI 重设计方案。重设计的核心目标是将当前简单的深色主题界面升级为符合 Material Design 3 规范的现代化界面，提供流畅的动画、动态颜色支持和优化的用户体验。

设计采用模块化架构，将 UI 组件、主题系统、动画引擎和资源管理分离，确保代码的可维护性和可扩展性。

## Architecture

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Activities  │  │  Fragments   │  │   Adapters   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────────────────────────────────────┐
│                      UI Components                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ TemplateCard │  │  AppBarView  │  │ NavigationBar│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────────────────────────────────────┐
│                    Theme & Animation                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ ThemeEngine  │  │AnimationMgr  │  │  ColorUtils  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 设计原则

1. **Material Design 3 优先**: 遵循 Google 最新的设计规范
2. **性能优化**: 使用硬件加速、图片缓存和懒加载
3. **响应式设计**: 适配不同屏幕尺寸和方向
4. **可访问性**: 支持 TalkBack、大字体和高对比度
5. **向后兼容**: 在旧版本 Android 上提供降级方案

## Components and Interfaces

### 1. TemplateCardView (自定义 View)

现代化的模板卡片组件，替代当前简单的 CardView。


**接口定义**:
```kotlin
class TemplateCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    
    // 预览图片
    private val previewImageView: ImageView
    
    // 模板标题
    private val titleTextView: TextView
    
    // 类型标签
    private val typeChip: Chip
    
    // 操作按钮
    private val actionButton: ImageButton
    
    // 设置模板数据
    fun setTemplate(template: KLWPTemplate)
    
    // 播放点击动画
    fun playClickAnimation()
    
    // 设置选择状态
    fun setSelected(selected: Boolean, animated: Boolean = true)
}
```

**关键特性**:
- 16dp 圆角设计
- 9:16 宽高比的预览图
- 柔和阴影 (elevation 4dp)
- 点击缩放动画 (scale 0.95)
- 支持选择模式

### 2. ThemeEngine (主题管理器)

负责管理应用主题和动态颜色。

**接口定义**:
```kotlin
object ThemeEngine {
    
    // 初始化主题引擎
    fun initialize(context: Context)
    
    // 获取当前主题模式
    fun getCurrentThemeMode(): ThemeMode
    
    // 设置主题模式
    fun setThemeMode(mode: ThemeMode)
    
    // 是否支持 Material You
    fun supportsMaterialYou(): Boolean
    
    // 提取动态颜色
    fun extractDynamicColors(context: Context): ColorScheme?
    
    // 应用主题到 Activity
    fun applyTheme(activity: Activity)
}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM, AMOLED
}

data class ColorScheme(
    val primary: Int,
    val onPrimary: Int,
    val primaryContainer: Int,
    val secondary: Int,
    val surface: Int,
    val background: Int
)
```


**关键特性**:
- Android 12+ 使用 DynamicColors API
- Android 11- 使用预设配色方案
- 支持实时主题切换
- 持久化用户偏好

### 3. AnimationManager (动画管理器)

统一管理应用内的所有动画效果。

**接口定义**:
```kotlin
object AnimationManager {
    
    // 卡片点击动画
    fun playCardClickAnimation(view: View, onEnd: () -> Unit = {})
    
    // 共享元素过渡
    fun createSharedElementTransition(): Transition
    
    // 列表项交错动画
    fun playStaggeredAnimation(views: List<View>, delayMs: Long = 50)
    
    // 淡入淡出动画
    fun crossFade(fromView: View, toView: View, durationMs: Long = 300)
    
    // 下拉刷新动画
    fun createRefreshAnimation(): Animation
}
```

**动画参数**:
- 标准动画时长: 200ms
- 长动画时长: 300ms
- 插值器: FastOutSlowInInterpolator
- 交错延迟: 50ms

### 4. TemplateGridAdapter (网格适配器)

优化的 RecyclerView 适配器，支持响应式布局。

**接口定义**:
```kotlin
class TemplateGridAdapter(
    private val onTemplateClick: (KLWPTemplate) -> Unit,
    private val onTemplateLongClick: (KLWPTemplate) -> Boolean
) : RecyclerView.Adapter<TemplateGridAdapter.ViewHolder>() {
    
    var templates: List<KLWPTemplate> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    
    var selectionMode: Boolean = false
    
    val selectedTemplates: Set<KLWPTemplate>
    
    fun toggleSelection(template: KLWPTemplate)
    
    fun clearSelection()
    
    class ViewHolder(val cardView: TemplateCardView) : 
        RecyclerView.ViewHolder(cardView)
}
```


### 5. ImageLoader (图片加载器)

基于 Coil 的图片加载封装，提供占位符和缓存。

**接口定义**:
```kotlin
object ImageLoader {
    
    // 加载模板预览图
    fun loadPreview(
        imageView: ImageView,
        url: String,
        placeholder: Drawable? = null,
        onSuccess: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    )
    
    // 预加载图片
    fun preload(context: Context, urls: List<String>)
    
    // 清除缓存
    fun clearCache(context: Context)
    
    // 获取缓存大小
    fun getCacheSize(context: Context): Long
}
```

**缓存策略**:
- 内存缓存: 最多 50 张图片
- 磁盘缓存: 最大 100MB
- 预加载: 提前 3 个位置

### 6. GradientBackgroundView (渐变背景)

动态渐变背景组件。

**接口定义**:
```kotlin
class GradientBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    
    // 设置渐变颜色
    fun setGradientColors(startColor: Int, endColor: Int)
    
    // 设置渐变方向
    fun setGradientOrientation(orientation: Orientation)
    
    // 动画过渡到新颜色
    fun animateToColors(startColor: Int, endColor: Int, durationMs: Long = 500)
    
    enum class Orientation {
        TOP_BOTTOM, BOTTOM_TOP, LEFT_RIGHT, DIAGONAL
    }
}
```

## Data Models

### KLWPTemplate (模板数据模型)

```kotlin
data class KLWPTemplate(
    val id: String,
    val name: String,
    val description: String,
    val previewUrl: String,
    val fileUrl: String,
    val type: TemplateType,
    val author: String,
    val version: String,
    val fileSize: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val tags: List<String> = emptyList()
)

enum class TemplateType {
    WALLPAPER, WIDGET, LOCKSCREEN, KOMPONENT
}
```


### UIState (UI 状态模型)

```kotlin
sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UIState<Nothing>()
    object Empty : UIState<Nothing>()
}
```

### LayoutConfig (布局配置)

```kotlin
data class LayoutConfig(
    val spanCount: Int,
    val itemSpacing: Int,
    val edgeSpacing: Int,
    val cardCornerRadius: Int,
    val cardElevation: Int
) {
    companion object {
        fun forScreenWidth(widthDp: Int, isTablet: Boolean): LayoutConfig {
            val spanCount = when {
                isTablet -> if (widthDp > 900) 4 else 3
                widthDp > 600 -> 3
                else -> 2
            }
            return LayoutConfig(
                spanCount = spanCount,
                itemSpacing = 12,
                edgeSpacing = 16,
                cardCornerRadius = 16,
                cardElevation = 4
            )
        }
    }
}
```

## Correctness Properties

*属性是关于系统应该满足的特征或行为的形式化陈述，它们在所有有效执行中都应该成立。属性是人类可读规范和机器可验证正确性保证之间的桥梁。*


### Property 1: 模板卡片视觉规范一致性

*对于任何* TemplateCardView 实例，该卡片应该具有 16dp 的圆角半径、4dp 的 elevation、9:16 的预览图宽高比，并且包含标题、类型标签和操作按钮这三个必需的子视图。

**Validates: Requirements 1.1, 1.2, 1.3, 1.4**

### Property 2: 响应式网格布局适配

*对于任何* 屏幕配置（宽度、方向、设备类型），Template_Grid 的列数应该根据以下规则计算：手机竖屏为 2 列，手机横屏为 3 列，平板为 3-4 列，并且卡片间距始终为 12dp。

**Validates: Requirements 2.1, 2.2, 2.3, 2.5**

### Property 3: 动态颜色版本适配

*对于任何* Android 版本，Theme_Engine 应该在 Android 12+ 上提取系统动态颜色，在 Android 11- 上使用预设品牌配色，并且颜色方案应该正确应用到背景、卡片和按钮组件。

**Validates: Requirements 3.1, 3.3, 3.5**

### Property 4: 主题切换响应性

*对于任何* 主题模式切换（浅色/深色/AMOLED），Theme_Engine 应该保持动态颜色的适配，并且在系统主题颜色改变时自动更新应用配色方案。

**Validates: Requirements 3.2, 3.4**

### Property 5: 视觉层次元素存在性

*对于任何* 主界面实例，应该包含渐变背景、半透明 App Bar（alpha < 1.0）、具有适当 elevation 的底部导航栏，并且分类标题使用更大的字体（24sp）和醒目颜色。

**Validates: Requirements 4.1, 4.2, 4.4, 4.5**

### Property 6: 滚动响应式阴影

*对于任何* 滚动事件，当内容向上滚动时，App Bar 的 elevation 应该增加以显示动态阴影；当滚动到顶部时，elevation 应该恢复到初始值。

**Validates: Requirements 4.3**

### Property 7: 卡片点击动画规范

*对于任何* 模板卡片点击事件，应该触发一个 200ms 的缩放动画，目标缩放比例为 0.95，使用 FastOutSlowInInterpolator 插值器。

**Validates: Requirements 1.5, 5.1**

### Property 8: 列表项交错动画

*对于任何* 首次加载的模板列表，每个卡片应该按顺序播放淡入动画，相邻卡片之间的延迟为 50ms，形成交错效果。

**Validates: Requirements 5.5**


### Property 9: 标签页切换过渡

*对于任何* 标签页切换操作，应该使用淡入淡出过渡效果，旧视图淡出和新视图淡入应该同时进行，总时长为 300ms。

**Validates: Requirements 5.3**

### Property 10: 排版规范一致性

*对于任何* 文本元素，应该遵循以下字体规范：模板标题使用 16sp Medium，分类标题使用 24sp Bold，描述文本使用 14sp Regular，并且所有文本与背景的对比度比率至少为 4.5:1。

**Validates: Requirements 6.1, 6.2, 6.3, 6.4**

### Property 11: 内容区域边距一致性

*对于任何* 内容区域，左右边距应该始终为 16dp，确保内容不会紧贴屏幕边缘。

**Validates: Requirements 6.5**

### Property 12: 图片预加载策略

*对于任何* 列表滚动操作，当用户向下滚动时，应该预加载当前可见位置之后 3 个位置的图片；向上滚动时，应该预加载之前 3 个位置的图片。

**Validates: Requirements 7.1**

### Property 13: 图片加载状态处理

*对于任何* 图片加载操作，在加载中状态应该显示品牌色占位符，加载成功后使用淡入动画显示图片，加载失败时显示错误占位图，并且重复访问时应该从缓存加载而非重新请求。

**Validates: Requirements 7.2, 7.3, 7.4, 7.5**

### Property 14: 操作按钮位置规范

*对于任何* TemplateCardView，操作按钮应该定位在卡片的右下角，使用 ConstraintLayout 的 end 和 bottom 约束。

**Validates: Requirements 8.1**

### Property 15: 长按选择模式

*对于任何* 模板卡片长按事件，应该进入选择模式，卡片上显示复选框，顶部显示批量操作工具栏，并且可以通过点击其他卡片来切换选择状态。

**Validates: Requirements 8.3, 8.4**

### Property 16: 底部导航视觉规范

*对于任何* BottomNavigationView 实例，应该同时显示图标和文字标签（labelVisibilityMode = LABELED），选中项应该高亮显示，点击时应该有波纹效果，并且在滚动时保持固定在底部。

**Validates: Requirements 10.1, 10.2, 10.3, 10.4**

### Property 17: 小屏设备导航适配

*对于任何* 屏幕宽度小于 360dp 的设备，BottomNavigationView 应该自动切换到仅图标模式（labelVisibilityMode = UNLABELED），以节省空间。

**Validates: Requirements 10.5**

## Error Handling

### 图片加载错误


**场景**: 网络不可用或图片 URL 无效

**处理策略**:
1. 显示错误占位图（带有图标的灰色背景）
2. 记录错误日志但不崩溃
3. 提供重试机制（点击占位图重新加载）
4. 降级到本地缓存（如果存在）

**实现**:
```kotlin
ImageLoader.loadPreview(
    imageView = previewImage,
    url = template.previewUrl,
    placeholder = R.drawable.placeholder_loading,
    onError = {
        previewImage.setImageResource(R.drawable.placeholder_error)
        previewImage.setOnClickListener { 
            // 重试加载
            loadPreviewImage(template)
        }
    }
)
```

### 主题切换错误

**场景**: 动态颜色提取失败或主题资源缺失

**处理策略**:
1. 捕获异常并记录
2. 回退到默认主题配色
3. 通知用户（Toast 或 Snackbar）
4. 禁用 Material You 功能

**实现**:
```kotlin
try {
    val colors = ThemeEngine.extractDynamicColors(context)
    applyColorScheme(colors)
} catch (e: Exception) {
    Log.e(TAG, "Failed to extract dynamic colors", e)
    applyColorScheme(ColorScheme.DEFAULT)
    showSnackbar("动态颜色不可用，使用默认主题")
}
```

### 动画性能问题

**场景**: 低端设备上动画卡顿

**处理策略**:
1. 检测设备性能等级
2. 在低端设备上禁用复杂动画
3. 使用简化版动画（减少时长和效果）
4. 提供用户设置选项

**实现**:
```kotlin
object AnimationManager {
    private val isLowEndDevice: Boolean by lazy {
        ActivityManager.isLowRamDevice() || 
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M
    }
    
    fun playCardClickAnimation(view: View) {
        if (isLowEndDevice) {
            // 简化动画：仅改变 alpha
            view.animate().alpha(0.7f).setDuration(100).start()
        } else {
            // 完整动画：缩放 + alpha
            view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .alpha(0.9f)
                .setDuration(200)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()
        }
    }
}
```

### 布局测量错误

**场景**: 自定义 View 测量或布局计算错误

**处理策略**:
1. 添加边界检查
2. 使用安全的默认值
3. 记录警告日志
4. 避免负数或过大的尺寸

**实现**:
```kotlin
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec).coerceAtLeast(0)
    val height = (width * 16 / 9).coerceAtLeast(0)
    
    if (width <= 0 || height <= 0) {
        Log.w(TAG, "Invalid dimensions: width=$width, height=$height")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        return
    }
    
    setMeasuredDimension(width, height)
}
```

## Testing Strategy

### 测试方法概述

本项目采用**双重测试策略**，结合单元测试和属性测试，确保全面的代码覆盖和正确性验证：


**单元测试 (Unit Tests)**:
- 验证特定示例和边界情况
- 测试组件集成点
- 验证错误处理逻辑
- 测试 UI 状态转换

**属性测试 (Property-Based Tests)**:
- 验证跨所有输入的通用属性
- 通过随机化实现全面的输入覆盖
- 每个测试最少 100 次迭代
- 使用 Kotest Property Testing 框架

### 测试框架和工具

**核心测试框架**:
- **JUnit 5**: 单元测试框架
- **Kotest**: Kotlin 测试框架，支持属性测试
- **MockK**: Kotlin mocking 库
- **Robolectric**: Android 单元测试（无需模拟器）
- **Espresso**: UI 测试框架

**属性测试库**:
```gradle
dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-assertions-core:5.5.4")
    testImplementation("io.kotest:kotest-property:5.5.4")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.robolectric:robolectric:4.10")
}
```

### 属性测试配置

每个属性测试必须：
1. 运行最少 **100 次迭代**
2. 使用注释引用设计文档中的属性
3. 标签格式: `// Feature: ui-redesign, Property {number}: {property_text}`

**示例配置**:
```kotlin
class TemplateCardViewPropertyTest : StringSpec({
    
    // Feature: ui-redesign, Property 1: 模板卡片视觉规范一致性
    "template card should have consistent visual specifications" {
        checkAll(100, Arb.templateCard()) { cardView ->
            cardView.radius shouldBe 16.dp
            cardView.cardElevation shouldBe 4.dp
            cardView.previewImage.aspectRatio shouldBe (9f / 16f)
            cardView.findViewById<TextView>(R.id.title) shouldNotBe null
            cardView.findViewById<Chip>(R.id.typeChip) shouldNotBe null
            cardView.findViewById<ImageButton>(R.id.actionButton) shouldNotBe null
        }
    }
})
```

### 单元测试策略

**TemplateCardView 测试**:
```kotlin
class TemplateCardViewTest {
    
    @Test
    fun `card displays template information correctly`() {
        val template = KLWPTemplate(
            id = "test-1",
            name = "Test Template",
            type = TemplateType.WALLPAPER,
            previewUrl = "https://example.com/preview.jpg"
        )
        
        val cardView = TemplateCardView(context)
        cardView.setTemplate(template)
        
        assertEquals("Test Template", cardView.titleTextView.text)
        assertEquals("WALLPAPER", cardView.typeChip.text)
    }
    
    @Test
    fun `card click triggers scale animation`() {
        val cardView = TemplateCardView(context)
        cardView.performClick()
        
        // 验证动画已启动
        assertNotNull(cardView.scaleX)
        assertEquals(0.95f, cardView.scaleX, 0.01f)
    }
}
```

**ThemeEngine 测试**:
```kotlin
class ThemeEngineTest {
    
    @Test
    fun `extracts dynamic colors on Android 12+`() {
        // 模拟 Android 12+ 环境
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        
        val colors = ThemeEngine.extractDynamicColors(context)
        
        assertNotNull(colors)
        assertTrue(colors.primary != 0)
    }
    
    @Test
    fun `uses preset colors on Android 11-`() {
        // 模拟 Android 11- 环境
        assumeTrue(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
        
        val colors = ThemeEngine.extractDynamicColors(context)
        
        assertEquals(ColorScheme.DEFAULT, colors)
    }
}
```


**AnimationManager 测试**:
```kotlin
class AnimationManagerTest {
    
    @Test
    fun `card click animation has correct duration`() {
        val view = View(context)
        var animationEnded = false
        
        AnimationManager.playCardClickAnimation(view) {
            animationEnded = true
        }
        
        // 等待动画完成
        Thread.sleep(250)
        assertTrue(animationEnded)
    }
    
    @Test
    fun `staggered animation applies correct delays`() {
        val views = List(5) { View(context) }
        
        AnimationManager.playStaggeredAnimation(views, delayMs = 50)
        
        // 验证每个 view 的动画延迟
        views.forEachIndexed { index, view ->
            val expectedDelay = index * 50L
            // 验证动画延迟（需要访问动画对象）
        }
    }
}
```

**LayoutConfig 测试**:
```kotlin
class LayoutConfigTest {
    
    @Test
    fun `calculates correct span count for phone portrait`() {
        val config = LayoutConfig.forScreenWidth(widthDp = 360, isTablet = false)
        assertEquals(2, config.spanCount)
    }
    
    @Test
    fun `calculates correct span count for phone landscape`() {
        val config = LayoutConfig.forScreenWidth(widthDp = 640, isTablet = false)
        assertEquals(3, config.spanCount)
    }
    
    @Test
    fun `calculates correct span count for tablet`() {
        val config = LayoutConfig.forScreenWidth(widthDp = 1024, isTablet = true)
        assertEquals(4, config.spanCount)
    }
}
```

### 属性测试示例

**Property 2: 响应式网格布局适配**:
```kotlin
// Feature: ui-redesign, Property 2: 响应式网格布局适配
"grid layout adapts to screen configuration" {
    checkAll(100, 
        Arb.int(320..2048), // 屏幕宽度
        Arb.boolean()       // 是否平板
    ) { widthDp, isTablet ->
        val config = LayoutConfig.forScreenWidth(widthDp, isTablet)
        
        // 验证列数规则
        when {
            isTablet && widthDp > 900 -> config.spanCount shouldBe 4
            isTablet -> config.spanCount shouldBe 3
            widthDp > 600 -> config.spanCount shouldBe 3
            else -> config.spanCount shouldBe 2
        }
        
        // 验证间距
        config.itemSpacing shouldBe 12
    }
}
```

**Property 7: 卡片点击动画规范**:
```kotlin
// Feature: ui-redesign, Property 7: 卡片点击动画规范
"card click animation follows specifications" {
    checkAll(100, Arb.view()) { view ->
        val animator = AnimationManager.createCardClickAnimator(view)
        
        animator.duration shouldBe 200L
        animator.interpolator shouldBe instanceOf<FastOutSlowInInterpolator>()
        
        // 验证缩放目标值
        val scaleXAnimator = animator.childAnimations
            .find { it.propertyName == "scaleX" }
        scaleXAnimator?.animatedValue shouldBe 0.95f
    }
}
```

**Property 10: 排版规范一致性**:
```kotlin
// Feature: ui-redesign, Property 10: 排版规范一致性
"typography follows consistent specifications" {
    checkAll(100, Arb.textElement()) { element ->
        when (element.type) {
            TextType.TEMPLATE_TITLE -> {
                element.textSize shouldBe 16.sp
                element.typeface.weight shouldBe Typeface.MEDIUM
            }
            TextType.CATEGORY_TITLE -> {
                element.textSize shouldBe 24.sp
                element.typeface.weight shouldBe Typeface.BOLD
            }
            TextType.DESCRIPTION -> {
                element.textSize shouldBe 14.sp
                element.typeface.weight shouldBe Typeface.NORMAL
            }
        }
        
        // 验证对比度
        val contrastRatio = calculateContrastRatio(
            element.textColor, 
            element.backgroundColor
        )
        contrastRatio shouldBeGreaterThan 4.5f
    }
}
```

### UI 测试 (Espresso)

**模板列表显示测试**:
```kotlin
@RunWith(AndroidJUnit4::class)
class TemplateListUITest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun templateCardsDisplayCorrectly() {
        // 验证列表显示
        onView(withId(R.id.templateRecyclerView))
            .check(matches(isDisplayed()))
        
        // 验证第一个卡片
        onView(withId(R.id.templateRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        
        onView(withText("HelloWorld"))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun cardClickTriggersAnimation() {
        // 点击卡片
        onView(withText("HelloWorld"))
            .perform(click())
        
        // 验证动画效果（通过检查 scaleX/scaleY）
        onView(withText("HelloWorld"))
            .check { view, _ ->
                assertTrue(view.scaleX < 1.0f)
            }
    }
}
```

### 测试覆盖率目标

- **单元测试覆盖率**: 最低 80%
- **属性测试**: 覆盖所有 17 个正确性属性
- **UI 测试**: 覆盖关键用户流程
- **集成测试**: 测试组件间交互

### 持续集成

在 CI/CD 流程中自动运行所有测试：

```yaml
# .github/workflows/test.yml
name: Run Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run unit tests
        run: ./gradlew test
      - name: Run property tests
        run: ./gradlew testDebugUnitTest --tests "*PropertyTest"
      - name: Generate coverage report
        run: ./gradlew jacocoTestReport
```

## Implementation Notes

### 资源文件组织

```
res/
├── values/
│   ├── colors.xml          # 颜色定义
│   ├── dimens.xml          # 尺寸定义
│   ├── styles.xml          # 样式定义
│   └── themes.xml          # 主题定义
├── values-v31/
│   └── themes.xml          # Material You 主题
├── drawable/
│   ├── bg_gradient.xml     # 渐变背景
│   ├── placeholder_loading.xml
│   └── placeholder_error.xml
├── layout/
│   ├── item_template_card.xml
│   ├── fragment_template_list.xml
│   └── view_empty_state.xml
└── anim/
    ├── card_click.xml
    ├── fade_in.xml
    └── stagger_fade_in.xml
```

### 性能优化建议

1. **图片加载**: 使用 Coil 的内存和磁盘缓存
2. **RecyclerView**: 使用 DiffUtil 优化列表更新
3. **动画**: 使用硬件加速层 (setLayerType)
4. **布局**: 减少嵌套层级，使用 ConstraintLayout
5. **主题**: 缓存颜色提取结果，避免重复计算

### 可访问性考虑

1. **内容描述**: 为所有图片和按钮添加 contentDescription
2. **触摸目标**: 确保最小 48dp × 48dp
3. **对比度**: 遵循 WCAG 2.1 AA 标准 (4.5:1)
4. **字体缩放**: 支持系统字体大小设置
5. **TalkBack**: 测试屏幕阅读器兼容性

### 向后兼容性

- **Android 12+**: 完整 Material You 支持
- **Android 8-11**: 预设主题，部分动画
- **Android 5-7**: 基础功能，简化动画
- **降级策略**: 检测 API 级别并提供替代方案

