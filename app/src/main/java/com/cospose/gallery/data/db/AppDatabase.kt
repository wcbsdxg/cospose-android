package com.cospose.gallery.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.cospose.gallery.data.db.entity.AnnotationEntity
import com.cospose.gallery.data.db.entity.BoardEntity
import com.cospose.gallery.data.db.entity.BoardImageEntity
import com.cospose.gallery.data.db.entity.CommentEntity
import com.cospose.gallery.data.db.entity.ImageEntity
import com.cospose.gallery.data.db.entity.ImageTagEntity
import com.cospose.gallery.data.db.entity.LikeEntity
import com.cospose.gallery.data.db.entity.RatingEntity
import com.cospose.gallery.data.db.entity.SmartFolderEntity
import com.cospose.gallery.data.db.entity.TagEntity
import com.cospose.gallery.data.db.entity.TextEmbeddingEntity
import com.cospose.gallery.data.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ImageEntity::class,
        TagEntity::class,
        ImageTagEntity::class,
        LikeEntity::class,
        RatingEntity::class,
        CommentEntity::class,
        BoardEntity::class,
        BoardImageEntity::class,
        TextEmbeddingEntity::class,
        SmartFolderEntity::class,
        AnnotationEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao
    abstract fun tagDao(): TagDao
    abstract fun commentDao(): CommentDao
    abstract fun ratingDao(): RatingDao
    abstract fun likeDao(): LikeDao
    abstract fun boardDao(): BoardDao
    abstract fun textEmbeddingDao(): TextEmbeddingDao
    abstract fun smartFolderDao(): SmartFolderDao
    abstract fun annotationDao(): AnnotationDao

    companion object {
        const val DB_NAME = "cospose.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add dominant_color column to images
                db.execSQL("ALTER TABLE images ADD COLUMN dominant_color INTEGER DEFAULT NULL")

                // Create smart_folders table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS smart_folders (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        rules TEXT NOT NULL,
                        icon TEXT,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                """)

                // Create annotations table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS annotations (
                        id TEXT NOT NULL PRIMARY KEY,
                        image_id TEXT NOT NULL,
                        content TEXT NOT NULL,
                        x_ratio REAL,
                        y_ratio REAL,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL,
                        FOREIGN KEY(image_id) REFERENCES images(id) ON DELETE CASCADE
                    )
                """)

                // Create indices
                db.execSQL("CREATE INDEX IF NOT EXISTS index_tags_parent_id ON tags(parent_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_annotations_image_id ON annotations(image_id)")
            }
        }
    }
}
