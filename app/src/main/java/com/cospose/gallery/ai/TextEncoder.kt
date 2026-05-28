package com.cospose.gallery.ai

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 文本编码器 — 委托给 ClipEngine（服务器端）
 * 本地不再缓存嵌入向量，全部由服务器处理
 */
@Singleton
class TextEncoder @Inject constructor(
    private val clipEngine: ClipEngine
) {
    suspend fun encode(text: String): FloatArray? {
        return clipEngine.encodeText(text)
    }

    suspend fun encodeBatch(texts: List<String>): List<FloatArray?> {
        return texts.map { encode(it) }
    }
}
