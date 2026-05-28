package com.cospose.gallery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("user_id"),
        Index("created_at"),
        Index("score"),
        Index("likes_count"),
        Index("rating_avg")
    ]
)
data class ImageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val title: String,
    val description: String? = null,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "thumbnail_path") val thumbnailPath: String? = null,
    @ColumnInfo(name = "medium_path") val mediumPath: String? = null,
    @ColumnInfo(name = "server_id") val serverId: String? = null,
    @ColumnInfo(name = "server_url") val serverUrl: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    @ColumnInfo(name = "file_size") val fileSize: Long = 0,
    @ColumnInfo(name = "mime_type") val mimeType: String = "image/jpeg",
    val embedding: String? = null,
    @ColumnInfo(name = "likes_count") val likesCount: Int = 0,
    @ColumnInfo(name = "comments_count") val commentsCount: Int = 0,
    @ColumnInfo(name = "rating_avg") val ratingAvg: Float = 0f,
    @ColumnInfo(name = "rating_count") val ratingCount: Int = 0,
    val score: Float = 0f,
    @ColumnInfo(name = "dominant_color") val dominantColor: Int? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDING",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
) {
    val url: String get() = serverUrl ?: filePath
    val filename: String get() = filePath.substringAfterLast("/")
}
