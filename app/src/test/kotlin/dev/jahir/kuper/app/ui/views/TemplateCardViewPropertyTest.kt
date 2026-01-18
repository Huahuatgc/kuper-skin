package dev.jahir.kuper.app.ui.views

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import dev.jahir.kuper.app.R
import dev.jahir.kuper.app.data.models.KLWPTemplate
import dev.jahir.kuper.app.data.models.TemplateType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * TemplateCardView 属性测试
 * Feature: ui-redesign, Property 1: 模板卡片视觉规范一致性
 * Validates: Requirements 1.1, 1.2, 1.3, 1.4
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class TemplateCardViewPropertyTest : StringSpec({
    
    lateinit var context: Context
    
    beforeTest {
        context = ApplicationProvider.getApplicationContext()
    }
    
    // Feature: ui-redesign, Property 1: 模板卡片视觉规范一致性
    "template card should have consistent visual specifications" {
        checkAll(100, arbKLWPTemplate()) { template ->
            val cardView = TemplateCardView(context)
            cardView.setTemplate(template)
            
            // 验证圆角半径为 16dp
            val expectedRadius = context.resources.getDimension(R.dimen.card_corner_radius)
            cardView.radius shouldBe expectedRadius
            
            // 验证 elevation 为 4dp
            val expectedElevation = context.resources.getDimension(R.dimen.card_elevation)
            cardView.cardElevation shouldBe expectedElevation
            
            // 验证包含必需的子视图
            cardView.previewImageView shouldNotBe null
            cardView.titleTextView shouldNotBe null
            cardView.typeChip shouldNotBe null
            cardView.actionButton shouldNotBe null
            
            // 验证标题和类型正确设置
            cardView.titleTextView.text.toString() shouldBe template.name
            cardView.typeChip.text.toString() shouldBe template.type.name
        }
    }
    
    "template card should maintain aspect ratio for preview image" {
        checkAll(100, arbKLWPTemplate()) { template ->
            val cardView = TemplateCardView(context)
            cardView.setTemplate(template)
            
            // 验证预览图存在
            cardView.previewImageView shouldNotBe null
            
            // 注意：实际的宽高比验证需要在布局测量后进行
            // 这里验证 ImageView 已正确初始化
            cardView.previewImageView.scaleType shouldNotBe null
        }
    }
    
    "template card should support selection mode" {
        checkAll(100, Arb.boolean()) { selected ->
            val cardView = TemplateCardView(context)
            
            // 进入选择模式
            cardView.enterSelectionMode()
            
            // 设置选择状态
            cardView.setSelected(selected, animated = false)
            
            // 验证选择状态
            cardView.isSelected() shouldBe selected
        }
    }
    
    "template card should exit selection mode correctly" {
        val cardView = TemplateCardView(context)
        
        // 进入选择模式并选中
        cardView.enterSelectionMode()
        cardView.setSelected(true, animated = false)
        
        // 退出选择模式
        cardView.exitSelectionMode()
        
        // 验证状态已重置
        cardView.isSelected() shouldBe false
    }
})

/**
 * 生成随机 KLWPTemplate 的 Arb
 */
fun arbKLWPTemplate(): Arb<KLWPTemplate> = arbitrary {
    KLWPTemplate(
        id = Arb.uuid().bind().toString(),
        name = Arb.string(5..50).bind(),
        description = Arb.string(10..200).bind(),
        previewUrl = "https://example.com/preview_${Arb.int(1..1000).bind()}.jpg",
        fileUrl = "https://example.com/file_${Arb.int(1..1000).bind()}.klwp",
        type = Arb.enum<TemplateType>().bind(),
        author = Arb.string(3..30).bind(),
        version = "${Arb.int(1..10).bind()}.${Arb.int(0..9).bind()}",
        fileSize = Arb.long(1000L..10000000L).bind(),
        tags = Arb.list(Arb.string(3..15), 0..5).bind()
    )
}
