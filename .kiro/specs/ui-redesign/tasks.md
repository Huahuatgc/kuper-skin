# Implementation Plan: Kuper UI Redesign

## Overview

本实施计划将 Kuper 应用的 UI 从传统深色主题升级为现代化的 Material Design 3 界面。实施分为多个阶段，每个阶段都包含核心功能实现和相应的测试任务。我们将采用增量开发方式，确保每个组件都经过充分测试后再集成到主应用中。

## Tasks

- [x] 1. 项目基础设置和依赖配置
  - 添加 Material Design 3 依赖
  - 添加 Coil 图片加载库
  - 添加 Kotest 属性测试框架
  - 配置测试环境（JUnit 5, MockK, Robolectric）
  - 创建基础资源文件结构（colors.xml, dimens.xml, themes.xml）
  - _Requirements: 所有需求的基础_

- [x] 2. 实现 ThemeEngine 主题管理系统
  - [x] 2.1 创建 ThemeEngine 单例对象和 ColorScheme 数据类
    - 实现 ThemeMode 枚举（LIGHT, DARK, SYSTEM, AMOLED）
    - 实现 ColorScheme 数据类包含所有主题颜色
    - 实现 initialize() 和 getCurrentThemeMode() 方法
    - _Requirements: 3.1, 3.5_

  - [x] 2.2 实现动态颜色提取功能
    - 实现 supportsMaterialYou() 检测 Android 版本
    - 实现 extractDynamicColors() 使用 DynamicColors API (Android 12+)
    - 实现降级方案使用预设配色（Android 11-）
    - _Requirements: 3.1, 3.5_

  - [x] 2.3 实现主题应用和切换功能
    - 实现 setThemeMode() 保存用户偏好到 SharedPreferences
    - 实现 applyTheme() 应用主题到 Activity
    - 实现主题变化监听器
    - _Requirements: 3.2, 3.4_

  - [x] 2.4 编写 ThemeEngine 属性测试
    - **Property 3: 动态颜色版本适配**
    - **Validates: Requirements 3.1, 3.3, 3.5**

  - [x] 2.5 编写 ThemeEngine 单元测试
    - 测试 Android 12+ 动态颜色提取
    - 测试 Android 11- 预设颜色使用
    - 测试主题切换和持久化
    - _Requirements: 3.1, 3.2, 3.4, 3.5_

- [x] 3. 创建 TemplateCardView 自定义组件
  - [x] 3.1 实现 TemplateCardView 基础布局
    - 继承 MaterialCardView 并设置 16dp 圆角
    - 添加 ImageView（预览图）、TextView（标题）、Chip（类型标签）、ImageButton（操作按钮）
    - 实现 9:16 宽高比的预览图布局
    - 设置 4dp elevation 和柔和阴影
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [x] 3.2 实现 setTemplate() 数据绑定方法
    - 绑定模板名称到 titleTextView
    - 绑定模板类型到 typeChip
    - 使用 ImageLoader 加载预览图
    - 设置操作按钮点击监听器
    - _Requirements: 1.2, 1.3_

  - [x] 3.3 实现卡片点击动画
    - 实现 playClickAnimation() 方法
    - 使用 200ms 缩放动画（scale 0.95）
    - 使用 FastOutSlowInInterpolator 插值器
    - _Requirements: 1.5, 5.1_

  - [x] 3.4 实现选择模式功能
    - 实现 setSelected() 方法显示/隐藏复选框
    - 添加选中状态的视觉反馈（边框高亮）
    - 实现选中状态动画过渡
    - _Requirements: 8.3_

  - [x] 3.5 编写 TemplateCardView 属性测试
    - **Property 1: 模板卡片视觉规范一致性**
    - **Validates: Requirements 1.1, 1.2, 1.3, 1.4**

  - [x] 3.6 编写 TemplateCardView 单元测试
    - 测试卡片显示模板信息
    - 测试点击动画触发
    - 测试选择模式切换
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 8.3_

