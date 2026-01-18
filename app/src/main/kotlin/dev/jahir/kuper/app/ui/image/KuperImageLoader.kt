package dev.jahir.kuper.app.ui.image

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
import dev.jahir.kuper.app.R
import java.io.File

/**
 * 图片加载管理器
 * 基于 Coil 实现高效的图片加载、缓存和预加载
 */
object KuperImageLoader {
    
    private var imageLoader: ImageLoader? = null
    
    // 缓存配置
    private const val MEMORY_CACHE_MAX_SIZE = 50 // 最多缓存 50 张图片
    private const val DISK_CACHE_MAX_SIZE = 100L * 1024 * 1024 // 100MB
    private const val CROSSFADE_DURATION = 300 // 淡入动画时长
    
    /**
     * 初始化图片加载器
     * 应在 Application.onCreate() 中调用
     */
    fun initialize(context: Context) {
        if (imageLoader != null) return
        
        val cacheDir = File(context.cacheDir, "image_cache")
        
        imageLoader = ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 使用 25% 的可用内存
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir)
                    .maxSizeBytes(DISK_CACHE_MAX_SIZE)
                    .build()
            }
            .crossfade(true)
            .crossfade(CROSSFADE_DURATION)
            .respectCacheHeaders(false)
            .build()
    }
    
    /**
     * 获取 ImageLoader 实例
     */
    fun getLoader(context: Context): ImageLoader {
        if (imageLoader == null) {
            initialize(context)
        }
        return imageLoader!!
    }
    
    /**
     * 加载预览图片
     * @param imageView 目标 ImageView
     * @param url 图片 URL 或本地路径
     * @param onSuccess 加载成功回调
     * @param onError 加载失败回调
     */
    fun loadPreview(
        imageView: ImageView,
        url: String?,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (url.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.placeholder_error)
            onError?.invoke(IllegalArgumentException("URL is null or blank"))
            return
        }
        
        val context = imageView.context
        val request = ImageRequest.Builder(context)
            .data(url)
            .target(imageView)
            .placeholder(R.drawable.placeholder_loading)
            .error(R.drawable.placeholder_error)
            .crossfade(true)
            .crossfade(CROSSFADE_DURATION)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .listener(
                onSuccess = { _, _ -> onSuccess?.invoke() },
                onError = { _, result -> onError?.invoke(result.throwable) }
            )
            .build()
        
        getLoader(context).enqueue(request)
    }
    
    /**
     * 从本地文件加载预览图
     * @param imageView 目标 ImageView
     * @param file 本地文件
     */
    fun loadPreviewFromFile(
        imageView: ImageView,
        file: File?,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        if (file == null || !file.exists()) {
            imageView.setImageResource(R.drawable.placeholder_error)
            onError?.invoke(IllegalArgumentException("File is null or does not exist"))
            return
        }
        
        val context = imageView.context
        val request = ImageRequest.Builder(context)
            .data(file)
            .target(imageView)
            .placeholder(R.drawable.placeholder_loading)
            .error(R.drawable.placeholder_error)
            .crossfade(true)
            .crossfade(CROSSFADE_DURATION)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .listener(
                onSuccess = { _, _ -> onSuccess?.invoke() },
                onError = { _, result -> onError?.invoke(result.throwable) }
            )
            .build()
        
        getLoader(context).enqueue(request)
    }
    
    /**
     * 预加载图片列表
     * 用于提前加载即将进入视口的图片
     * @param context Context
     * @param urls 图片 URL 列表
     */
    fun preload(context: Context, urls: List<String>) {
        val loader = getLoader(context)
        urls.forEach { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
            loader.enqueue(request)
        }
    }
    
    /**
     * 预加载单张图片
     */
    fun preload(context: Context, url: String) {
        preload(context, listOf(url))
    }
    
    /**
     * 清除所有缓存
     */
    fun clearCache(context: Context) {
        getLoader(context).memoryCache?.clear()
        getLoader(context).diskCache?.clear()
    }
    
    /**
     * 获取磁盘缓存大小（字节）
     */
    fun getDiskCacheSize(context: Context): Long {
        return getLoader(context).diskCache?.size ?: 0L
    }
    
    /**
     * 获取格式化的缓存大小字符串
     */
    fun getFormattedCacheSize(context: Context): String {
        val bytes = getDiskCacheSize(context)
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}
