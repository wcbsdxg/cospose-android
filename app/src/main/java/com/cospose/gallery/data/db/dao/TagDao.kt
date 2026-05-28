package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.ImageTagEntity
import com.cospose.gallery.data.db.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTags(tags: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertImageTag(imageTag: ImageTagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertImageTags(imageTags: List<ImageTagEntity>)

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getById(id: String): TagEntity?

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): TagEntity?

    @Query("SELECT * FROM tags ORDER BY name")
    fun observeAll(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags ORDER BY name")
    suspend fun getAll(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE category = :category ORDER BY name")
    fun observeByCategory(category: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE parent_id = :parentId ORDER BY name")
    suspend fun getChildren(parentId: String): List<TagEntity>

    @Query("SELECT * FROM tags WHERE parent_id IS NULL ORDER BY category, name")
    suspend fun getRootTags(): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN image_tags it ON t.id = it.tag_id
        WHERE it.image_id = :imageId
        ORDER BY t.category, t.name
    """)
    suspend fun getTagsByCategoryGrouped(imageId: String): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN image_tags it ON t.id = it.tag_id
        WHERE it.image_id = :imageId
        ORDER BY t.category, t.name
    """)
    suspend fun getTagsForImage(imageId: String): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN image_tags it ON t.id = it.tag_id
        WHERE it.image_id = :imageId AND it.source = :source
        ORDER BY t.category, t.name
    """)
    suspend fun getTagsForImageBySource(imageId: String, source: String): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN image_tags it ON t.id = it.tag_id
        WHERE it.image_id = :imageId
    """)
    fun observeTagsForImage(imageId: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM image_tags WHERE image_id = :imageId")
    suspend fun getImageTags(imageId: String): List<ImageTagEntity>

    @Query("DELETE FROM image_tags WHERE image_id = :imageId AND source = 'AI'")
    suspend fun deleteAITags(imageId: String)

    @Query("DELETE FROM image_tags WHERE image_id = :imageId AND source = 'AI' AND tag_id IN (SELECT id FROM tags WHERE category = :category)")
    suspend fun deleteAITagsByCategory(imageId: String, category: String)

    @Query("DELETE FROM image_tags WHERE image_id = :imageId AND tag_id = :tagId")
    suspend fun deleteImageTag(imageId: String, tagId: String)

    @Query("""
        SELECT t.id, t.name, t.category, t.parent_id, t.created_at,
               COUNT(it.image_id) as imageCount
        FROM tags t
        LEFT JOIN image_tags it ON t.id = it.tag_id
        WHERE t.name LIKE '%' || :query || '%'
        GROUP BY t.id
        ORDER BY imageCount DESC
        LIMIT :limit
    """)
    suspend fun searchTags(query: String, limit: Int = 20): List<TagWithCount>

    @Query("""
        SELECT t.id, t.name, t.category, t.parent_id, t.created_at,
               COUNT(it.image_id) as imageCount
        FROM tags t
        LEFT JOIN image_tags it ON t.id = it.tag_id
        GROUP BY t.id
        ORDER BY imageCount DESC
        LIMIT :limit
    """)
    suspend fun getHotTags(limit: Int = 15): List<TagWithCount>

    @Query("SELECT COUNT(*) FROM tags")
    suspend fun count(): Int
}

data class TagWithCount(
    val id: String,
    val name: String,
    val category: String,
    val parent_id: String?,
    val created_at: Long,
    val imageCount: Int
)
