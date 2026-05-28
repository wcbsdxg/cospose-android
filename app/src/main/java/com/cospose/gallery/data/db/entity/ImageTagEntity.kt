package com.cospose.gallery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "image_tags",
    primaryKeys = ["image_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = ImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["image_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ImageTagEntity(
    @ColumnInfo(name = "image_id") val imageId: String,
    @ColumnInfo(name = "tag_id") val tagId: String,
    val source: String,
    val confidence: Float? = null
)
