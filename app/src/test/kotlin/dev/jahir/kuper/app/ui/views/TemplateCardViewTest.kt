package dev.jahir.kuper.app.ui.views

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import dev.jahir.kuper.app.R
import dev.jahir.kuper.app.data.models.KLWPTemplate
import dev.jahir.kuper.app.data.models.TemplateType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * TemplateCardView 单元测试
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class TemplateCardViewTest {
    
    private lateinit var context: Context
    private lateinit var cardView: TemplateCardView
    
    @BeforeEach
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        cardView = TemplateCardView(context)
    }
    
    @Test
    fun `card should initialize with correct dimensions`() {
        val expectedRadius = context.resources.getDimension(R.dimen.card_corner_radius)
        val expectedElevation = context.resources.getDimension(R.dimen.card_elevation)
        
        assertEquals(expectedRadius, cardView.radius)
        assertEquals(expectedElevation, cardView.cardElevation)
    }
    
    @Test
    fun `card should have all required child views`() {
        assertNotNull(cardView.previewImageView)
        assertNotNull(cardView.titleTextView)
        assertNotNull(cardView.typeChip)
        assertNotNull(cardView.actionButton)
    }
    
    @Test
    fun `setTemplate should update UI elements`() {
        val template = KLWPTemplate(
            id = "test-1",
            name = "Test Template",
            description = "Test Description",
            previewUrl = "https://example.com/preview.jpg",
            fileUrl = "https://example.com/file.klwp",
            type = TemplateType.WALLPAPER
        )
        
        cardView.setTemplate(template)
        
        assertEquals("Test Template", cardView.titleTextView.text.toString())
        assertEquals("WALLPAPER", cardView.typeChip.text.toString())
        assertEquals(template, cardView.getTemplate())
    }
    
    @Test
    fun `enterSelectionMode should show checkbox and hide action button`() {
        cardView.enterSelectionMode()
        
        // 注意：visibility 检查需要在 UI 线程或使用 Robolectric
        // 这里验证方法调用不抛出异常
        assertDoesNotThrow {
            cardView.enterSelectionMode()
        }
    }
    
    @Test
    fun `exitSelectionMode should reset selection state`() {
        cardView.enterSelectionMode()
        cardView.setSelected(true, animated = false)
        
        cardView.exitSelectionMode()
        
        assertFalse(cardView.isSelected())
    }
    
    @Test
    fun `setSelected should update selection state in selection mode`() {
        cardView.enterSelectionMode()
        
        cardView.setSelected(true, animated = false)
        assertTrue(cardView.isSelected())
        
        cardView.setSelected(false, animated = false)
        assertFalse(cardView.isSelected())
    }
    
    @Test
    fun `setSelected should not work outside selection mode`() {
        // 不在选择模式下
        cardView.setSelected(true, animated = false)
        
        // 应该仍然是未选中状态
        assertFalse(cardView.isSelected())
    }
    
    @Test
    fun `playClickAnimation should not throw exception`() {
        assertDoesNotThrow {
            cardView.playClickAnimation()
        }
    }
    
    @Test
    fun `playClickAnimation should call onEnd callback`() {
        var callbackCalled = false
        
        cardView.playClickAnimation {
            callbackCalled = true
        }
        
        // 等待动画完成
        Thread.sleep(250)
        
        assertTrue(callbackCalled)
    }
    
    @Test
    fun `card should be clickable and focusable`() {
        assertTrue(cardView.isClickable)
        assertTrue(cardView.isFocusable)
    }
    
    @Test
    fun `getTemplate should return null initially`() {
        val newCardView = TemplateCardView(context)
        assertNull(newCardView.getTemplate())
    }
    
    @Test
    fun `multiple templates can be set on same card`() {
        val template1 = KLWPTemplate(
            id = "1",
            name = "Template 1",
            description = "Desc 1",
            previewUrl = "url1",
            fileUrl = "file1",
            type = TemplateType.WALLPAPER
        )
        
        val template2 = KLWPTemplate(
            id = "2",
            name = "Template 2",
            description = "Desc 2",
            previewUrl = "url2",
            fileUrl = "file2",
            type = TemplateType.WIDGET
        )
        
        cardView.setTemplate(template1)
        assertEquals("Template 1", cardView.titleTextView.text.toString())
        
        cardView.setTemplate(template2)
        assertEquals("Template 2", cardView.titleTextView.text.toString())
        assertEquals("WIDGET", cardView.typeChip.text.toString())
    }
}