- [x] 4. 实现 AnimationManager 动画管理器
  - [x] 4.1 创建 AnimationManager 单例对象
    - 实现 playCardClickAnimation() 卡片点击动画
    - 实现 crossFade() 淡入淡出动画
    - 实现 playStaggeredAnimation() 交错动画
    - 定义标准动画参数（时长、插值器）
    - _Requirements: 5.1, 5.3, 5.5_

  - [x] 4.2 实现共享元素过渡动画
    - 实现 createSharedElementTransition() 方法
    - 配置过渡动画参数
    - _Requirements: 5.2_

  - [x] 4.3 实现下拉刷新动画
    - 实现 createRefreshAnimation() 自定义刷新动画
    - _Requirements: 5.4_

  - [x] 4.4 编写 AnimationManager 属性测试
    - **Property 7: 卡片点击动画规范**
    - **Property 8: 列表项交错动画**
    - **Property 9: 标签页切换过渡**
    - **Validates: Requirements 1.5, 5.1, 5.3, 5.5**

  - [x] 4.5 编写 AnimationManager 单元测试
    - 测试动画时长和插值器
    - 测试动画回调执行
    - 测试低端设备降级
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 5. Checkpoint - 核心组件测试验证
  - 确保 ThemeEngine、TemplateCardView 和 AnimationManager 所有测试通过
  - 验证组件可以独立工作
  - 如有问题请向用户反馈

- [ ] 6. 实现 ImageLoader 图片加载器
  - [ ] 6.1 创建 ImageLoader 单例对象
    - 配置 Coil ImageLoader 实例
    - 设置内存缓存（最多 50 张）
    - 设置磁盘缓存（最大 100MB）
    - _Requirements: 7.5_

  - [ ] 6.2 实现 loadPreview() 图片加载方法
    - 实现占位符显示（加载中）
    - 实现淡入动画（加载成功）
    - 实现错误占位图（加载失败）
    - 添加成功和失败回调
    - _Requirements: 7.2, 7.3, 7.4_

  - [ ] 6.3 实现图片预加载功能
    - 实现 preload() 方法批量预加载
    - 实现预加载策略（提前 3 个位置）
    - _Requirements: 7.1_

  - [ ] 6.4 实现缓存管理功能
    - 实现 clearCache() 清除缓存
    - 实现 getCacheSize() 获取缓存大小
    - _Requirements: 7.5_

  - [ ] 6.5 编写 ImageLoader 属性测试
    - **Property 12: 图片预加载策略**
    - **Property 13: 图片加载状态处理**
    - **Validates: Requirements 7.1, 7.2, 7.3, 7.4, 7.5**

  - [ ] 6.6 编写 ImageLoader 单元测试
    - 测试图片加载成功流程
    - 测试图片加载失败处理
    - 测试缓存命中和未命中
    - 测试预加载逻辑
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_


- [ ] 7. 实现 LayoutConfig 和响应式布局
  - [ ] 7.1 创建 LayoutConfig 数据类
    - 实现 forScreenWidth() 静态方法
    - 实现列数计算逻辑（手机 2-3 列，平板 3-4 列）
    - 定义间距和圆角参数
    - _Requirements: 2.1, 2.2, 2.3, 2.5_

  - [ ] 7.2 编写 LayoutConfig 属性测试
    - **Property 2: 响应式网格布局适配**
    - **Validates: Requirements 2.1, 2.2, 2.3, 2.5**

  - [ ] 7.3 编写 LayoutConfig 单元测试
    - 测试手机竖屏列数（2 列）
    - 测试手机横屏列数（3 列）
    - 测试平板列数（3-4 列）
    - 测试间距参数
    - _Requirements: 2.1, 2.2, 2.3, 2.5_

- [ ] 8. 实现 TemplateGridAdapter 网格适配器
  - [ ] 8.1 创建 TemplateGridAdapter 类
    - 继承 RecyclerView.Adapter
    - 实现 ViewHolder 使用 TemplateCardView
    - 实现 onCreateViewHolder 和 onBindViewHolder
    - 添加模板点击和长按监听器
    - _Requirements: 1.1, 8.1_

  - [ ] 8.2 实现选择模式功能
    - 添加 selectionMode 标志
    - 实现 toggleSelection() 切换选择状态
    - 实现 clearSelection() 清除所有选择
    - 维护 selectedTemplates 集合
    - _Requirements: 8.3, 8.4_

  - [ ] 8.3 实现 DiffUtil 优化列表更新
    - 创建 TemplateDiffCallback 类
    - 使用 DiffUtil.calculateDiff() 计算差异
    - 优化 notifyDataSetChanged() 调用
    - _Requirements: 2.4（性能优化）_

  - [ ] 8.4 编写 TemplateGridAdapter 单元测试
    - 测试适配器数据绑定
    - 测试选择模式切换
    - 测试 DiffUtil 更新
    - _Requirements: 8.3, 8.4_

