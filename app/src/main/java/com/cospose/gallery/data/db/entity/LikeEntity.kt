package com.cospose.gallery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "likes",
    primaryKeys = ["user_id", "image_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["image_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LikeEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "image_id") val imageId: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
