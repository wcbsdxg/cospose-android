package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.LikeEntity

@Dao
interface LikeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: LikeEntity): Long

    @Query("DELETE FROM likes WHERE user_id = :userId AND image_id = :imageId")
    suspend fun delete(userId: String, imageId: String): Int

    @Query("SELECT COUNT(*) FROM likes WHERE user_id = :userId AND image_id = :imageId > 0")
    suspend fun isLiked(userId: String, imageId: String): Boolean

    @Query("SELECT COUNT(*) FROM likes WHERE image_id = :imageId")
    suspend fun countByImage(imageId: String): Int
}
