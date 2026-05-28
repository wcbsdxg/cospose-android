package com.cospose.gallery.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.sqrt

object ColorExtractor {

    /**
     * Extract the dominant color from a bitmap using a simple color quantization algorithm.
     * Returns an ARGB integer.
     */
    fun extractDominant(bitmap: Bitmap): Int {
        // Downscale for performance
        val scaled = Bitmap.createScaledBitmap(bitmap, 50, 50, true)

        val pixels = IntArray(scaled.width * scaled.height)
        scaled.getPixels(pixels, 0, scaled.width, 0, 0, scaled.width, scaled.height)

        // Simple color quantization - find most common color bucket
        val colorBuckets = mutableMapOf<Int, Int>()

        for (pixel in pixels) {
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            // Skip very dark and very light pixels (likely background)
            val brightness = (r + g + b) / 3
            if (brightness < 20 || brightness > 235) continue

            // Quantize to reduce color space
            val qr = (r / 32) * 32
            val qg = (g / 32) * 32
            val qb = (b / 32) * 32

            val quantized = Color.rgb(qr, qg, qb)
            colorBuckets[quantized] = (colorBuckets[quantized] ?: 0) + 1
        }

        // Find the most common color
        val dominant = colorBuckets.maxByOrNull { it.value }?.key
            ?: return Color.rgb(128, 128, 128)

        // Refine by averaging all pixels in the dominant bucket
        var totalR = 0L
        var totalG = 0L
        var totalB = 0L
        var count = 0

        val dr = Color.red(dominant)
        val dg = Color.green(dominant)
        val db = Color.blue(dominant)

        for (pixel in pixels) {
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            val qr = (r / 32) * 32
            val qg = (g / 32) * 32
            val qb = (b / 32) * 32

            if (qr == dr && qg == dg && qb == db) {
                totalR += r
                totalG += g
                totalB += b
                count++
            }
        }

        return if (count > 0) {
            Color.rgb(
                (totalR / count).toInt(),
                (totalG / count).toInt(),
                (totalB / count).toInt()
            )
        } else {
            dominant
        }
    }

    /**
     * Calculate color distance in HSL space for more perceptually accurate matching.
     */
    fun colorDistance(color1: Int, color2: Int): Float {
        val hsl1 = FloatArray(3)
        val hsl2 = FloatArray(3)

        Color.colorToHSV(color1, hsl1)
        Color.colorToHSV(color2, hsl2)

        val dh = Math.abs(hsl1[0] - hsl2[0]).let { if (it > 180) 360 - it else it } / 180f
        val ds = Math.abs(hsl1[1] - hsl2[1])
        val dl = Math.abs(hsl1[2] - hsl2[2])

        return sqrt(dh * dh + ds * ds + dl * dl)
    }

    /**
     * Check if two colors are similar within a tolerance.
     */
    fun isSimilarColor(color1: Int, color2: Int, tolerance: Float = 0.3f): Boolean {
        return colorDistance(color1, color2) <= tolerance
    }
}
