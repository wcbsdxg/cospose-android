package com.cospose.gallery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "board_images",
    primaryKeys = ["board_id", "image_id"],
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["id"],
            childColumns = ["board_id"],
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
data class BoardImageEntity(
    @ColumnInfo(name = "board_id") val boardId: String,
    @ColumnInfo(name = "image_id") val imageId: String,
    val position: Int = 0,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)
