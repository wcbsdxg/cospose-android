package com.cospose.gallery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_folders")
data class SmartFolderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val rules: String,  // JSON-encoded FilterState
    val icon: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
