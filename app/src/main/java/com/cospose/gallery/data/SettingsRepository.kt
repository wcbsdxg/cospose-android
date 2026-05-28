package com.cospose.gallery.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cospose_settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")
        val SERVER_URL = stringPreferencesKey("server_url")
        val SHOW_TAGS = booleanPreferencesKey("show_tags")
        val COMPRESS_UPLOAD = booleanPreferencesKey("compress_upload")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        try {
            ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: "SYSTEM")
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    val dynamicColor: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DYNAMIC_COLOR] ?: true
    }

    val gridColumns: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.GRID_COLUMNS] ?: 2
    }

    val syncEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SYNC_ENABLED] ?: true
    }

    val serverUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.SERVER_URL] ?: "https://cospose.com"
    }

    val showTags: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_TAGS] ?: true
    }

    val compressUpload: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.COMPRESS_UPLOAD] ?: true
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setGridColumns(columns: Int) {
        context.dataStore.edit { it[Keys.GRID_COLUMNS] = columns }
    }

    suspend fun setSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SYNC_ENABLED] = enabled }
    }

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[Keys.SERVER_URL] = url }
    }

    suspend fun setShowTags(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_TAGS] = show }
    }

    suspend fun setCompressUpload(compress: Boolean) {
        context.dataStore.edit { it[Keys.COMPRESS_UPLOAD] = compress }
    }
}
