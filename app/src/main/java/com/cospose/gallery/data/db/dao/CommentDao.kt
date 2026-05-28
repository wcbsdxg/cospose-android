package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE image_id = :imageId ORDER BY created_at DESC")
    fun observeByImage(imageId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE image_id = :imageId ORDER BY created_at DESC")
    suspend fun getByImage(imageId: String): List<CommentEntity>

    @Query("SELECT COUNT(*) FROM comments WHERE image_id = :imageId")
    suspend fun countByImage(imageId: String): Int

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM comments WHERE sync_status = 'PENDING'")
    suspend fun getPendingSync(): List<CommentEntity>
}
