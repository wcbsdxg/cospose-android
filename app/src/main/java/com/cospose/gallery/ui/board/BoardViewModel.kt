package com.cospose.gallery.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.BoardDao
import com.cospose.gallery.data.db.entity.BoardEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardDao: BoardDao
) : ViewModel() {

    private val currentUserId = "local-user"

    val boards: StateFlow<List<BoardEntity>> = boardDao
        .observeByUser(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun createBoard(name: String) {
        if (name.isBlank()) {
            _errorMessage.value = "收藏板名称不能为空"
            return
        }
        viewModelScope.launch {
            try {
                boardDao.upsert(
                    BoardEntity(
                        id = UUID.randomUUID().toString(),
                        userId = currentUserId,
                        name = name
                    )
                )
            } catch (e: Exception) {
                Log.e("BoardViewModel", "创建收藏板失败", e)
                _errorMessage.value = "创建收藏板失败: ${e.localizedMessage}"
            }
        }
    }

    fun deleteBoard(id: String) {
        viewModelScope.launch {
            try {
                boardDao.deleteById(id)
            } catch (e: Exception) {
                Log.e("BoardViewModel", "删除收藏板失败", e)
                _errorMessage.value = "删除收藏板失败: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
