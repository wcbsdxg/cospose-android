package com.cospose.gallery.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.dao.TagDao
import com.cospose.gallery.data.db.dao.TagWithCount
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.data.db.entity.ImageTagEntity
import com.cospose.gallery.data.db.entity.TagEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class SortMode(val label: String, val apiValue: String) {
    RECOMMENDED("推荐", "recommended"),
    NEWEST("最新", "newest"),
    POPULAR("最热", "popular"),
    TOP_RATED("最高评分", "top_rated")
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val imageDao: ImageDao,
    private val tagDao: TagDao
) : ViewModel() {

    private val _sortMode = MutableStateFlow(SortMode.RECOMMENDED)
    val sortMode: StateFlow<SortMode> = _sortMode

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    private val _hotTags = MutableStateFlow<List<TagWithCount>>(emptyList())
    val hotTags: StateFlow<List<TagWithCount>> = _hotTags

    private val _allTags = MutableStateFlow<List<TagEntity>>(emptyList())
    val allTags: StateFlow<List<TagEntity>> = _allTags

    @OptIn(ExperimentalCoroutinesApi::class)
    val images: Flow<PagingData<ImageEntity>> = _filterState.flatMapLatest { filter ->
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false)
        ) {
            if (filter.isActive) {
                when (_sortMode.value) {
                    SortMode.RECOMMENDED -> imageDao.pagingFiltered(
                        minRating = filter.minRating,
                        maxRating = filter.maxRating,
                        minWidth = filter.minWidth,
                        minHeight = filter.minHeight,
                        mimeType = filter.mimeType,
                        since = filter.sinceTimestamp,
                        aspectRatio = filter.aspectRatio?.ratio,
                        dominantColor = filter.dominantColor
                    )
                    SortMode.NEWEST -> imageDao.pagingFilteredNewest(
                        minRating = filter.minRating,
                        maxRating = filter.maxRating,
                        minWidth = filter.minWidth,
                        minHeight = filter.minHeight,
                        mimeType = filter.mimeType,
                        since = filter.sinceTimestamp,
                        aspectRatio = filter.aspectRatio?.ratio,
                        dominantColor = filter.dominantColor
                    )
                    SortMode.POPULAR -> imageDao.pagingFilteredPopular(
                        minRating = filter.minRating,
                        maxRating = filter.maxRating,
                        minWidth = filter.minWidth,
                        minHeight = filter.minHeight,
                        mimeType = filter.mimeType,
                        since = filter.sinceTimestamp,
                        aspectRatio = filter.aspectRatio?.ratio,
                        dominantColor = filter.dominantColor
                    )
                    SortMode.TOP_RATED -> imageDao.pagingFilteredTopRated(
                        minRating = filter.minRating,
                        maxRating = filter.maxRating,
                        minWidth = filter.minWidth,
                        minHeight = filter.minHeight,
                        mimeType = filter.mimeType,
                        since = filter.sinceTimestamp,
                        aspectRatio = filter.aspectRatio?.ratio,
                        dominantColor = filter.dominantColor
                    )
                }
            } else {
                when (_sortMode.value) {
                    SortMode.RECOMMENDED -> imageDao.pagingByScore()
                    SortMode.NEWEST -> imageDao.pagingByNewest()
                    SortMode.POPULAR -> imageDao.pagingByPopular()
                    SortMode.TOP_RATED -> imageDao.pagingByRating()
                }
            }
        }.flow
    }.cachedIn(viewModelScope)

    init {
        loadHotTags()
        loadAllTags()
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
        // Trigger re-evaluation by updating filter state
        _filterState.value = _filterState.value.copy()
    }

    fun setFilterState(state: FilterState) {
        _filterState.value = state
    }

    fun clearFilters() {
        _filterState.value = FilterState()
    }

    fun batchAddTags(imageIds: List<String>, tagIds: List<String>) {
        viewModelScope.launch {
            imageIds.forEach { imageId ->
                tagIds.forEach { tagId ->
                    tagDao.upsertImageTag(
                        ImageTagEntity(
                            imageId = imageId,
                            tagId = tagId,
                            source = "MANUAL",
                            confidence = null
                        )
                    )
                }
            }
        }
    }

    fun batchRate(imageIds: List<String>, score: Int) {
        viewModelScope.launch {
            imageIds.forEach { imageId ->
                val ratingEntity = com.cospose.gallery.data.db.entity.RatingEntity(
                    userId = "local-user",
                    imageId = imageId,
                    score = score
                )
                // Update rating
                imageDao.updateRating(imageId, score.toFloat(), 1)
            }
        }
    }

    fun batchDelete(imageIds: List<String>) {
        viewModelScope.launch {
            imageIds.forEach { imageId ->
                imageDao.deleteById(imageId)
            }
        }
    }

    private fun loadHotTags() {
        viewModelScope.launch {
            _hotTags.value = tagDao.getHotTags(15)
        }
    }

    private fun loadAllTags() {
        viewModelScope.launch {
            _allTags.value = tagDao.getAll()
        }
    }
}
