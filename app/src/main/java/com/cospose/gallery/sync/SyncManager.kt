package com.cospose.gallery.sync

import com.cospose.gallery.data.db.dao.CommentDao
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.dao.RatingDao
import com.cospose.gallery.data.db.dao.TagDao
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.data.remote.ApiService
import com.cospose.gallery.data.remote.SyncCommentDto
import com.cospose.gallery.data.remote.SyncImageDto
import com.cospose.gallery.data.remote.SyncPushRequest
import com.cospose.gallery.data.remote.SyncRatingDto
import com.cospose.gallery.storage.ImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncState {
    IDLE, SYNCING, SUCCESS, ERROR
}

@Singleton
class SyncManager @Inject constructor(
    private val apiService: ApiService,
    private val imageDao: ImageDao,
    private val commentDao: CommentDao,
    private val ratingDao: RatingDao,
    private val tagDao: TagDao,
    private val imageStorage: ImageStorage
) {
    private val _state = MutableStateFlow(SyncState.IDLE)
    val state: StateFlow<SyncState> = _state

    private val _serverReachable = MutableStateFlow(false)
    val serverReachable: StateFlow<Boolean> = _serverReachable

    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    suspend fun checkServer() = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTags()
            _serverReachable.value = response.isSuccessful
        } catch (_: Exception) {
            _serverReachable.value = false
        }
    }

    suspend fun syncAll() = withContext(Dispatchers.IO) {
        val token = authToken ?: return@withContext
        _state.value = SyncState.SYNCING

        try {
            // 1. Push pending images
            val pendingImages = imageDao.getPendingSync()
            for (image in pendingImages) {
                syncImage(token, image)
            }

            // 2. Push pending comments
            val pendingComments = commentDao.getPendingSync()
            // TODO: batch push comments

            // 3. Pull new data from server
            // TODO: pull since last sync timestamp

            _state.value = SyncState.SUCCESS
        } catch (e: Exception) {
            _state.value = SyncState.ERROR
            e.printStackTrace()
        }
    }

    private suspend fun syncImage(token: String, image: ImageEntity) {
        val file = File(image.filePath)
        if (!file.exists()) return

        val requestFile = file.asRequestBody("image/jpeg".toMediaType())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val imageIdBody = image.id.toRequestBody("text/plain".toMediaType())

        val response = apiService.syncImage(token, filePart, imageIdBody)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                imageDao.markSynced(image.id, body.serverId, body.url)
            }
        }
    }
}
