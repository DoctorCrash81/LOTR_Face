package com.wizl.lookalike.ui

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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.wizl.lookalike.App
import com.wizl.lookalike.R
import com.wizl.lookalike.logick.FileService
import com.wizl.lookalike.logick.UserPersisten
import com.wizl.lookalike.logick.analytics.AnalyticsService
import kotlinx.android.synthetic.main.activity_bs_result.*
import kotlinx.android.synthetic.main.activity_bs_result._result
import kotlinx.android.synthetic.main.view_bs_result.*
import java.io.File
import kotlin.math.roundToInt


class ResultActivity : AppCompatActivity() {

    private val myDict = java.util.Hashtable<String,String>()

    private var mResultFile: File? = null
    private var mScore = 0f


    private fun setImageAndText(_imageView: ImageView, _textView: TextView, _index: Int){
        val decImgHero = intent.getByteArrayExtra("IMAGE_HERO_$_index")
        val decBMPHero = BitmapFactory.decodeByteArray(decImgHero,0,decImgHero.size)
            _imageView.setImageBitmap(decBMPHero)
        var t = intent.getStringExtra("NAME_HERO_$_index")

        // Переводим на русский если англ
        if (App.instance.language == "ru"){
            t = myDict[t]
        }
        _textView.text = t
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bs_result)

        // Инициализируем словарь
        myDict["Elf"] = "Эльф"
        myDict["Hobbit"] = "Хоббит"
        myDict["Wizard"] = "Маг"
        myDict["Dwarf"] = "Дварф"

        AnalyticsService.resultViewed(0F)

        // Отображаем результаты
        _imgMainHero.setImageDrawable(null)
        _imgMainHero.setImageURI(FileService.instance.getBeautyPath())

        setImageAndText(_imgHero1,_txtNameHero1,1)
        setImageAndText(_imgHero2,_txtNameHero2,2)
        setImageAndText(_imgHero3,_txtNameHero3,3)

        _btGallery.setOnClickListener {
            AnalyticsService.resultGalleryTap(mScore)
            startActivity(Intent(this, GalleryActivity::class.java).putExtra("start_gallery", true))
            finish()
        }

        _btSave.setOnClickListener {
            // Срабатывает если оплачено
            if (UserPersisten.isPrem) {
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
                    imageSaved()
                }
            }
        }

        _btSend.setOnClickListener {
            AnalyticsService.resultShareTap(mScore)
            if (mResultFile != null) startShare(mResultFile!!.absolutePath)
        }

        if (!UserPersisten.isPrem) {
            Handler().postDelayed({
                noPrem()
            }, 500)  //TODO интервал показа фото
        }

        _btBgPrem.setOnClickListener {
            val intent = Intent(this, PaywallActivity::class.java)
            intent.putExtra(PaywallActivity.IMAGE_MODE,true)
            startActivity(intent)
        }
        _btPrem.setOnClickListener {
            val intent = Intent(this, PaywallActivity::class.java)
            intent.putExtra(PaywallActivity.IMAGE_MODE,true)
            startActivity(intent)
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

    private fun imageSaved() {

        AnalyticsService.resultSaveOk()

        Toast.makeText(this, getString(R.string.p_saved), Toast.LENGTH_LONG).show()

        _contTxtSaved.visibility = View.VISIBLE
        _btSend.visibility = View.VISIBLE
        _btSave.visibility = View.GONE
        _progress.visibility = View.INVISIBLE
    }
}