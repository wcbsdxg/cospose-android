package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.AnnotationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnotationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(annotation: AnnotationEntity)

    @Query("SELECT * FROM annotations WHERE image_id = :imageId ORDER BY created_at DESC")
    suspend fun getByImage(imageId: String): List<AnnotationEntity>

    @Query("SELECT * FROM annotations WHERE image_id = :imageId ORDER BY created_at DESC")
    fun observeByImage(imageId: String): Flow<List<AnnotationEntity>>

    @Query("SELECT * FROM annotations WHERE id = :id")
    suspend fun getById(id: String): AnnotationEntity?

    @Query("DELETE FROM annotations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM annotations WHERE image_id = :imageId")
    suspend fun deleteByImage(imageId: String)

    @Query("SELECT COUNT(*) FROM annotations WHERE image_id = :imageId")
    suspend fun countByImage(imageId: String): Int
}
