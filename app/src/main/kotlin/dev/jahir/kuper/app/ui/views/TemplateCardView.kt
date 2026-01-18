package dev.jahir.kuper.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import dev.jahir.kuper.app.R
import dev.jahir.kuper.app.data.models.KLWPTemplate

/**
 * 模板卡片自定义 View
 * 现代化的模板卡片组件，支持 16dp 圆角、9:16 预览图、点击动画和选择模式
 */
class TemplateCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    
    // UI 组件
    val previewImageView: ImageView
    val titleTextView: TextView
    val typeChip: Chip
    val actionButton: ImageButton
    private val selectionCheckbox: CheckBox
    
    // 当前模板数据
    private var currentTemplate: KLWPTemplate? = null
    
    // 选择状态
    private var isInSelectionMode = false
    
    init {
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.view_template_card, this, true)
        
        // 初始化 UI 组件
        previewImageView = findViewById(R.id.previewImageView)
        titleTextView = findViewById(R.id.titleTextView)
        typeChip = findViewById(R.id.typeChip)
        actionButton = findViewById(R.id.actionButton)
        selectionCheckbox = findViewById(R.id.selectionCheckbox)
        
        // 设置默认属性
        radius = resources.getDimension(R.dimen.card_corner_radius)
        cardElevation = resources.getDimension(R.dimen.card_elevation)
        
        // 设置点击效果
        isClickable = true
        isFocusable = true
    }
    
    /**
     * 设置模板数据
     */
    fun setTemplate(template: KLWPTemplate) {
        currentTemplate = template
        
        // 设置标题
        titleTextView.text = template.name
        
        // 设置类型标签
        typeChip.text = template.type.name
        
        // 预览图将在后续通过 ImageLoader 加载
        // 这里只是占位
    }
    
    /**
     * 播放点击动画
     * 200ms 缩放动画，目标缩放比例 0.95
     */
    fun playClickAnimation(onEnd: () -> Unit = {}) {
        // 缩小动画
        animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // 恢复动画
                animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction(onEnd)
                    .start()
            }
            .start()
    }
    
    /**
     * 设置选择状态
     * @param selected 是否选中
     * @param animated 是否使用动画
     */
    fun setSelected(selected: Boolean, animated: Boolean = true) {
        if (!isInSelectionMode) return
        
        selectionCheckbox.isChecked = selected
        
        if (animated) {
            // 添加选中状态的视觉反馈
            val targetElevation = if (selected) {
                cardElevation * 2
            } else {
                resources.getDimension(R.dimen.card_elevation)
            }
            
            animate()
                .translationZ(targetElevation)
                .setDuration(200)
                .start()
        } else {
            translationZ = if (selected) cardElevation * 2 else 0f
        }
    }
    
    /**
     * 进入选择模式
     */
    fun enterSelectionMode() {
        isInSelectionMode = true
        selectionCheckbox.visibility = View.VISIBLE
        actionButton.visibility = View.GONE
        
        // 动画显示复选框
        selectionCheckbox.alpha = 0f
        selectionCheckbox.animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }
    
    /**
     * 退出选择模式
     */
    fun exitSelectionMode() {
        isInSelectionMode = false
        selectionCheckbox.visibility = View.GONE
        actionButton.visibility = View.VISIBLE
        selectionCheckbox.isChecked = false
        translationZ = 0f
    }
    
    /**
     * 获取当前模板
     */
    fun getTemplate(): KLWPTemplate? = currentTemplate
    
    /**
     * 检查是否选中
     */
    fun isTemplateSelected(): Boolean = selectionCheckbox.isChecked
}
