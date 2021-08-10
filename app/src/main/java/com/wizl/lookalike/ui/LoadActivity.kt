package com.wizl.lookalike.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wizl.lookalike.R
import com.wizl.lookalike.logick.FileService
import com.wizl.lookalike.logick.analytics.AnalyticsService
import com.wizl.lookalike.logick.helpers.PermissionHelper
import com.wizl.lookalike.logick.net.Network
import kotlinx.android.synthetic.main.activity_load.*
import android.media.ExifInterface
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


class LoadActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_STR_URI = "EXTRA_IMAGE_STR_URI"
        const val REQUEST_CODE_PERMISSION_STORAGE = 2
        const val GENDER = "GENDER"
        const val IMAGE_SIZE = 500
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        AnalyticsService.loadViewed()

        checkPermission()

        _btGallery.setOnClickListener {
            AnalyticsService.loadGalleryTap()
            startActivity(Intent(this, GalleryActivity::class.java).putExtra("start_gallery", true))
            finish()
        }
        _btTryAgain.setOnClickListener {
            AnalyticsService.loadTryAgainTap()
            startLoad()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startLoad() {

        _progress.visibility = View.VISIBLE
        _btTryAgain.visibility = View.INVISIBLE
        _btGallery.visibility = View.INVISIBLE
        _text.setText(R.string.data_processing)

        val imageStrUrl = intent.getStringExtra(EXTRA_IMAGE_STR_URI)
        val imageUri = Uri.parse(imageStrUrl)

        val gender = intent.getBooleanExtra(LoadActivity.GENDER,true)
        val imageStream = contentResolver.openInputStream(imageUri)
        val imageStream2 = contentResolver.openInputStream(imageUri)

        val matrix = Matrix()
        try {
            val degrees = getImageOrientation2(imageStream2).toFloat()
            matrix.postRotate(degrees)
        }
        catch (ex: Exception)
        {
            val s = ex.localizedMessage
        }

        var origImage: Bitmap = BitmapFactory.decodeStream(imageStream)
        origImage = Bitmap.createBitmap(
            origImage,
            0,
            0,
            origImage.width,
            origImage.height,
            matrix,
            true
        )

        val selectedImage = scaleAndCropImage(origImage)

        if (selectedImage != null) {
            FileService.instance.savePhotoBeauty(selectedImage) { fileImg ->

                AnalyticsService.loadSavedPrevie()

                Network.instance.getResult(fileImg, gender, {

                    _progress.visibility = View.INVISIBLE
                    _text.text = ""
                    AnalyticsService.loadResultOk(0F)

                    // передаем картинки и Запускаем резалт активити
                    val intent = Intent(this, ResultActivity::class.java)

                    for (i in 0..2) {
                        var s = it.getJPG(i)
                        val decodedImage = Base64.decode(s, Base64.DEFAULT)
                        if (decodedImage != null) {
                            intent.putExtra("IMAGE_HERO_${i + 1}", decodedImage)
                            intent.putExtra("NAME_HERO_${i + 1}", it.getHeroName(i))
                        }
                        else throw Exception("No image from server")
                    }
                    AnalyticsService.galleryPhotoPicked()

                    startActivity(intent)
                    finish()
                }, {
                    AnalyticsService.loadError(it.message)
                    _progress.visibility = View.INVISIBLE
                    _btTryAgain.visibility = View.VISIBLE
                    _text.text = it.message
                }, { code, message ->
                    AnalyticsService.serverError("$code: $message")
                    _progress.visibility = View.INVISIBLE
                    _btGallery.visibility = View.VISIBLE
                    when (code) {
                        422 -> {
                            _text.setText(R.string.could_not_detect_face)
                        }
                        else -> {
                            _text.setText(R.string.image_error)
                        }
                    }
                })
            }
        }
    }

    // Преобразуем исходную картинку к заданному размеру
    private  fun scaleAndCropImage(sourceImg: Bitmap): Bitmap?
    {
        var transImage: Bitmap
        var resultImage: Bitmap? = null

        // 1 случай - рисунок больше фрейма по обеим сторонам.  обрезаем до квадрата и масштабируем
        if (sourceImg.height >= IMAGE_SIZE && sourceImg.width >= IMAGE_SIZE) {
            transImage = cropImageToSquare(sourceImg)
            resultImage = Bitmap.createScaledBitmap(transImage, IMAGE_SIZE, IMAGE_SIZE, true)
        }
        else
        // 2а - рисунок узкий. Масштабируем, обрезаем
        if (sourceImg.height > sourceImg.width){
            val newHeight = sourceImg.height * IMAGE_SIZE/sourceImg.width
            transImage = Bitmap.createScaledBitmap(sourceImg, IMAGE_SIZE,newHeight,true)
            resultImage = cropImageToSquare(transImage)
        }
        else
        // 2б - рисунок широкий или квадратный, но не в фрейме. Масштабируем, обрезаем
        {
            val newWidth = sourceImg.width * IMAGE_SIZE/sourceImg.height
            transImage = Bitmap.createScaledBitmap(sourceImg, newWidth,IMAGE_SIZE,true)
            resultImage = cropImageToSquare(transImage)

        }
        return resultImage
    }

    private fun cropImageToSquare(sourceImg: Bitmap): Bitmap {
        var dW: Int = 0
        var dH: Int = 0
        var dS: Int = 0

        if (sourceImg.height > sourceImg.width) {
                dS = sourceImg.width
                dH = ((dS * sourceImg.height) / sourceImg.width - dS) / 2
            } else {
                dS = sourceImg.height
                dW = ((dS * sourceImg.width) / sourceImg.height - dS) / 2
            }
        return Bitmap.createBitmap(sourceImg, dW, dH, dS, dS)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getImageOrientation2(_img: InputStream?): Int {
        var degree = 0
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(_img)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        if (exif != null) {
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
            if (orientation != -1) {
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                }
            }
        }
        return degree
    }

    private fun checkPermission() {
        if (PermissionHelper.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            && PermissionHelper.isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        )
            startLoad()
        else
            PermissionHelper.requestPermission(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION_STORAGE
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val h = Handler()
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                REQUEST_CODE_PERMISSION_STORAGE -> {
                    h.post {
                        startLoad()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}