package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.BoardEntity
import com.cospose.gallery.data.db.entity.BoardImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(board: BoardEntity)

    @Query("SELECT * FROM boards WHERE user_id = :userId ORDER BY updated_at DESC")
    fun observeByUser(userId: String): Flow<List<BoardEntity>>

    @Query("SELECT * FROM boards WHERE id = :id")
    suspend fun getById(id: String): BoardEntity?

    @Query("SELECT * FROM boards WHERE id = :id")
    fun observeById(id: String): Flow<BoardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(boardImage: BoardImageEntity)

    @Query("DELETE FROM board_images WHERE board_id = :boardId AND image_id = :imageId")
    suspend fun removeImage(boardId: String, imageId: String)

    @Query("SELECT * FROM board_images WHERE board_id = :boardId ORDER BY position")
    suspend fun getImages(boardId: String): List<BoardImageEntity>

    @Query("""
        SELECT i.* FROM images i
        INNER JOIN board_images bi ON i.id = bi.image_id
        WHERE bi.board_id = :boardId
        ORDER BY bi.position
    """)
    fun observeImagesInBoard(boardId: String): Flow<List<com.cospose.gallery.data.db.entity.ImageEntity>>

    @Query("DELETE FROM boards WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM board_images WHERE board_id = :boardId")
    suspend fun imageCount(boardId: String): Int
}
