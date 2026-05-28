package com.cospose.gallery.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.sqrt

object EmbeddingUtils {

    private val gson = Gson()

    /**
     * Parse a JSON string embedding into a FloatArray.
     */
    fun parseEmbedding(json: String): FloatArray? {
        return try {
            val type = object : TypeToken<List<Float>>() {}.type
            val list: List<Float> = gson.fromJson(json, type)
            list.toFloatArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calculate cosine similarity between two vectors.
     */
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f

        var dotProduct = 0f
        var normA = 0f
        var normB = 0f

        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }

        val denominator = sqrt(normA) * sqrt(normB)
        return if (denominator == 0f) 0f else dotProduct / denominator
    }

    /**
     * Find groups of similar images based on embedding similarity.
     * Returns groups of image IDs with their similarity scores.
     */
    fun findSimilarGroups(
        embeddings: List<Pair<String, FloatArray>>,
        threshold: Float = 0.85f
    ): List<List<Pair<String, Float>>> {
        val groups = mutableListOf<MutableList<Pair<String, Float>>>()
        val assigned = mutableSetOf<String>()

        for (i in embeddings.indices) {
            if (embeddings[i].first in assigned) continue

            val group = mutableListOf<Pair<String, Float>>()
            group.add(embeddings[i].first to 1.0f)
            assigned.add(embeddings[i].first)

            for (j in i + 1 until embeddings.size) {
                if (embeddings[j].first in assigned) continue

                val similarity = cosineSimilarity(embeddings[i].second, embeddings[j].second)
                if (similarity >= threshold) {
                    group.add(embeddings[j].first to similarity)
                    assigned.add(embeddings[j].first)
                }
            }

            if (group.size > 1) {
                groups.add(group)
            }
        }

        return groups
    }
}
