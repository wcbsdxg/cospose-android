package com.cospose.gallery.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.SettingsRepository
import com.cospose.gallery.data.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val dynamicColor: StateFlow<Boolean> = settingsRepository.dynamicColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val gridColumns: StateFlow<Int> = settingsRepository.gridColumns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    val syncEnabled: StateFlow<Boolean> = settingsRepository.syncEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val serverUrl: StateFlow<String> = settingsRepository.serverUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "https://cospose.com")

    val showTags: StateFlow<Boolean> = settingsRepository.showTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val compressUpload: StateFlow<Boolean> = settingsRepository.compressUpload
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColor(enabled) }
    }

    fun setGridColumns(columns: Int) {
        viewModelScope.launch { settingsRepository.setGridColumns(columns) }
    }

    fun setSyncEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSyncEnabled(enabled) }
    }

    fun setServerUrl(url: String) {
        viewModelScope.launch { settingsRepository.setServerUrl(url) }
    }

    fun setShowTags(show: Boolean) {
        viewModelScope.launch { settingsRepository.setShowTags(show) }
    }

    fun setCompressUpload(compress: Boolean) {
        viewModelScope.launch { settingsRepository.setCompressUpload(compress) }
    }
}
