package dev.jahir.kuper.app.ui.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.Transition
import android.transition.TransitionSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AlphaAnimation
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * 动画管理器
 * 统一管理应用内的所有动画效果
 */
object AnimationManager {
    
    // 动画时长常量
    const val DURATION_SHORT = 200L
    const val DURATION_MEDIUM = 300L
    const val DURATION_LONG = 500L
    
    // 交错动画延迟
    const val STAGGER_DELAY = 50L
    
    // 低端设备检测
    private var isLowEndDevice: Boolean? = null
    
    /**
     * 检测是否为低端设备
     */
    private fun isLowEndDevice(context: Context): Boolean {
        if (isLowEndDevice == null) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            isLowEndDevice = activityManager?.isLowRamDevice ?: false ||
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        }
        return isLowEndDevice!!
    }
    
    /**
     * 播放卡片点击动画
     * 200ms 缩放动画，目标缩放比例 0.95
     */
    fun playCardClickAnimation(view: View, onEnd: () -> Unit = {}) {
        if (isLowEndDevice(view.context)) {
            // 低端设备：简化动画
            view.animate()
                .alpha(0.7f)
                .setDuration(100)
                .withEndAction {
                    view.animate()
                        .alpha(1.0f)
                        .setDuration(100)
                        .withEndAction(onEnd)
                        .start()
                }
                .start()
        } else {
            // 完整动画：缩放 + alpha
            view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .alpha(0.9f)
                .setDuration(DURATION_SHORT)
                .setInterpolator(FastOutSlowInInterpolator())
                .withEndAction {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(1.0f)
                        .setDuration(DURATION_SHORT)
                        .setInterpolator(FastOutSlowInInterpolator())
                        .withEndAction(onEnd)
                        .start()
                }
                .start()
        }
    }
    
    /**
     * 创建共享元素过渡动画
     */
    fun createSharedElementTransition(): Transition {
        return TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
            duration = DURATION_MEDIUM
            interpolator = FastOutSlowInInterpolator()
        }
    }
    
    /**
     * 播放列表项交错动画
     * 每个 view 按顺序播放淡入动画，相邻延迟 50ms
     */
    fun playStaggeredAnimation(views: List<View>, delayMs: Long = STAGGER_DELAY) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 50f
            
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(DURATION_MEDIUM)
                .setStartDelay(index * delayMs)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()
        }
    }
    
    /**
     * 淡入淡出动画
     * 旧视图淡出和新视图淡入同时进行
     */
    fun crossFade(fromView: View, toView: View, durationMs: Long = DURATION_MEDIUM) {
        // 设置初始状态
        toView.alpha = 0f
        toView.visibility = View.VISIBLE
        
        // 淡出旧视图
        fromView.animate()
            .alpha(0f)
            .setDuration(durationMs)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                fromView.visibility = View.GONE
            }
            .start()
        
        // 淡入新视图
        toView.animate()
            .alpha(1f)
            .setDuration(durationMs)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()
    }
    
    /**
     * 创建下拉刷新动画
     */
    fun createRefreshAnimation(): Animation {
        return AnimationSet(true).apply {
            // 旋转动画
            val rotate = android.view.animation.RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
            }
            
            addAnimation(rotate)
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
    
    /**
     * 创建淡入动画
     */
    fun createFadeInAnimation(durationMs: Long = DURATION_MEDIUM): Animation {
        return AlphaAnimation(0f, 1f).apply {
            duration = durationMs
            interpolator = FastOutSlowInInterpolator()
        }
    }
    
    /**
     * 创建淡出动画
     */
    fun createFadeOutAnimation(durationMs: Long = DURATION_MEDIUM): Animation {
        return AlphaAnimation(1f, 0f).apply {
            duration = durationMs
            interpolator = FastOutSlowInInterpolator()
        }
    }
    
    /**
     * 播放脉冲动画（用于强调）
     */
    fun playPulseAnimation(view: View, repeat: Int = 1) {
        val scaleUp = 1.1f
        val scaleDuration = 150L
        
        var currentRepeat = 0
        
        fun pulse() {
            view.animate()
                .scaleX(scaleUp)
                .scaleY(scaleUp)
                .setDuration(scaleDuration)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(scaleDuration)
                        .withEndAction {
                            currentRepeat++
                            if (currentRepeat < repeat) {
                                pulse()
                            }
                        }
                        .start()
                }
                .start()
        }
        
        pulse()
    }
    
    /**
     * 创建颜色过渡动画
     */
    fun animateColorTransition(
        fromColor: Int,
        toColor: Int,
        durationMs: Long = DURATION_LONG,
        onUpdate: (Int) -> Unit
    ): ValueAnimator {
        return ValueAnimator.ofArgb(fromColor, toColor).apply {
            duration = durationMs
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animator ->
                onUpdate(animator.animatedValue as Int)
            }
        }
    }
}
