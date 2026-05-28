package com.cospose.gallery.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cospose.gallery.data.db.dao.UserDao
import com.cospose.gallery.data.db.entity.UserEntity
import com.cospose.gallery.data.remote.ApiService
import com.cospose.gallery.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            val user = userDao.getLocalUser()
            _currentUser.value = user
            _isLoggedIn.value = user != null
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.login(mapOf("email" to email, "password" to password))
                if (response.isSuccessful) {
                    val auth = response.body()!!
                    val user = UserEntity(
                        id = auth.user.id,
                        name = auth.user.name,
                        email = auth.user.email,
                        passwordHash = null,
                        avatarPath = auth.user.avatar,
                        bio = auth.user.bio,
                        role = auth.user.role,
                        syncStatus = "SYNCED"
                    )
                    userDao.upsert(user)
                    syncManager.setAuthToken("Bearer ${auth.token}")
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _error.value = null
                } else {
                    _error.value = "邮箱或密码错误"
                }
            } catch (e: Exception) {
                _error.value = "网络错误，请检查连接"
                // Fallback: create local user
                createLocalUser(email, email.substringBefore("@"))
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.register(
                    mapOf("name" to name, "email" to email, "password" to password)
                )
                if (response.isSuccessful) {
                    login(email, password)
                } else {
                    _error.value = "注册失败，邮箱可能已存在"
                }
            } catch (e: Exception) {
                _error.value = "网络错误，创建本地用户"
                createLocalUser(email, name)
            }
        }
    }

    private suspend fun createLocalUser(email: String, name: String) {
        val user = UserEntity(
            id = "local-${email.hashCode()}",
            name = name,
            email = email,
            passwordHash = null,
            avatarPath = null,
            bio = null,
            syncStatus = "PENDING"
        )
        userDao.upsert(user)
        _currentUser.value = user
        _isLoggedIn.value = true
    }

    fun logout() {
        viewModelScope.launch {
            syncManager.setAuthToken(null)
            _currentUser.value = null
            _isLoggedIn.value = false
        }
    }
}
