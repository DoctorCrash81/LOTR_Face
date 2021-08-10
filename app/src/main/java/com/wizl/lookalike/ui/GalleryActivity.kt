package com.wizl.lookalike.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wizl.lookalike.R
import com.wizl.lookalike.logick.UserPersisten
import com.wizl.lookalike.logick.analytics.AnalyticsService
import com.wizl.lookalike.logick.helpers.PermissionHelper
import kotlinx.android.synthetic.main.activity_bs_gallery.*


class GalleryActivity : AppCompatActivity() {

    companion object {
        private const val RESULT_PAYWALL = 13
        private const val PICK_IMG = 1
        private const val RESULT_WHOIS = 17
    }

    private var imgData: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bs_gallery)

        AnalyticsService.galleryViewed()

        if (intent.getBooleanExtra("start_gallery", false)) {
            startGallery()
        }

        _btGallery.setOnClickListener {
            AnalyticsService.galleryTap()
            checkPermission()
        }

        if (!UserPersisten.isPrem) {
            val intent = Intent(this, PaywallActivity::class.java)
            intent.putExtra(PaywallActivity.IMAGE_MODE,false)
            startActivityForResult(intent, RESULT_PAYWALL)
        }
    }

    private fun checkPermission() {
        if (PermissionHelper.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            && PermissionHelper.isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        )
            startGallery()
        else
            PermissionHelper.requestPermission(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                LoadActivity.REQUEST_CODE_PERMISSION_STORAGE
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                LoadActivity.REQUEST_CODE_PERMISSION_STORAGE -> {
                    startGallery()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RESULT_WHOIS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                       AnalyticsService.galleryResultOk()
                        if (imgData != null && imgData!!.data != null) {
                            val imageUri: Uri = imgData!!.data!!
                            val gender: Boolean = data?.getBooleanExtra(LoadActivity.GENDER, true) ?: true

                            // Загрузка изображения на сервак
                            val intent = Intent(this, LoadActivity::class.java)
                            intent.putExtra(LoadActivity.EXTRA_IMAGE_STR_URI, imageUri.toString())
                            intent.putExtra(LoadActivity.GENDER, gender)
                            AnalyticsService.galleryPhotoPicked()
                            startActivity(intent)
                            finish()
                         }
                    }
                    Activity.RESULT_CANCELED-> {
                        AnalyticsService.galleryResultCanceled()
                    }
                    else -> {
                        AnalyticsService.galleryResultError()
                    }
                }
            }
            RESULT_PAYWALL -> {
                if (resultCode == Activity.RESULT_OK) {
                    //купили!!!
                }
            }
            PICK_IMG -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        // Сохраняем данные картинки
                        imgData = Intent(data)
                        AnalyticsService.galleryStartGenderSelect()

                        // Здесь выбор пола
                        val intent = Intent(this, WhoIsActivity::class.java)
                        startActivityForResult(intent, RESULT_WHOIS)
                    }
                    Activity.RESULT_CANCELED-> {
                        AnalyticsService.galleryResultCanceled()
                    }
                    else -> {
                        AnalyticsService.galleryResultError()
                    }
                }
            }
        }
    }

    private fun startGallery() {

        AnalyticsService.galleryStartIntent()

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_photo)),
            PICK_IMG
        )
    }
}