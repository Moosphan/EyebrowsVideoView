package com.eyebrows.video.sample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Hashtable

object VideoUtils {

    fun createVideoThumbnail(filePath: String, kind: Int): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
                retriever.setDataSource(filePath, Hashtable())
            } else {
                retriever.setDataSource(filePath)
            }
            bitmap =
                retriever.getFrameAtTime(200, MediaMetadataRetriever.OPTION_CLOSEST_SYNC) //retriever.getFrameAtTime(-1);
        } catch (ex: IllegalArgumentException) {
            // Assume this is a corrupt video file
            ex.printStackTrace()
        } catch (ex: RuntimeException) {
            // Assume this is a corrupt video file.
            ex.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (ex: RuntimeException) {
                // Ignore failures while cleaning up.
                ex.printStackTrace()
            }

        }

        if (bitmap == null) {
            return null
        }

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {//压缩图片 开始处
            // Scale down the bitmap if it's too large.
            val width = bitmap.width
            val height = bitmap.height
            val max = Math.max(width, height)
            if (max > 512) {
                val scale = 512f / max
                val w = Math.round(scale * width)
                val h = Math.round(scale * height)
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                96,
                96,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
            )
        }
        return bitmap
    }

    /**
     * compress image to thumbnail
     */
    fun compressImage(image: Bitmap): Bitmap? {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var options = 90

        while (baos.toByteArray().size / 1024 > 100) {
            baos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)
            options -= 10// 每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())
        return BitmapFactory.decodeStream(isBm, null, null)
    }
}
