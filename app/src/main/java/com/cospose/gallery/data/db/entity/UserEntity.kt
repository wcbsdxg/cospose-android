package com.cospose.gallery.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val email: String?,
    @ColumnInfo(name = "password_hash") val passwordHash: String?,
    @ColumnInfo(name = "avatar_path") val avatarPath: String?,
    val bio: String?,
    val role: String = "USER",
    @ColumnInfo(name = "server_id") val serverId: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "SYNCED",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
