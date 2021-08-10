package com.wizl.beautyscanner.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.wizl.beautyscanner.R
import com.wizl.beautyscanner.logick.FileService
import com.wizl.beautyscanner.logick.UserPersisten
import com.wizl.beautyscanner.logick.analytics.AnalyticsService
import com.wizl.beautyscanner.model.BeautyModel
import kotlinx.android.synthetic.main.activity_bs_result.*
import kotlinx.android.synthetic.main.activity_bs_result._result
import kotlinx.android.synthetic.main.view_bs_result.*
import java.io.File
import kotlin.math.roundToInt


class ResultActivity : AppCompatActivity() {
    companion object {
        const val IMAGE_HERO = "IMAGE_HERO"
        const val IMAGE_HERO_1 = "IMAGE_HERO_1"
        const val IMAGE_HERO_2 = "IMAGE_HERO_2"
        const val IMAGE_HERO_3 = "IMAGE_HERO_3"
        const val NAME_HERO_1 = "NAME_HERO_1"
        const val NAME_HERO_2 = "NAME_HERO_2"
        const val NAME_HERO_3 = "NAME_HERO_3"
    }

    private var mResultFile: File? = null
    private var mScore = 0f


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bs_result)

        //TODO:
        AnalyticsService.resultViewed(0F)

        //_result.setBModel(bModel)

        // Отображаем результаты
        /*
        var decImgHero = intent.getByteArrayExtra("IMAGE_HERO")
        if (decImgHero != null) {
            var decBMPHero = BitmapFactory.decodeByteArray(decImgHero, 0, decImgHero.size)
            _imgMainHero.setImageBitmap(decBMPHero)
        }
        else
            */

        // Основная картинка
        _imgMainHero.setImageDrawable(null)
        _imgMainHero.setImageURI(FileService.instance.getBeautyPath())

        var decBMPHero1: Bitmap
        var t: String

        var decImgHero1 = intent.getByteArrayExtra("IMAGE_HERO_1")
        if (decImgHero1 != null) {
            decBMPHero1 = BitmapFactory.decodeByteArray(decImgHero1,0,decImgHero1.size)
            _imgHero1.setImageBitmap(decBMPHero1)
            t = intent.getStringExtra(NAME_HERO_1)
            //decImgHero1 = null
        }
        else t = getString(R.string.no_image)
        _txtNameHero1.setText(t)

        decImgHero1 = intent.getByteArrayExtra("IMAGE_HERO_2")
        if (decImgHero1 != null) {
            var decBMPHero2 = BitmapFactory.decodeByteArray(decImgHero1, 0, decImgHero1.size)
            _imgHero2.setImageBitmap(decBMPHero2)
            t = intent.getStringExtra(NAME_HERO_2)
        }
        else t = getString(R.string.no_image)
        _txtNameHero2.setText(t)

        decImgHero1 = intent.getByteArrayExtra("IMAGE_HERO_3")
        if (decImgHero1 != null) {
            var decBMPHero3 = BitmapFactory.decodeByteArray(decImgHero1, 0, decImgHero1.size)
            _imgHero3.setImageBitmap(decBMPHero3)
            t = intent.getStringExtra(NAME_HERO_3)
        }
        else t = getString(R.string.no_image)
        _txtNameHero3.setText(t)


        _btGallery.setOnClickListener {
            AnalyticsService.resultGalleryTap(mScore)
            startActivity(Intent(this, GalleryActivity::class.java).putExtra("start_gallery", true))
            finish()
        }

        _btSave.setOnClickListener {

            AnalyticsService.resultSaveTap(mScore)

            _btSave.isEnabled = false
            _btSave.alpha = .6f
            _progress.visibility = View.VISIBLE

            val bmResult = _result.drawToBitmap()

            FileService.instance.saveResult(bmResult) {

                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(it.toString()),
                    null
                ) { path, uri ->
                    Log.i("ExternalStorage", "Scanned $path:")
                    Log.i("ExternalStorage", "-> uri=$uri")
                }

                mResultFile = it
                //galleryAddPic(it)
                seved()
            }
        }

        _btSend.setOnClickListener {
            AnalyticsService.resultShareTap(mScore)
            if (mResultFile != null) startShare(mResultFile!!.absolutePath)
        }

        if (!UserPersisten.isPrem) {
            Handler().postDelayed({
                noPrem()
            }, 1000)  //TODO
        }

        _btBgPrem.setOnClickListener {
            val intent = Intent(this, PaywallActivity::class.java)
            intent.putExtra(PaywallActivity.IMAGE_MODE,true)
            startActivity(intent)
//            startActivity(Intent(this, PaywallActivity::class.java))
        }
        _btPrem.setOnClickListener {
            val intent = Intent(this, PaywallActivity::class.java)
            intent.putExtra(PaywallActivity.IMAGE_MODE,true)
            startActivity(intent)
            //startActivity(Intent(this, PaywallActivity::class.java))
        }

    }

    private fun yesPrem() {
        if (_result.visibility != View.VISIBLE) _result.visibility = View.VISIBLE
        if (_bg_prem.visibility != View.GONE) _bg_prem.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun noPrem() {
        val bmResult = blur(this, _result.drawToBitmap())
        _contResult.setBackgroundDrawable(BitmapDrawable(bmResult))
        _result.visibility = View.INVISIBLE
        _bg_prem.visibility = View.VISIBLE

        val intent = Intent(this, PaywallActivity::class.java)
        intent.putExtra(PaywallActivity.IMAGE_MODE,true)
        startActivity(intent)
        //startActivity(Intent(this, PaywallActivity::class.java))
    }

    override fun onRestart() {
        super.onRestart()
        if (UserPersisten.isPrem) {
            yesPrem()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun blur(context: Context, image: Bitmap): Bitmap {

        val BITMAP_SCALE = 0.4f
        val BLUR_RADIUS = 10f

        val width = (image.width * BITMAP_SCALE).roundToInt()
        val height = (image.height * BITMAP_SCALE).roundToInt()
        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLUR_RADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }


    private fun startShare(shareFilePath: String) {

        val intent = Intent(Intent.ACTION_SEND)
        val ext = shareFilePath.substring(shareFilePath.lastIndexOf(".") + 1)
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        intent.type = type

        intent.putExtra(
            Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".my.package.name.provider",
                File(shareFilePath)
            )
        )

        startActivity(intent)

    }

    private fun seved() {

        AnalyticsService.resultSaveOk()

        Toast.makeText(this, getString(R.string.p_saved), Toast.LENGTH_LONG).show()

        _contTxtSaved.visibility = View.VISIBLE
        _txtUseGreetify.visibility = View.VISIBLE
        _btSend.visibility = View.VISIBLE
        _btSave.visibility = View.GONE
        _progress.visibility = View.INVISIBLE
    }
}