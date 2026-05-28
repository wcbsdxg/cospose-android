package com.cospose.gallery.ui.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.dao.TagDao
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.data.db.entity.ImageTagEntity
import com.cospose.gallery.data.db.entity.TagEntity
import com.cospose.gallery.data.remote.ApiService
import com.cospose.gallery.scoring.ScoringEngine
import com.cospose.gallery.storage.ImageStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID
import javax.inject.Inject

data class SelectedImage(
    val uri: Uri,
    val previewPath: String? = null
)

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val imageStorage: ImageStorage,
    private val imageDao: ImageDao,
    private val tagDao: TagDao,
    private val apiService: ApiService
) : ViewModel() {

    private val _selectedImages = MutableStateFlow<List<SelectedImage>>(emptyList())
    val selectedImages: StateFlow<List<SelectedImage>> = _selectedImages

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _selectedTags = MutableStateFlow<List<TagEntity>>(emptyList())
    val selectedTags: StateFlow<List<TagEntity>> = _selectedTags

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    private val _uploadDone = MutableStateFlow(false)
    val uploadDone: StateFlow<Boolean> = _uploadDone

    private val _aiStatus = MutableStateFlow("")
    val aiStatus: StateFlow<String> = _aiStatus

    private val currentUserId = "local-user"

    fun addImages(uris: List<Uri>) {
        _selectedImages.value = uris.map { SelectedImage(it) }
        if (_title.value.isBlank() && uris.isNotEmpty()) {
            val fileName = uris.last().lastPathSegment ?: "未命名"
            _title.value = fileName.substringBeforeLast(".")
        }
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value.filter { it.uri != uri }
    }

    fun setTitle(value: String) { _title.value = value }
    fun setDescription(value: String) { _description.value = value }

    fun addTag(tag: TagEntity) {
        if (_selectedTags.value.none { it.id == tag.id }) {
            _selectedTags.value = _selectedTags.value + tag
        }
    }

    fun removeTag(tag: TagEntity) {
        _selectedTags.value = _selectedTags.value.filter { it.id != tag.id }
    }

    fun upload() {
        val images = _selectedImages.value
        if (images.isEmpty()) return

        viewModelScope.launch {
            _isUploading.value = true
            val total = images.size

            images.forEachIndexed { index, selected ->
                val saved = imageStorage.saveImage(selected.uri)
                if (saved != null) {
                    val imageEntity = ImageEntity(
                        id = saved.id,
                        userId = currentUserId,
                        title = _title.value.ifBlank { "未命名" },
                        description = _description.value.ifBlank { null },
                        filePath = saved.filePath,
                        thumbnailPath = saved.thumbnailPath,
                        mediumPath = saved.mediumPath,
                        width = saved.width,
                        height = saved.height,
                        fileSize = saved.fileSize,
                        mimeType = saved.mimeType,
                        score = ScoringEngine.calculateScore(0f, 0, 0, System.currentTimeMillis())
                    )
                    imageDao.upsert(imageEntity)

                    // Save manual tags
                    _selectedTags.value.forEach { tag ->
                        tagDao.upsertImageTag(
                            ImageTagEntity(imageId = saved.id, tagId = tag.id, source = "MANUAL")
                        )
                    }

                    // Trigger server-side AI classification
                    _aiStatus.value = "AI 正在识别第 ${index + 1} 张..."
                    classifyOnServer(saved.id, saved.filePath)
                }
                _uploadProgress.value = (index + 1).toFloat() / total
            }

            _isUploading.value = false
            _uploadDone.value = true
            _aiStatus.value = "上传完成，AI 识别已启动"
        }
    }

    private suspend fun classifyOnServer(imageId: String, filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) return

            val requestFile = file.asRequestBody("image/jpeg".toMediaType())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.classifyImageDirect(filePart)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                for (category in body.categories) {
                    for (result in category.results) {
                        // Upsert tag
                        val tagId = UUID.nameUUIDFromBytes("${result.label}-${category.name}".toByteArray()).toString()
                        tagDao.upsertTag(
                            TagEntity(id = tagId, name = result.label, category = category.name)
                        )
                        tagDao.upsertImageTag(
                            ImageTagEntity(
                                imageId = imageId,
                                tagId = tagId,
                                source = "AI",
                                confidence = result.similarity
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // AI failure shouldn't block upload
        }
    }
}