- [ ] 9. 实现 GradientBackgroundView 渐变背景
  - [ ] 9.1 创建 GradientBackgroundView 自定义 View
    - 继承 View 并实现 onDraw()
    - 实现 setGradientColors() 设置渐变颜色
    - 实现 setGradientOrientation() 设置渐变方向
    - 使用 LinearGradient 绘制渐变
    - _Requirements: 4.1_

  - [ ] 9.2 实现颜色动画过渡
    - 实现 animateToColors() 方法
    - 使用 ValueAnimator 平滑过渡颜色
    - 设置 500ms 动画时长
    - _Requirements: 4.1_

  - [ ] 9.3 编写 GradientBackgroundView 单元测试
    - 测试渐变颜色设置
    - 测试渐变方向设置
    - 测试颜色动画过渡
    - _Requirements: 4.1_

- [ ] 10. 创建主题资源文件
  - [ ] 10.1 创建 colors.xml 颜色定义
    - 定义浅色主题颜色
    - 定义深色主题颜色
    - 定义 AMOLED 主题颜色
    - 定义品牌色和强调色
    - _Requirements: 3.5_

  - [ ] 10.2 创建 dimens.xml 尺寸定义
    - 定义卡片圆角半径（16dp）
    - 定义卡片 elevation（4dp）
    - 定义间距（12dp, 16dp）
    - 定义字体大小（14sp, 16sp, 24sp）
    - _Requirements: 1.1, 1.4, 2.5, 6.1, 6.2, 6.3, 6.5_

  - [ ] 10.3 创建 themes.xml 主题定义
    - 定义 MyApp.Default 浅色主题
    - 定义 MyApp.Default.Amoled 深色主题
    - 配置 Material Design 3 属性
    - _Requirements: 3.4, 3.5_

  - [ ] 10.4 创建 themes.xml (v31) Material You 主题
    - 定义 MyApp.Default.MaterialYou 主题
    - 定义 MyApp.Default.Amoled.MaterialYou 主题
    - 配置动态颜色属性
    - _Requirements: 3.1, 3.2, 3.3_

  - [ ] 10.5 创建 drawable 资源文件
    - 创建 bg_gradient.xml 渐变背景
    - 创建 placeholder_loading.xml 加载占位符
    - 创建 placeholder_error.xml 错误占位符
    - _Requirements: 4.1, 7.2, 7.4_

- [ ] 11. Checkpoint - 资源和布局验证
  - 确保所有资源文件正确创建
  - 验证主题可以正确应用
  - 验证颜色和尺寸定义符合设计规范
  - 如有问题请向用户反馈

- [ ] 12. 更新 MainActivity 集成新主题系统
  - [ ] 12.1 在 MainActivity.onCreate() 中初始化 ThemeEngine
    - 调用 ThemeEngine.initialize(this)
    - 调用 ThemeEngine.applyTheme(this)
    - _Requirements: 3.1, 3.2_

  - [ ] 12.2 添加主题切换功能
    - 在设置界面添加主题选择选项
    - 实现主题切换监听器
    - 切换后重启 Activity 应用新主题
    - _Requirements: 3.4_

  - [ ] 12.3 更新 Activity 主题配置
    - 修改 defaultTheme() 返回新主题
    - 修改 amoledTheme() 返回新主题
    - 修改 defaultMaterialYouTheme() 返回新主题
    - 修改 amoledMaterialYouTheme() 返回新主题
    - _Requirements: 3.1, 3.3, 3.4_

