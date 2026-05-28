package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.RatingEntity

@Dao
interface RatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rating: RatingEntity)

    @Query("SELECT * FROM ratings WHERE user_id = :userId AND image_id = :imageId LIMIT 1")
    suspend fun get(userId: String, imageId: String): RatingEntity?

    @Query("SELECT AVG(score) FROM ratings WHERE image_id = :imageId")
    suspend fun getAverage(imageId: String): Float?

    @Query("SELECT COUNT(*) FROM ratings WHERE image_id = :imageId")
    suspend fun countByImage(imageId: String): Int

    @Query("SELECT * FROM ratings WHERE image_id = :imageId")
    suspend fun getAllForImage(imageId: String): List<RatingEntity>
}
