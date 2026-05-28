package com.cospose.gallery.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cospose.gallery.data.db.AppDatabase
import com.cospose.gallery.data.db.dao.AnnotationDao
import com.cospose.gallery.data.db.dao.BoardDao
import com.cospose.gallery.data.db.dao.CommentDao
import com.cospose.gallery.data.db.dao.ImageDao
import com.cospose.gallery.data.db.dao.LikeDao
import com.cospose.gallery.data.db.dao.RatingDao
import com.cospose.gallery.data.db.dao.SmartFolderDao
import com.cospose.gallery.data.db.dao.TagDao
import com.cospose.gallery.data.db.dao.TextEmbeddingDao
import com.cospose.gallery.data.db.dao.UserDao
import com.cospose.gallery.data.remote.ApiService
import com.cospose.gallery.storage.ImageStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    try {
                        val now = System.currentTimeMillis()
                        db.execSQL(
                            "INSERT OR IGNORE INTO users (id, name, role, created_at, updated_at) VALUES ('local-user', '本地用户', 'USER', $now, $now)"
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("AppDatabase", "Failed to insert local-user", e)
                    }
                }
            })
            .build()
    }

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideImageDao(db: AppDatabase): ImageDao = db.imageDao()
    @Provides fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()
    @Provides fun provideCommentDao(db: AppDatabase): CommentDao = db.commentDao()
    @Provides fun provideRatingDao(db: AppDatabase): RatingDao = db.ratingDao()
    @Provides fun provideLikeDao(db: AppDatabase): LikeDao = db.likeDao()
    @Provides fun provideBoardDao(db: AppDatabase): BoardDao = db.boardDao()
    @Provides fun provideTextEmbeddingDao(db: AppDatabase): TextEmbeddingDao = db.textEmbeddingDao()
    @Provides fun provideSmartFolderDao(db: AppDatabase): SmartFolderDao = db.smartFolderDao()
    @Provides fun provideAnnotationDao(db: AppDatabase): AnnotationDao = db.annotationDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // 模拟器 localhost，真机需配置
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideImageStorage(@ApplicationContext context: Context): ImageStorage {
        return ImageStorage(context)
    }
}