- [ ] 13. 创建 TemplateListFragment 模板列表界面
  - [ ] 13.1 创建 fragment_template_list.xml 布局文件
    - 添加 GradientBackgroundView 作为背景
    - 添加 RecyclerView 显示模板网格
    - 添加 SwipeRefreshLayout 支持下拉刷新
    - 添加空状态、加载状态、错误状态视图
    - _Requirements: 4.1, 9.1, 9.2_

  - [ ] 13.2 创建 TemplateListFragment 类
    - 初始化 RecyclerView 使用 GridLayoutManager
    - 根据屏幕宽度配置列数
    - 设置 TemplateGridAdapter
    - 添加 ItemDecoration 设置间距
    - _Requirements: 2.1, 2.2, 2.3, 2.5_

  - [ ] 13.3 实现模板数据加载
    - 从 ViewModel 观察模板数据
    - 处理 Loading、Success、Error、Empty 状态
    - 显示对应的状态视图
    - _Requirements: 9.1, 9.2, 9.3_

  - [ ] 13.4 实现下拉刷新功能
    - 配置 SwipeRefreshLayout
    - 使用 AnimationManager.createRefreshAnimation()
    - 实现刷新回调
    - _Requirements: 5.4_

  - [ ] 13.5 实现滚动监听和图片预加载
    - 添加 RecyclerView.OnScrollListener
    - 检测滚动方向和位置
    - 调用 ImageLoader.preload() 预加载图片
    - _Requirements: 7.1_

  - [ ] 13.6 实现列表项交错动画
    - 在首次加载时应用交错动画
    - 使用 AnimationManager.playStaggeredAnimation()
    - _Requirements: 5.5_

- [ ] 14. 实现 AppBar 和视觉层次
  - [ ] 14.1 创建半透明 AppBar
    - 设置 AppBar 背景为半透明
    - 添加模糊效果（使用 RenderScript 或 Blurry 库）
    - _Requirements: 4.2_

  - [ ] 14.2 实现滚动响应式阴影
    - 添加 AppBarLayout.OnOffsetChangedListener
    - 根据滚动偏移量动态调整 elevation
    - _Requirements: 4.3_

  - [ ] 14.3 编写 AppBar 属性测试
    - **Property 5: 视觉层次元素存在性**
    - **Property 6: 滚动响应式阴影**
    - **Validates: Requirements 4.2, 4.3, 4.4, 4.5**

- [ ] 15. 实现底部导航栏
  - [ ] 15.1 更新 BottomNavigationView 样式
    - 设置 labelVisibilityMode 为 LABELED
    - 设置图标大小为 24dp
    - 应用毛玻璃效果背景
    - 设置浮动 elevation
    - _Requirements: 4.5, 10.1_

  - [ ] 15.2 实现导航项选中动画
    - 添加选中状态监听器
    - 实现高亮动画
    - 添加波纹效果
    - _Requirements: 10.2, 10.3_

  - [ ] 15.3 实现响应式标签显示
    - 检测屏幕宽度
    - 在小屏设备（< 360dp）切换到仅图标模式
    - _Requirements: 10.5_

  - [ ] 15.4 编写底部导航属性测试
    - **Property 16: 底部导航视觉规范**
    - **Property 17: 小屏设备导航适配**
    - **Validates: Requirements 10.1, 10.2, 10.3, 10.4, 10.5**

- [ ] 16. 实现模板操作功能
  - [ ] 16.1 实现操作按钮菜单
    - 创建 PopupMenu 或 BottomSheetDialog
    - 添加"应用"、"分享"、"详情"选项
    - 实现菜单项点击处理
    - _Requirements: 8.2_

  - [ ] 16.2 实现长按选择模式
    - 监听卡片长按事件
    - 切换到选择模式
    - 显示批量操作工具栏
    - _Requirements: 8.3, 8.4_

  - [ ] 16.3 实现批量操作
    - 在 ActionMode 中添加批量操作按钮
    - 实现批量应用、分享、删除
    - _Requirements: 8.4_

  - [ ] 16.4 实现应用模板确认对话框
    - 创建确认对话框
    - 显示模板信息
    - 实现应用操作
    - _Requirements: 8.5_

- [ ] 17. 实现空状态和错误状态界面
  - [ ] 17.1 创建 view_empty_state.xml 布局
    - 添加插图 ImageView
    - 添加引导文字 TextView
    - 添加操作按钮（如"浏览模板"）
    - _Requirements: 9.1_

  - [ ] 17.2 创建骨架屏加载动画
    - 使用 Shimmer 库或自定义实现
    - 创建模板卡片骨架布局
    - _Requirements: 9.2_

  - [ ] 17.3 创建错误状态视图
    - 显示错误图标和消息
    - 添加重试按钮
    - _Requirements: 9.3_

  - [ ] 17.4 创建搜索无结果视图
    - 显示搜索建议
    - 提供热门模板推荐
    - _Requirements: 9.4_

  - [ ] 17.5 创建欢迎引导界面
    - 检测首次启动（SharedPreferences）
    - 显示欢迎界面和功能介绍
    - _Requirements: 9.5_

