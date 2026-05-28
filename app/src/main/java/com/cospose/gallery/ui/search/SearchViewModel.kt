package com.cospose.gallery.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

enum class SearchMode(val label: String) {
    KEYWORD("关键词搜索"),
    SEMANTIC("语义搜索"),
    IMAGE("以图搜图")
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imageDao: ImageDao,
    private val apiService: ApiService
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _searchMode = MutableStateFlow(SearchMode.KEYWORD)
    val searchMode: StateFlow<SearchMode> = _searchMode

    private val _results = MutableStateFlow<List<ImageEntity>>(emptyList())
    val results: StateFlow<List<ImageEntity>> = _results

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched

    fun setQuery(value: String) { _query.value = value }
    fun setMode(mode: SearchMode) { _searchMode.value = mode }

    fun search() {
        val q = _query.value.trim()
        if (q.isBlank()) return

        viewModelScope.launch {
            _isSearching.value = true
            _hasSearched.value = true

            when (_searchMode.value) {
                SearchMode.KEYWORD -> searchKeyword(q)
                SearchMode.SEMANTIC -> searchSemantic(q)
                SearchMode.IMAGE -> { /* handled separately */ }
            }

            _isSearching.value = false
        }
    }

    private suspend fun searchKeyword(query: String) {
        // Try server first
        try {
            val response = apiService.search(query, "keyword", 50)
            if (response.isSuccessful && response.body() != null) {
                val serverImages = response.body()!!.images
                _results.value = serverImages.map { dto ->
                    ImageEntity(
                        id = dto.id,
                        userId = dto.userId,
                        title = dto.title,
                        description = dto.description,
                        filePath = "",
                        width = dto.width,
                        height = dto.height,
                        mimeType = dto.mimeType,
                        likesCount = dto.likesCount,
                        commentsCount = dto.commentsCount,
                        ratingAvg = dto.ratingAvg,
                        ratingCount = dto.ratingCount,
                        score = dto.score
                    )
                }
                return
            }
        } catch (_: Exception) {}

        // Fallback: local search
        val allImages = imageDao.getAll()
        _results.value = allImages.filter { img ->
            img.title.contains(query, ignoreCase = true) ||
            img.description?.contains(query, ignoreCase = true) == true
        }
    }

    private suspend fun searchSemantic(query: String) {
        // Call server-side semantic search via existing /api/search?mode=semantic
        try {
            val response = apiService.search(query, "semantic", 50)
            if (response.isSuccessful && response.body() != null) {
                val serverImages = response.body()!!.images
                _results.value = serverImages.map { dto ->
                    ImageEntity(
                        id = dto.id,
                        userId = dto.userId,
                        title = dto.title,
                        description = dto.description,
                        filePath = "",
                        width = dto.width,
                        height = dto.height,
                        mimeType = dto.mimeType,
                        likesCount = dto.likesCount,
                        commentsCount = dto.commentsCount,
                        ratingAvg = dto.ratingAvg,
                        ratingCount = dto.ratingCount,
                        score = dto.score
                    )
                }
                return
            }
        } catch (_: Exception) {}

        // Fallback to keyword
        searchKeyword(query)
    }

    fun searchByImage(imagePath: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _hasSearched.value = true
            _searchMode.value = SearchMode.IMAGE

            try {
                val file = File(imagePath)
                if (!file.exists()) {
                    _isSearching.value = false
                    return@launch
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaType())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = apiService.imageSearch(filePart)
                if (response.isSuccessful && response.body() != null) {
                    val serverImages = response.body()!!.images
                    _results.value = serverImages.map { dto ->
                        ImageEntity(
                            id = dto.id,
                            userId = dto.userId,
                            title = dto.title,
                            description = dto.description,
                            filePath = "",
                            width = dto.width,
                            height = dto.height,
                            mimeType = dto.mimeType,
                            likesCount = dto.likesCount,
                            commentsCount = dto.commentsCount,
                            ratingAvg = dto.ratingAvg,
                            ratingCount = dto.ratingCount,
                            score = dto.score
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _isSearching.value = false
        }
    }

    fun searchByTag(tagName: String) {
        _query.value = tagName
        search()
    }
}
