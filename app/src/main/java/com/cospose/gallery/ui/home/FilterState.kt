package com.cospose.gallery.ui.home

data class FilterState(
    val minRating: Float? = null,
    val maxRating: Float? = null,
    val minWidth: Int? = null,
    val minHeight: Int? = null,
    val mimeType: String? = null,
    val sinceTimestamp: Long? = null,
    val aspectRatio: AspectRatio? = null,
    val dominantColor: Int? = null
) {
    val isActive: Boolean
        get() = minRating != null || maxRating != null || minWidth != null ||
                minHeight != null || mimeType != null || sinceTimestamp != null ||
                aspectRatio != null || dominantColor != null

    val activeCount: Int
        get() = listOfNotNull(
            minRating, maxRating, minWidth, minHeight, mimeType, sinceTimestamp, aspectRatio, dominantColor
        ).size
}

enum class AspectRatio(val label: String, val ratio: Float?) {
    ANY("不限", null),
    LANDSCAPE("横向", 16f / 9f),
    PORTRAIT("竖向", 9f / 16f),
    SQUARE("方形", 1f)
}

enum class TimeRange(val label: String, val days: Long?) {
    ANY("不限", null),
    TODAY("今天", 1),
    WEEK("最近7天", 7),
    MONTH("最近30天", 30),
    YEAR("最近一年", 365)
}

enum class MimeTypeFilter(val label: String, val mimeType: String?) {
    ALL("全部", null),
    JPG("JPG", "image/jpeg"),
    PNG("PNG", "image/png"),
    WEBP("WEBP", "image/webp")
}
