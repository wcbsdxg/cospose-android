package com.cospose.gallery.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.AnnotationDao
import com.cospose.gallery.data.db.dao.CommentDao
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.dao.LikeDao
import com.cospose.gallery.data.db.dao.RatingDao
import com.cospose.gallery.data.db.dao.TagDao
import com.cospose.gallery.data.db.entity.AnnotationEntity
import com.cospose.gallery.data.db.entity.CommentEntity
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.data.db.entity.ImageTagEntity
import com.cospose.gallery.data.db.entity.TagEntity
import com.cospose.gallery.data.remote.ApiService
import com.cospose.gallery.scoring.ScoringEngine
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

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val imageDao: ImageDao,
    private val tagDao: TagDao,
    private val likeDao: LikeDao,
    private val ratingDao: RatingDao,
    private val commentDao: CommentDao,
    private val annotationDao: AnnotationDao,
    private val apiService: ApiService
) : ViewModel() {

    private val imageId: String = savedStateHandle["imageId"] ?: ""

    private val _image = MutableStateFlow<ImageEntity?>(null)
    val image: StateFlow<ImageEntity?> = _image

    private val _manualTags = MutableStateFlow<List<TagEntity>>(emptyList())
    val manualTags: StateFlow<List<TagEntity>> = _manualTags

    private val _aiTags = MutableStateFlow<List<TagEntity>>(emptyList())
    val aiTags: StateFlow<List<TagEntity>> = _aiTags

    private val _comments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val comments: StateFlow<List<CommentEntity>> = _comments

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    private val _userRating = MutableStateFlow(0)
    val userRating: StateFlow<Int> = _userRating

    private val _relatedImages = MutableStateFlow<List<ImageEntity>>(emptyList())
    val relatedImages: StateFlow<List<ImageEntity>> = _relatedImages

    private val _allTags = MutableStateFlow<List<TagEntity>>(emptyList())
    val allTags: StateFlow<List<TagEntity>> = _allTags

    private val _annotations = MutableStateFlow<List<AnnotationEntity>>(emptyList())
    val annotations: StateFlow<List<AnnotationEntity>> = _annotations

    // Default user for local mode
    private val currentUserId = "local-user"

    init {
        if (imageId.isNotEmpty()) {
            loadAll()
        }
    }

    private fun loadAll() {
        viewModelScope.launch {
            _image.value = imageDao.getById(imageId)
            loadTags()
            loadComments()
            checkLiked()
            loadUserRating()
            loadRelated()
            loadAllTags()
            loadAnnotations()
        }
    }

    private suspend fun loadAllTags() {
        _allTags.value = tagDao.getAll()
    }

    private suspend fun loadAnnotations() {
        _annotations.value = annotationDao.getByImage(imageId)
    }

    fun addAnnotation(content: String, xRatio: Float, yRatio: Float) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            annotationDao.upsert(
                AnnotationEntity(
                    id = UUID.randomUUID().toString(),
                    imageId = imageId,
                    content = content,
                    xRatio = xRatio,
                    yRatio = yRatio,
                    createdAt = now,
                    updatedAt = now
                )
            )
            loadAnnotations()
        }
    }

    fun deleteAnnotation(annotationId: String) {
        viewModelScope.launch {
            annotationDao.deleteById(annotationId)
            loadAnnotations()
        }
    }

    fun addTagToImage(tagId: String) {
        viewModelScope.launch {
            tagDao.upsertImageTag(
                ImageTagEntity(
                    imageId = imageId,
                    tagId = tagId,
                    source = "MANUAL",
                    confidence = null
                )
            )
            loadTags()
        }
    }

    fun removeTagFromImage(tagId: String) {
        viewModelScope.launch {
            tagDao.deleteImageTag(imageId, tagId)
            loadTags()
        }
    }

    private suspend fun loadTags() {
        val allTags = tagDao.getTagsForImage(imageId)
        val imageTags = tagDao.getImageTags(imageId)
        val aiTagIds = imageTags.filter { it.source == "AI" }.map { it.tagId }.toSet()
        _manualTags.value = allTags.filter { it.id !in aiTagIds }
        _aiTags.value = allTags.filter { it.id in aiTagIds }
    }

    private suspend fun loadComments() {
        _comments.value = commentDao.getByImage(imageId)
    }

    private suspend fun checkLiked() {
        _isLiked.value = likeDao.isLiked(currentUserId, imageId)
    }

    private suspend fun loadUserRating() {
        val rating = ratingDao.get(currentUserId, imageId)
        _userRating.value = rating?.score ?: 0
    }

    private suspend fun loadRelated() {
        // Simple related: images sharing tags, sorted by score
        val imageTags = tagDao.getImageTags(imageId)
        if (imageTags.isEmpty()) return
        val tagIds = imageTags.map { it.tagId }.toSet()
        val allImages = imageDao.getAll()
        val scored = allImages.filter { it.id != imageId }.map { img ->
            val imgTags = tagDao.getImageTags(img.id).map { it.tagId }.toSet()
            val overlap = imgTags.intersect(tagIds).size
            img to overlap
        }.filter { it.second > 0 }.sortedByDescending { it.second }
        _relatedImages.value = scored.take(10).map { it.first }
    }

    fun toggleLike() {
        viewModelScope.launch {
            val liked = _isLiked.value
            if (liked) {
                likeDao.delete(currentUserId, imageId)
            } else {
                likeDao.insert(
                    com.cospose.gallery.data.db.entity.LikeEntity(currentUserId, imageId)
                )
            }
            val count = likeDao.countByImage(imageId)
            imageDao.updateLikesCount(imageId, count)
            _isLiked.value = !liked
            _image.value = imageDao.getById(imageId)
            updateScore()
        }
    }

    fun submitRating(score: Int) {
        viewModelScope.launch {
            ratingDao.upsert(
                com.cospose.gallery.data.db.entity.RatingEntity(currentUserId, imageId, score)
            )
            val avg = ratingDao.getAverage(imageId) ?: 0f
            val count = ratingDao.countByImage(imageId)
            imageDao.updateRating(imageId, avg, count)
            _userRating.value = score
            _image.value = imageDao.getById(imageId)
            updateScore()
        }
    }

    fun addComment(content: String) {
        viewModelScope.launch {
            val comment = CommentEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                imageId = imageId,
                content = content
            )
            commentDao.upsert(comment)
            val count = commentDao.countByImage(imageId)
            imageDao.updateCommentsCount(imageId, count)
            loadComments()
            _image.value = imageDao.getById(imageId)
            updateScore()
        }
    }

    private suspend fun updateScore() {
        val img = imageDao.getById(imageId) ?: return
        val score = ScoringEngine.calculateScore(
            ratingAvg = img.ratingAvg,
            likesCount = img.likesCount,
            commentsCount = img.commentsCount,
            createdAt = img.createdAt
        )
        imageDao.updateScore(imageId, score)
    }

    /**
     * 通过服务器端 AI 重新识别标签
     * @param mode "all" = 全部重新识别, "additive" = 仅追加
     */
    fun reclassify(mode: String) {
        viewModelScope.launch {
            val img = _image.value ?: return@launch
            try {
                val file = File(img.filePath)
                if (!file.exists()) return@launch

                val requestFile = file.asRequestBody("image/jpeg".toMediaType())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = apiService.classifyImageDirect(filePart)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (mode == "all") {
                        // Delete existing AI tags
                        tagDao.deleteAITags(imageId)
                    }

                    // Add new AI tags
                    for (category in body.categories) {
                        for (result in category.results) {
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

                    // Reload tags
                    loadTags()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
