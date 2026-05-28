package com.cospose.gallery.ai

import com.cospose.gallery.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CLIP 引擎 — 调用服务器端 CLIP 微服务
 * 不再使用本地 ONNX Runtime
 */
@Singleton
class ClipEngine @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * 通过服务器编码图像为向量
     */
    suspend fun encodeImage(imagePath: String): FloatArray? = withContext(Dispatchers.IO) {
        try {
            val file = File(imagePath)
            if (!file.exists()) return@withContext null

            val requestFile = file.asRequestBody("image/jpeg".toMediaType())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.embedImage(filePart)
            if (response.isSuccessful) {
                response.body()?.embedding?.toFloatArray()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 通过服务器编码文本为向量
     */
    suspend fun encodeText(text: String): FloatArray? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.embedText(text)
            if (response.isSuccessful) {
                response.body()?.embedding?.toFloatArray()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