- [ ] 18. 实现排版和间距规范
  - [ ] 18.1 创建 Typography 工具类
    - 定义标准字体样式（标题、正文、说明）
    - 实现字体大小和字重设置方法
    - _Requirements: 6.1, 6.2, 6.3_

  - [ ] 18.2 实现对比度检查工具
    - 创建 ColorUtils.calculateContrastRatio() 方法
    - 验证文本和背景对比度 >= 4.5:1
    - 在开发模式下显示警告
    - _Requirements: 6.4_

  - [ ] 18.3 应用排版规范到所有文本元素
    - 更新模板标题为 16sp Medium
    - 更新分类标题为 24sp Bold
    - 更新描述文本为 14sp Regular
    - 确保所有内容区域使用 16dp 边距
    - _Requirements: 6.1, 6.2, 6.3, 6.5_

  - [ ] 18.4 编写排版规范属性测试
    - **Property 10: 排版规范一致性**
    - **Property 11: 内容区域边距一致性**
    - **Validates: Requirements 6.1, 6.2, 6.3, 6.4, 6.5**

- [ ] 19. Checkpoint - 功能集成测试
  - 确保所有 UI 组件正确集成
  - 测试完整的用户流程（浏览、选择、应用模板）
  - 验证动画和过渡效果
  - 测试不同屏幕尺寸和方向
  - 如有问题请向用户反馈

- [ ] 20. 性能优化和错误处理
  - [ ] 20.1 实现低端设备检测和动画降级
    - 检测设备性能等级
    - 在低端设备上简化动画
    - _Requirements: 所有动画相关需求_

  - [ ] 20.2 添加布局测量边界检查
    - 在自定义 View 的 onMeasure() 中添加边界检查
    - 使用安全的默认值
    - 记录警告日志
    - _Requirements: 1.1, 1.2_

  - [ ] 20.3 实现图片加载错误处理
    - 添加重试机制
    - 降级到本地缓存
    - 显示友好的错误提示
    - _Requirements: 7.4_

  - [ ] 20.4 实现主题切换异常处理
    - 捕获动态颜色提取异常
    - 回退到默认主题
    - 显示用户通知
    - _Requirements: 3.1, 3.2_

- [ ] 21. 可访问性改进
  - [ ] 21.1 添加内容描述
    - 为所有图片添加 contentDescription
    - 为所有按钮添加描述性文本
    - _Requirements: 所有 UI 需求_

  - [ ] 21.2 确保触摸目标大小
    - 验证所有可点击元素至少 48dp × 48dp
    - 调整过小的触摸目标
    - _Requirements: 8.1, 8.2_

  - [ ] 21.3 测试 TalkBack 兼容性
    - 使用 TalkBack 测试所有界面
    - 确保导航流畅
    - 修复发现的问题
    - _Requirements: 所有 UI 需求_

- [ ] 22. 最终集成测试和 UI 测试
  - [ ] 22.1 编写端到端 UI 测试（Espresso）
    - 测试模板列表显示
    - 测试卡片点击和动画
    - 测试主题切换
    - 测试选择模式和批量操作
    - _Requirements: 所有需求_

  - [ ] 22.2 运行所有属性测试
    - 确保所有 17 个属性测试通过
    - 验证每个测试至少 100 次迭代
    - _Requirements: 所有需求_

  - [ ] 22.3 生成测试覆盖率报告
    - 运行 Jacoco 生成覆盖率报告
    - 确保覆盖率 >= 80%
    - _Requirements: 所有需求_

- [ ] 23. 文档和发布准备
  - [ ] 23.1 更新 README 文档
    - 添加新 UI 特性说明
    - 更新截图
    - 添加主题配置说明

  - [ ] 23.2 创建迁移指南
    - 说明从旧版本升级的步骤
    - 列出破坏性变更
    - 提供配置示例

  - [ ] 23.3 准备发布说明
    - 列出所有新功能
    - 列出改进和优化
    - 列出已知问题

## Notes

- 所有测试任务都是必需的，确保全面的代码质量
- 每个任务都引用了具体的需求编号，便于追溯
- Checkpoint 任务确保增量验证，及早发现问题
- 属性测试任务明确标注了对应的设计文档属性编号
- 建议按顺序执行任务，确保依赖关系正确

