package com.cospose.gallery.ui.smartfolder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.dao.SmartFolderDao
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.data.db.entity.SmartFolderEntity
import com.cospose.gallery.ui.home.FilterState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SmartFolderViewModel @Inject constructor(
    private val smartFolderDao: SmartFolderDao,
    private val imageDao: ImageDao
) : ViewModel() {

    private val _smartFolders = MutableStateFlow<List<SmartFolderEntity>>(emptyList())
    val smartFolders: StateFlow<List<SmartFolderEntity>> = _smartFolders

    private val _currentFolder = MutableStateFlow<SmartFolderEntity?>(null)
    val currentFolder: StateFlow<SmartFolderEntity?> = _currentFolder

    private val _matchingImages = MutableStateFlow<List<ImageEntity>>(emptyList())
    val matchingImages: StateFlow<List<ImageEntity>> = _matchingImages

    private val gson = Gson()

    init {
        loadSmartFolders()
    }

    private fun loadSmartFolders() {
        viewModelScope.launch {
            smartFolderDao.observeAll().collect { folders ->
                _smartFolders.value = folders
            }
        }
    }

    fun loadFolder(folderId: String) {
        viewModelScope.launch {
            val folder = smartFolderDao.getById(folderId)
            _currentFolder.value = folder
            folder?.let { loadMatchingImages(it) }
        }
    }

    private suspend fun loadMatchingImages(folder: SmartFolderEntity) {
        try {
            val filterState = gson.fromJson(folder.rules, FilterState::class.java)
            val allImages = imageDao.getAll()

            val filtered = allImages.filter { image ->
                var matches = true

                filterState.minRating?.let { min ->
                    if (image.ratingAvg < min) matches = false
                }
                filterState.maxRating?.let { max ->
                    if (image.ratingAvg > max) matches = false
                }
                filterState.mimeType?.let { mime ->
                    if (image.mimeType != mime) matches = false
                }
                filterState.sinceTimestamp?.let { since ->
                    if (image.createdAt < since) matches = false
                }

                matches
            }

            _matchingImages.value = filtered
        } catch (e: Exception) {
            e.printStackTrace()
            _matchingImages.value = emptyList()
        }
    }

    fun createSmartFolder(name: String, filterState: FilterState) {
        viewModelScope.launch {
            val folder = SmartFolderEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                rules = gson.toJson(filterState),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            smartFolderDao.upsert(folder)
        }
    }

    fun deleteSmartFolder(folderId: String) {
        viewModelScope.launch {
            smartFolderDao.deleteById(folderId)
        }
    }
}
