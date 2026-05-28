package com.cospose.gallery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "annotations",
    foreignKeys = [
        ForeignKey(
            entity = ImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["image_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["image_id"])]
)
data class AnnotationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "image_id") val imageId: String,
    val content: String,
    @ColumnInfo(name = "x_ratio") val xRatio: Float? = null,
    @ColumnInfo(name = "y_ratio") val yRatio: Float? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
