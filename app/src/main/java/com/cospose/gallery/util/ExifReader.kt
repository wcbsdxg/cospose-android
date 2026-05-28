package com.cospose.gallery.util

import android.content.Context
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException

object ExifReader {

    data class ExifData(
        val cameraMake: String? = null,
        val cameraModel: String? = null,
        val aperture: String? = null,
        val iso: String? = null,
        val shutterSpeed: String? = null,
        val focalLength: String? = null,
        val flash: String? = null,
        val whiteBalance: String? = null,
        val gpsLatitude: String? = null,
        val gpsLongitude: String? = null,
        val orientation: Int = 0,
        val dateTime: String? = null,
        val imageWidth: Int = 0,
        val imageHeight: Int = 0
    )

    /**
     * Read EXIF data from an image file.
     */
    fun readExif(filePath: String): ExifData? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null

            val exif = ExifInterface(file)

            ExifData(
                cameraMake = exif.getAttribute(ExifInterface.TAG_MAKE),
                cameraModel = exif.getAttribute(ExifInterface.TAG_MODEL),
                aperture = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE),
                iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED),
                shutterSpeed = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME),
                focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH),
                flash = exif.getAttribute(ExifInterface.TAG_FLASH),
                whiteBalance = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE),
                gpsLatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                gpsLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),
                orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                ),
                dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME),
                imageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0),
                imageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convert EXIF data to a map for display.
     */
    fun toDisplayMap(exifData: ExifData): Map<String, String> {
        val map = mutableMapOf<String, String>()

        exifData.cameraMake?.let { map["相机品牌"] = it }
        exifData.cameraModel?.let { map["相机型号"] = it }
        exifData.aperture?.let { map["光圈"] = "f/$it" }
        exifData.iso?.let { map["ISO"] = it }
        exifData.shutterSpeed?.let { map["快门速度"] = "${it}s" }
        exifData.focalLength?.let { map["焦距"] = "${it}mm" }
        exifData.flash?.let { map["闪光灯"] = it }
        exifData.whiteBalance?.let { map["白平衡"] = it }
        exifData.dateTime?.let { map["拍摄时间"] = it }

        if (exifData.gpsLatitude != null && exifData.gpsLongitude != null) {
            map["GPS坐标"] = "${exifData.gpsLatitude}, ${exifData.gpsLongitude}"
        }

        if (exifData.imageWidth > 0 && exifData.imageHeight > 0) {
            map["EXIF尺寸"] = "${exifData.imageWidth} × ${exifData.imageHeight}"
        }

        return map
    }

    /**
     * Format file size for display.
     */
    fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024} KB"
            else -> String.format("%.2f MB", sizeInBytes / (1024.0 * 1024.0))
        }
    }
}
