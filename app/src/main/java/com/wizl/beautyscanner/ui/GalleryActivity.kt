package com.wizl.beautyscanner.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.wizl.beautyscanner.R
import com.wizl.beautyscanner.logick.UserPersisten
import com.wizl.beautyscanner.logick.analytics.AnalyticsService
import com.wizl.beautyscanner.logick.helpers.PermissionHelper
import kotlinx.android.synthetic.main.activity_bs_gallery.*


class GalleryActivity : AppCompatActivity() {

    companion object {
        private const val RESULT_PAYWALL = 13
        private const val PICK_IMG = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bs_gallery)

        AnalyticsService.galleryViewed()

        //checkSubs()

        if (intent.getBooleanExtra("start_gallery", false)) {
            startGallery()
        }

        _btGallery.setOnClickListener {
            AnalyticsService.galleryTap()
            checkPermission()
        }

        if (!UserPersisten.isPrem) {
            startActivityForResult(Intent(this, PaywallActivity::class.java), RESULT_PAYWALL)
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
            PICK_IMG -> {
                if (resultCode == Activity.RESULT_OK) {

                    AnalyticsService.galleryResultOk()

                    if (data != null && data.data != null) {

                        val imageUri: Uri = data.data!!

                        val intent = Intent(this, LoadActivity::class.java)
                        intent.putExtra(LoadActivity.EXTRA_IMAGE_STR_URI, imageUri.toString())
                        AnalyticsService.galleryPhotoPicked()
                        startActivity(intent)
                        finish()

//                        val imageStream = contentResolver.openInputStream(imageUri)
//                        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
//                        _img.setImageBitmap(selectedImage)
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    AnalyticsService.galleryResultCanceled()
                } else {
                    AnalyticsService.galleryResultError()
                }
            }
            RESULT_PAYWALL -> {
                if (resultCode == Activity.RESULT_OK) {
                    //купили!!!
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

//    private fun querySkuDetails() {
//        val skuDetailsParamsBuilder = SkuDetailsParams.newBuilder()
//        val skuList: MutableList<String> = ArrayList()
//        skuList.add(mSkuId)
//        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
//        mBillingClient.querySkuDetailsAsync(
//            skuDetailsParamsBuilder.build()
//        ) { result, skuDetailsList ->
//            when (result.responseCode) {
//                BillingResponseCode.OK -> {
//                    if (skuDetailsList != null) {
//                        for (skuDetails in skuDetailsList) {
//                            mSkuDetailsMap[skuDetails.sku] = skuDetails
//                        }
//                    }
//                }
//            }
//        }
//    }

}