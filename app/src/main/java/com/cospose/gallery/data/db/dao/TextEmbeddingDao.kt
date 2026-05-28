package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cospose.gallery.data.db.entity.TextEmbeddingEntity

@Dao
interface TextEmbeddingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(entity: TextEmbeddingEntity)

    @Query("SELECT * FROM text_embeddings WHERE text = :text LIMIT 1")
    suspend fun get(text: String): TextEmbeddingEntity?

    @Query("SELECT * FROM text_embeddings")
    suspend fun getAll(): List<TextEmbeddingEntity>
}
