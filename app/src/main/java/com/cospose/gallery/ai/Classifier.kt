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

@Singleton
class Classifier @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun classifyImage(imagePath: String): List<CategoryResult> = withContext(Dispatchers.IO) {
        try {
            val file = File(imagePath)
            if (!file.exists()) return@withContext emptyList()

            val requestFile = file.asRequestBody("image/jpeg".toMediaType())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.classifyImageDirect(filePart)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.categories.map { cat ->
                    CategoryResult(
                        category = cat.name,
                        categoryName = cat.name,
                        results = cat.results.map { r ->
                            LabelResult(
                                name = r.label,
                                score = r.score,
                                similarity = r.similarity
                            )
                        }
                    )
                }
            } else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

data class CategoryResult(
    val category: String,
    val categoryName: String,
    val results: List<LabelResult>
)

data class LabelResult(
    val name: String,
    val score: Float,
    val similarity: Float
)
