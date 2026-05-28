package com.cospose.gallery.ui.compare

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.entity.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val imageDao: ImageDao
) : ViewModel() {

    private val imageId1: String = savedStateHandle.get<String>("imageId1") ?: ""
    private val imageId2: String = savedStateHandle.get<String>("imageId2") ?: ""

    private val _image1 = MutableStateFlow<ImageEntity?>(null)
    val image1: StateFlow<ImageEntity?> = _image1

    private val _image2 = MutableStateFlow<ImageEntity?>(null)
    val image2: StateFlow<ImageEntity?> = _image2

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun loadImages() {
        _isLoading.value = true
        _image1.value = imageDao.getById(imageId1)
        _image2.value = imageDao.getById(imageId2)
        _isLoading.value = false
    }
}
