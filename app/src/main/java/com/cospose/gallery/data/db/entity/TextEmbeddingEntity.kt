package com.cospose.gallery.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "text_embeddings")
data class TextEmbeddingEntity(
    @PrimaryKey val text: String,
    val embedding: String
)
