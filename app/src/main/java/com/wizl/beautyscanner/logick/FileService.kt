package com.wizl.beautyscanner.logick

import android.graphics.Bitmap
import android.graphics.Path
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.StrictMode
import androidx.core.net.toUri
import com.wizl.beautyscanner.logick.helpers.ImageHelper
import java.io.File
import java.net.URI
import java.util.*

class FileService private constructor() {

    private object Holder {
        var INSTANCE = FileService()
    }

    companion object {
        val instance: FileService by lazy { Holder.INSTANCE }
    }

    private val pathPhotoBeauty: File
        get() {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            return File(directoryDownloads.path + "/photo_beauty.jpg")
        }

    private val newPathResult: File
        get() {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            return File(directoryPictures.path + "/beauty_${Date().time}.jpg")
        }

    val directoryDownloads: File
        get() {
            val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!file.exists())
                file.mkdirs()
            return file
        }

    private val directoryPictures: File
        get() {
            val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!file.exists())
                file.mkdirs()
            return file
        }

    fun savePhotoBeauty(bm: Bitmap, complete: (File) -> Unit) {
        ImageHelper.saveImageForClient(
            Handler(),
            bm,
            pathPhotoBeauty
        ) {
            complete(it)
        }
    }

    fun saveResult(bm: Bitmap, complete: (File) -> Unit) {
        ImageHelper.saveImageForClient(
            Handler(),
            bm,
            newPathResult
        ) {
            complete(it)
        }
    }

    fun getBeautyPath(): Uri {
        return Uri.fromFile(pathPhotoBeauty)
        //return p.toUri()
    }
}