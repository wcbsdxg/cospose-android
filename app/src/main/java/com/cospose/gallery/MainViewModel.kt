package com.cospose.gallery

import androidx.lifecycle.ViewModel
import com.cospose.gallery.data.SettingsRepository
import com.cospose.gallery.data.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: Flow<ThemeMode> = settingsRepository.themeMode

    val dynamicColor: Flow<Boolean> = settingsRepository.dynamicColor
}
