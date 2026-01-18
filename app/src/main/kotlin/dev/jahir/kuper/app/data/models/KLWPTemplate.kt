package dev.jahir.kuper.app.data.models

/**
 * KLWP 模板数据模型
 */
data class KLWPTemplate(
    val id: String,
    val name: String,
    val description: String,
    val previewUrl: String,
    val fileUrl: String,
    val type: TemplateType,
    val author: String = "",
    val version: String = "1.0",
    val fileSize: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList()
)

/**
 * 模板类型枚举
 */
enum class TemplateType {
    WALLPAPER,
    WIDGET,
    LOCKSCREEN,
    KOMPONENT
}
