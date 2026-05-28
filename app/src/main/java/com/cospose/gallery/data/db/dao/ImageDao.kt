package com.cospose.gallery.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cospose.gallery.data.db.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(image: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(images: List<ImageEntity>)

    @Update
    suspend fun update(image: ImageEntity)

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getById(id: String): ImageEntity?

    @Query("SELECT * FROM images WHERE id = :id")
    fun observeById(id: String): Flow<ImageEntity?>

    // Paging sources for different sorts
    @Query("SELECT * FROM images ORDER BY score DESC")
    fun pagingByScore(): PagingSource<Int, ImageEntity>

    @Query("SELECT * FROM images ORDER BY created_at DESC")
    fun pagingByNewest(): PagingSource<Int, ImageEntity>

    @Query("SELECT * FROM images ORDER BY likes_count DESC")
    fun pagingByPopular(): PagingSource<Int, ImageEntity>

    @Query("SELECT * FROM images ORDER BY rating_avg DESC")
    fun pagingByRating(): PagingSource<Int, ImageEntity>

    @Query("""
        SELECT * FROM images
        WHERE (:minRating IS NULL OR rating_avg >= :minRating)
          AND (:maxRating IS NULL OR rating_avg <= :maxRating)
          AND (:minWidth IS NULL OR width >= :minWidth)
          AND (:minHeight IS NULL OR height >= :minHeight)
          AND (:mimeType IS NULL OR mime_type = :mimeType)
          AND (:since IS NULL OR created_at >= :since)
          AND (:aspectRatio IS NULL OR
               (CAST(width AS REAL) / NULLIF(height, 0)) BETWEEN (:aspectRatio - 0.15) AND (:aspectRatio + 0.15))
          AND (:dominantColor IS NULL OR dominant_color = :dominantColor)
        ORDER BY score DESC
    """)
    fun pagingFiltered(
        minRating: Float? = null,
        maxRating: Float? = null,
        minWidth: Int? = null,
        minHeight: Int? = null,
        mimeType: String? = null,
        since: Long? = null,
        aspectRatio: Float? = null,
        dominantColor: Int? = null
    ): PagingSource<Int, ImageEntity>

    @Query("""
        SELECT * FROM images
        WHERE (:minRating IS NULL OR rating_avg >= :minRating)
          AND (:maxRating IS NULL OR rating_avg <= :maxRating)
          AND (:minWidth IS NULL OR width >= :minWidth)
          AND (:minHeight IS NULL OR height >= :minHeight)
          AND (:mimeType IS NULL OR mime_type = :mimeType)
          AND (:since IS NULL OR created_at >= :since)
          AND (:aspectRatio IS NULL OR
               (CAST(width AS REAL) / NULLIF(height, 0)) BETWEEN (:aspectRatio - 0.15) AND (:aspectRatio + 0.15))
          AND (:dominantColor IS NULL OR dominant_color = :dominantColor)
        ORDER BY created_at DESC
    """)
    fun pagingFilteredNewest(
        minRating: Float? = null,
        maxRating: Float? = null,
        minWidth: Int? = null,
        minHeight: Int? = null,
        mimeType: String? = null,
        since: Long? = null,
        aspectRatio: Float? = null,
        dominantColor: Int? = null
    ): PagingSource<Int, ImageEntity>

    @Query("""
        SELECT * FROM images
        WHERE (:minRating IS NULL OR rating_avg >= :minRating)
          AND (:maxRating IS NULL OR rating_avg <= :maxRating)
          AND (:minWidth IS NULL OR width >= :minWidth)
          AND (:minHeight IS NULL OR height >= :minHeight)
          AND (:mimeType IS NULL OR mime_type = :mimeType)
          AND (:since IS NULL OR created_at >= :since)
          AND (:aspectRatio IS NULL OR
               (CAST(width AS REAL) / NULLIF(height, 0)) BETWEEN (:aspectRatio - 0.15) AND (:aspectRatio + 0.15))
          AND (:dominantColor IS NULL OR dominant_color = :dominantColor)
        ORDER BY likes_count DESC
    """)
    fun pagingFilteredPopular(
        minRating: Float? = null,
        maxRating: Float? = null,
        minWidth: Int? = null,
        minHeight: Int? = null,
        mimeType: String? = null,
        since: Long? = null,
        aspectRatio: Float? = null,
        dominantColor: Int? = null
    ): PagingSource<Int, ImageEntity>

    @Query("""
        SELECT * FROM images
        WHERE (:minRating IS NULL OR rating_avg >= :minRating)
          AND (:maxRating IS NULL OR rating_avg <= :maxRating)
          AND (:minWidth IS NULL OR width >= :minWidth)
          AND (:minHeight IS NULL OR height >= :minHeight)
          AND (:mimeType IS NULL OR mime_type = :mimeType)
          AND (:since IS NULL OR created_at >= :since)
          AND (:aspectRatio IS NULL OR
               (CAST(width AS REAL) / NULLIF(height, 0)) BETWEEN (:aspectRatio - 0.15) AND (:aspectRatio + 0.15))
          AND (:dominantColor IS NULL OR dominant_color = :dominantColor)
        ORDER BY rating_avg DESC
    """)
    fun pagingFilteredTopRated(
        minRating: Float? = null,
        maxRating: Float? = null,
        minWidth: Int? = null,
        minHeight: Int? = null,
        mimeType: String? = null,
        since: Long? = null,
        aspectRatio: Float? = null,
        dominantColor: Int? = null
    ): PagingSource<Int, ImageEntity>

    @Query("SELECT * FROM images WHERE user_id = :userId ORDER BY created_at DESC")
    fun pagingByUser(userId: String): PagingSource<Int, ImageEntity>

    // Search
    @Query("""
        SELECT * FROM images
        WHERE title LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY created_at DESC
    """)
    fun searchByKeyword(query: String): PagingSource<Int, ImageEntity>

    @Query("SELECT * FROM images ORDER BY created_at DESC")
    suspend fun getAll(): List<ImageEntity>

    // Update counters
    @Query("UPDATE images SET likes_count = :count WHERE id = :imageId")
    suspend fun updateLikesCount(imageId: String, count: Int)

    @Query("UPDATE images SET comments_count = :count WHERE id = :imageId")
    suspend fun updateCommentsCount(imageId: String, count: Int)

    @Query("UPDATE images SET rating_avg = :avg, rating_count = :count WHERE id = :imageId")
    suspend fun updateRating(imageId: String, avg: Float, count: Int)

    @Query("UPDATE images SET score = :score WHERE id = :imageId")
    suspend fun updateScore(imageId: String, score: Float)

    @Query("UPDATE images SET embedding = :embedding WHERE id = :imageId")
    suspend fun updateEmbedding(imageId: String, embedding: String)

    @Query("UPDATE images SET server_id = :serverId, server_url = :serverUrl, sync_status = 'SYNCED' WHERE id = :imageId")
    suspend fun markSynced(imageId: String, serverId: String, serverUrl: String)

    @Query("SELECT * FROM images WHERE sync_status = 'PENDING'")
    suspend fun getPendingSync(): List<ImageEntity>

    @Query("SELECT * FROM images WHERE embedding IS NOT NULL")
    suspend fun getAllWithEmbedding(): List<ImageEntity>

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM images")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM images")
    suspend fun count(): Int
}
