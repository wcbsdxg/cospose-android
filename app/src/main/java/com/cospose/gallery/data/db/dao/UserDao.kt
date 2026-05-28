package com.cospose.gallery.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cospose.gallery.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY created_at DESC LIMIT 1")
    suspend fun getLocalUser(): UserEntity?

    @Query("SELECT * FROM users ORDER BY created_at DESC LIMIT 1")
    fun observeLocalUser(): Flow<UserEntity?>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}
