package com.cospose.gallery.ui.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.BoardDao
import com.cospose.gallery.data.db.entity.BoardEntity
import com.cospose.gallery.data.db.entity.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val boardDao: BoardDao
) : ViewModel() {

    private val boardId: String = savedStateHandle["boardId"] ?: ""

    private val _board = MutableStateFlow<BoardEntity?>(null)
    val board: StateFlow<BoardEntity?> = _board

    private val _images = MutableStateFlow<List<ImageEntity>>(emptyList())
    val images: StateFlow<List<ImageEntity>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        if (boardId.isNotEmpty()) {
            loadBoard()
        }
    }

    private fun loadBoard() {
        viewModelScope.launch {
            _isLoading.value = true
            _board.value = boardDao.getById(boardId)
            loadImages()
            _isLoading.value = false
        }
    }

    private suspend fun loadImages() {
        _images.value = boardDao.observeImagesInBoard(boardId).first()
    }

    fun removeImage(imageId: String) {
        viewModelScope.launch {
            boardDao.removeImage(boardId, imageId)
            loadImages()
        }
    }

    fun deleteBoard() {
        viewModelScope.launch {
            boardDao.deleteById(boardId)
        }
    }
}
