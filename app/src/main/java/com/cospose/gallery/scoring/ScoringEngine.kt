package com.cospose.gallery.scoring

import kotlin.math.pow

object ScoringEngine {

    /**
     * 计算推荐分（0-100）
     * rating 40% + likes 25% + freshness 20% + comments 15%
     */
    fun calculateScore(
        ratingAvg: Float,
        likesCount: Int,
        commentsCount: Int,
        createdAt: Long
    ): Float {
        val ratingScore = (ratingAvg / 5f) * 40f
        val likeScore = (likesCount.coerceAtMost(50) / 50f) * 25f
        val freshnessScore = freshnessDecay(createdAt) * 20f
        val commentScore = (commentsCount.coerceAtMost(20) / 20f) * 15f

        return ratingScore + likeScore + freshnessScore + commentScore
    }

    /**
     * 7 天半衰期的指数衰减
     */
    private fun freshnessDecay(createdAt: Long): Float {
        val ageDays = (System.currentTimeMillis() - createdAt) / (24 * 60 * 60 * 1000f)
        return 2f.pow(-ageDays / 7f)
    }
}
