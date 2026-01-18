package dev.jahir.kuper.app.ui.animation

import android.content.Context
import android.os.Build
import android.view.View
import androidx.test.core.app.ApplicationProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * AnimationManager 单元测试
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class AnimationManagerTest {
    
    private lateinit var context: Context
    
    @BeforeEach
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }
    
    @Test
    fun `animation constants should have correct values`() {
        assertEquals(200L, AnimationManager.DURATION_SHORT)
        assertEquals(300L, AnimationManager.DURATION_MEDIUM)
        assertEquals(500L, AnimationManager.DURATION_LONG)
        assertEquals(50L, AnimationManager.STAGGER_DELAY)
    }
    
    @Test
    fun `playCardClickAnimation should not throw exception`() {
        val view = View(context)
        assertDoesNotThrow {
            AnimationManager.playCardClickAnimation(view)
        }
    }
    
    @Test
    fun `playCardClickAnimation should call onEnd callback`() {
        val view = View(context)
        var callbackCalled = false
        
        AnimationManager.playCardClickAnimation(view) {
            callbackCalled = true
        }
        
        Thread.sleep(500)
        assertTrue(callbackCalled)
    }
    
    @Test
    fun `createSharedElementTransition should return valid transition`() {
        val transition = AnimationManager.createSharedElementTransition()
        assertNotNull(transition)
    }
    
    @Test
    fun `playStaggeredAnimation should handle empty list`() {
        assertDoesNotThrow {
            AnimationManager.playStaggeredAnimation(emptyList())
        }
    }
    
    @Test
    fun `playStaggeredAnimation should handle single view`() {
        val view = View(context)
        assertDoesNotThrow {
            AnimationManager.playStaggeredAnimation(listOf(view))
        }
    }
    
    @Test
    fun `crossFade should not throw exception`() {
        val fromView = View(context)
        val toView = View(context)
        
        assertDoesNotThrow {
            AnimationManager.crossFade(fromView, toView)
        }
    }
    
    @Test
    fun `createRefreshAnimation should return valid animation`() {
        val animation = AnimationManager.createRefreshAnimation()
        assertNotNull(animation)
    }
    
    @Test
    fun `createFadeInAnimation should return valid animation`() {
        val animation = AnimationManager.createFadeInAnimation()
        assertNotNull(animation)
    }
    
    @Test
    fun `createFadeOutAnimation should return valid animation`() {
        val animation = AnimationManager.createFadeOutAnimation()
        assertNotNull(animation)
    }
    
    @Test
    fun `animateColorTransition should create valid animator`() {
        val animator = AnimationManager.animateColorTransition(
            0xFF000000.toInt(),
            0xFFFFFFFF.toInt()
        ) { }
        
        assertNotNull(animator)
        assertEquals(AnimationManager.DURATION_LONG, animator.duration)
    }
}
