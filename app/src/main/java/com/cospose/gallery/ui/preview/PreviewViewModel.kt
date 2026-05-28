package com.cospose.gallery.ui.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.dao.LikeDao
import com.cospose.gallery.data.db.entity.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val imageDao: ImageDao,
    private val likeDao: LikeDao
) : ViewModel() {

    private val imageIds: List<String> = savedStateHandle.get<String>("imageIds")
        ?.split(",") ?: emptyList()

    private val startIndex: Int = savedStateHandle.get<Int>("startIndex") ?: 0

    private val _images = MutableStateFlow<List<ImageEntity>>(emptyList())
    val images: StateFlow<List<ImageEntity>> = _images

    private val _currentIndex = MutableStateFlow(startIndex)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    private val currentUserId = "local-user"

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            val loaded = imageIds.mapNotNull { imageDao.getById(it) }
            _images.value = loaded
            if (loaded.isNotEmpty()) {
                checkLiked(loaded[startIndex.coerceIn(0, loaded.lastIndex)].id)
            }
        }
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
        val images = _images.value
        if (index in images.indices) {
            checkLiked(images[index].id)
        }
    }

    fun toggleLike() {
        viewModelScope.launch {
            val images = _images.value
            val index = _currentIndex.value
            if (index !in images.indices) return@launch

            val imageId = images[index].id
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

            // Refresh image data
            _images.value = _images.value.toMutableList().apply {
                set(index, imageDao.getById(imageId) ?: get(index))
            }
        }
    }

    private fun checkLiked(imageId: String) {
        viewModelScope.launch {
            _isLiked.value = likeDao.isLiked(currentUserId, imageId)
        }
    }
}
