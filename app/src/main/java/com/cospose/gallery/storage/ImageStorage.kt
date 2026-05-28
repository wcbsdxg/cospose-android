package com.cospose.gallery.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

class ImageStorage @Inject constructor(
    private val context: Context
) {
    private val imagesDir: File
        get() = File(context.filesDir, "images").also { it.mkdirs() }

    fun saveImage(uri: Uri, maxWidth: Int = 1200): SavedImage? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val id = UUID.randomUUID().toString()
            val aspectRatio = original.height.toFloat() / original.width.toFloat()

            // Detect format from URI
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val formatInfo = getFormatInfo(mimeType)

            // Original (resized if too large)
            val originalBitmap = if (original.width > maxWidth) {
                Bitmap.createScaledBitmap(original, maxWidth, (maxWidth * aspectRatio).toInt(), true)
            } else {
                original
            }

            // Thumbnail (200px wide)
            val thumbWidth = 200
            val thumbHeight = (thumbWidth * aspectRatio).toInt()
            val thumbnail = Bitmap.createScaledBitmap(originalBitmap, thumbWidth, thumbHeight, true)

            // Medium (800px wide)
            val mediumWidth = 800
            val mediumHeight = (mediumWidth * aspectRatio).toInt()
            val medium = Bitmap.createScaledBitmap(originalBitmap, mediumWidth, mediumHeight, true)

            // Save dimensions before recycling
            val savedWidth = originalBitmap.width
            val savedHeight = originalBitmap.height

            // Save files with detected format
            val originalFile = File(imagesDir, "${id}_original.${formatInfo.extension}")
            val thumbFile = File(imagesDir, "${id}_thumb.${formatInfo.extension}")
            val mediumFile = File(imagesDir, "${id}_medium.${formatInfo.extension}")

            saveBitmap(originalBitmap, originalFile, formatInfo.format, formatInfo.quality)
            saveBitmap(thumbnail, thumbFile, formatInfo.format, formatInfo.quality)
            saveBitmap(medium, mediumFile, formatInfo.format, formatInfo.quality)

            // Recycle bitmaps
            if (originalBitmap !== original) originalBitmap.recycle()
            thumbnail.recycle()
            medium.recycle()
            original.recycle()

            SavedImage(
                id = id,
                filePath = originalFile.absolutePath,
                thumbnailPath = thumbFile.absolutePath,
                mediumPath = mediumFile.absolutePath,
                width = savedWidth,
                height = savedHeight,
                fileSize = originalFile.length(),
                mimeType = formatInfo.mimeType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImage(id: String, extension: String = "jpg") {
        File(imagesDir, "${id}_original.$extension").delete()
        File(imagesDir, "${id}_thumb.$extension").delete()
        File(imagesDir, "${id}_medium.$extension").delete()
    }

    fun getPath(id: String, type: String = "original", extension: String = "jpg"): String {
        return File(imagesDir, "${id}_${type}.$extension").absolutePath
    }

    private fun getFormatInfo(mimeType: String): FormatInfo {
        return when (mimeType) {
            "image/png" -> FormatInfo(
                format = Bitmap.CompressFormat.PNG,
                extension = "png",
                mimeType = "image/png",
                quality = 100 // PNG is lossless, quality is ignored
            )
            "image/webp" -> FormatInfo(
                format = Bitmap.CompressFormat.WEBP_LOSSY,
                extension = "webp",
                mimeType = "image/webp",
                quality = 90
            )
            else -> FormatInfo(
                format = Bitmap.CompressFormat.JPEG,
                extension = "jpg",
                mimeType = "image/jpeg",
                quality = 90
            )
        }
    }

    private fun saveBitmap(bitmap: Bitmap, file: File, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 90) {
        FileOutputStream(file).use { out ->
            bitmap.compress(format, quality, out)
        }
    }

    // For ONNX Runtime: load image as float array (224x224 RGB normalized)
    fun loadForInference(path: String, size: Int = 224): FloatArray? {
        return try {
            val bitmap = BitmapFactory.decodeFile(path) ?: return null
            val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)
            val pixels = IntArray(size * size)
            scaled.getPixels(pixels, 0, size, 0, 0, size, size)

            val floatValues = FloatArray(3 * size * size)
            for (i in pixels.indices) {
                val pixel = pixels[i]
                // RGB normalized to [0, 1] then with CLIP mean/std
                floatValues[i] = ((pixel shr 16 and 0xFF) / 255.0f - 0.48145466f) / 0.26862954f
                floatValues[size * size + i] = ((pixel shr 8 and 0xFF) / 255.0f - 0.4578275f) / 0.26130258f
                floatValues[2 * size * size + i] = ((pixel and 0xFF) / 255.0f - 0.40821073f) / 0.27577711f
            }

            bitmap.recycle()
            scaled.recycle()
            floatValues
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

private data class FormatInfo(
    val format: Bitmap.CompressFormat,
    val extension: String,
    val mimeType: String,
    val quality: Int
)

data class SavedImage(
    val id: String,
    val filePath: String,
    val thumbnailPath: String,
    val mediumPath: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val mimeType: String = "image/jpeg"
)
