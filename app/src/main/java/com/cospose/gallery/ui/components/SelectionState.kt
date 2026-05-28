package com.cospose.gallery.ui.components

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf

@Stable
class SelectionState {
    private val _selectedIds = mutableStateListOf<String>()
    val selectedIds: Set<String> get() = _selectedIds.toSet()

    val isSelectionMode: Boolean get() = _selectedIds.isNotEmpty()
    val selectedCount: Int get() = _selectedIds.size

    fun toggle(imageId: String) {
        if (_selectedIds.contains(imageId)) {
            _selectedIds.remove(imageId)
        } else {
            _selectedIds.add(imageId)
        }
    }

    fun isSelected(imageId: String): Boolean {
        return _selectedIds.contains(imageId)
    }

    fun selectAll(imageIds: List<String>) {
        _selectedIds.clear()
        _selectedIds.addAll(imageIds)
    }

    fun clear() {
        _selectedIds.clear()
    }

    fun add(imageId: String) {
        if (!_selectedIds.contains(imageId)) {
            _selectedIds.add(imageId)
        }
    }

    fun remove(imageId: String) {
        _selectedIds.remove(imageId)
    }
}
