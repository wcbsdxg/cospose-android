package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.SmartFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartFolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(smartFolder: SmartFolderEntity)

    @Query("SELECT * FROM smart_folders ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<SmartFolderEntity>>

    @Query("SELECT * FROM smart_folders WHERE id = :id")
    suspend fun getById(id: String): SmartFolderEntity?

    @Query("DELETE FROM smart_folders WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM smart_folders")
    fun observeCount(): Flow<Int>
}
