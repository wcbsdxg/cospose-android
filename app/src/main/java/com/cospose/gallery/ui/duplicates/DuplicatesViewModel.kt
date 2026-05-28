package com.cospose.gallery.ui.duplicates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.util.EmbeddingUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DuplicateGroup(
    val images: List<Pair<ImageEntity, Float>>
)

@HiltViewModel
class DuplicatesViewModel @Inject constructor(
    private val imageDao: ImageDao
) : ViewModel() {

    private val _duplicateGroups = MutableStateFlow<List<DuplicateGroup>>(emptyList())
    val duplicateGroups: StateFlow<List<DuplicateGroup>> = _duplicateGroups

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _similarityThreshold = MutableStateFlow(0.85f)
    val similarityThreshold: StateFlow<Float> = _similarityThreshold

    init {
        findDuplicates()
    }

    fun setThreshold(threshold: Float) {
        _similarityThreshold.value = threshold
        findDuplicates()
    }

    fun findDuplicates() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val imagesWithEmbedding = imageDao.getAllWithEmbedding()
                val embeddings = imagesWithEmbedding.mapNotNull { image ->
                    image.embedding?.let { json ->
                        EmbeddingUtils.parseEmbedding(json)?.let { vec ->
                            image.id to vec
                        }
                    }
                }

                val groups = EmbeddingUtils.findSimilarGroups(
                    embeddings,
                    threshold = _similarityThreshold.value
                )

                val imageMap = imagesWithEmbedding.associateBy { it.id }

                _duplicateGroups.value = groups.map { group ->
                    DuplicateGroup(
                        images = group.mapNotNull { (id, similarity) ->
                            imageMap[id]?.let { it to similarity }
                        }.sortedByDescending { it.second }
                    )
                }.sortedByDescending { it.images.size }
            } catch (e: Exception) {
                e.printStackTrace()
                _duplicateGroups.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    fun deleteImage(imageId: String) {
        viewModelScope.launch {
            imageDao.deleteById(imageId)
            findDuplicates()
        }
    }
}
