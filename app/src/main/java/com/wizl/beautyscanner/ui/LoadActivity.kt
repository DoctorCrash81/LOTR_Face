package com.wizl.beautyscanner.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wizl.beautyscanner.App
import com.wizl.beautyscanner.R
import com.wizl.beautyscanner.logick.FileService
import com.wizl.beautyscanner.logick.analytics.AnalyticsService
import com.wizl.beautyscanner.logick.helpers.ImageHelper
import com.wizl.beautyscanner.logick.helpers.PermissionHelper
import com.wizl.beautyscanner.logick.net.Network
import com.wizl.beautyscanner.model.BeautyModel
import com.wizl.beautyscanner.model.BeautyModelSerializable
import com.wizl.beautyscanner.model.BeautyParamsModel
import com.wizl.beautyscanner.model.StandartModel
import kotlinx.android.synthetic.main.activity_load.*
import java.io.File
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sqrt


class LoadActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_STR_URI = "EXTRA_IMAGE_STR_URI"
        const val REQUEST_CODE_PERMISSION_STORAGE = 2
    }

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

    fun startLoad() {

        _progress.visibility = View.VISIBLE
        _btTryAgain.visibility = View.INVISIBLE
        _btGallery.visibility = View.INVISIBLE
        _text.setText(R.string.data_processing)

        val imageStrUrl = intent.getStringExtra(EXTRA_IMAGE_STR_URI)
        val imageUri = Uri.parse(imageStrUrl)

        val matrix = Matrix()
        matrix.postRotate(getImageOrientation(imageUri).toFloat())

        val imageStream = contentResolver.openInputStream(imageUri)
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

        var newWidth = 500
        var newHeight = 500

        if (origImage.height > newHeight || origImage.width > newWidth) {
            if (origImage.height > origImage.width) {
                newWidth = origImage.width * newHeight / origImage.height
            } else {
                newHeight = origImage.height * newWidth / origImage.width
            }
        }
        var selectedImage = Bitmap.createScaledBitmap(origImage, newWidth, newHeight, true)

        FileService.instance.savePhotoBeauty(selectedImage) { fileImg ->

            AnalyticsService.loadSavedPrevie()
            _img.setImageBitmap(selectedImage)

            Network.instance.getResult(fileImg, {

                _progress.visibility = View.INVISIBLE
                _text.text = ""

                val bModel = BeautyModel()
                bModel.pathImg = fileImg.absolutePath

//                val bm = ImageHelper.getRoundedCornerBitmap(pars(it, bModel, origImage), DisplayHelper.dpToPx(8))
                val bm = pars(it, bModel, origImage)
                _img.setImageBitmap(bm)


//                _draw.addUpLone(
//                    it.meta.faceLandmarks[45][0].toFloat() * origImage.width / bm.width,
//                    it.meta.faceLandmarks[45][1].toFloat() * origImage.height / bm.height,
//                    300f,
//                    300f
//                )


//                _draw.set1(
//                    bModel.params[1].x.toFloat(),
//                    bModel.params[1].y.toFloat(),
//                    300f,
//                    300f
//                )
//                _draw.addUpLone(bModel.params[1].x.toFloat(), bModel.params[1].y.toFloat(), 0f, 0f)

                AnalyticsService.loadResultOk(bModel.score)

                FileService.instance.savePhotoBeauty(bm) { f: File ->
                    bModel.pathImg = f.absolutePath
                    AnalyticsService.loadsavedResult()
                    startActivity(
                        Intent(this, ResultActivity::class.java).putExtra(
                            "bModel",
                            bModel
                        )
                    )
                    finish()
                }
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
                        _text.text = "$code: $message"
                    }
                }
            })
        }

//        _img.setImageBitmap(selectedImage)
    }

    private fun getImageOrientation(imageUri: Uri): Int {
        val orientationColumn =
            arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur: Cursor? =
            contentResolver.query(imageUri, orientationColumn, null, null, null)
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            Log.d("orientation", "Picture orientation: $orientation")
        } else {
            Log.d("orientation", "Wrong picture orientation: $orientation")
        }
        cur?.close()
        return orientation
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

    private fun pars(bms: BeautyModelSerializable, bsModel: BeautyModel, orig: Bitmap): Bitmap {

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bm = BitmapFactory.decodeFile(bsModel.pathImg, options)

        val px = orig.width.toFloat() / bm.width.toFloat()
        val py = orig.height.toFloat() / bm.height.toFloat()

        bsModel.score = ((bms.result.score + 5.5) * 1000).toInt().toFloat() / 100

        BeautyParamsModel(
            App.instance.getString(R.string.proportions),
            featureBias(
                avg(
                    dist(bms.getPoint(36), bms.getPoint(57)),
                    dist(bms.getPoint(45), bms.getPoint(57))
                )
                        / dist(bms.getPoint(36), bms.getPoint(45)),
                StandartModel(0.986782f, 1.117462f, 1.050183f)
            ).toFloat(),
            -1f,
            -1f
        )

        bsModel.params.add(
            BeautyParamsModel(
                App.instance.getString(R.string.eyebrows),
                featureBias(
                    angle(
                        convert(bms.getPoint(22), bms.getPoint(35)),
                        convert(bms.getPoint(42), bms.getPoint(35))
                    ),
                    StandartModel(0.314547f, 2.698172f, 1.452419f)
                ).toFloat(),
                bms.meta.faceLandmarks[24][0].toFloat() * px,
                bms.meta.faceLandmarks[24][1].toFloat() * py
            )
        )

        bsModel.params.add(
            BeautyParamsModel(
                App.instance.getString(R.string.eyes),
                featureBias(
                    avg(
                        dist(bms.getPoint(36), bms.getPoint(39)),
                        dist(bms.getPoint(42), bms.getPoint(45))
                    ) / dist(bms.getPoint(39), bms.getPoint(42)),
                    StandartModel(0.584677f, 0.719267f, 0.650019f)
                ).toFloat(),
                bms.meta.faceLandmarks[45][0].toFloat() * px,
                bms.meta.faceLandmarks[45][1].toFloat() * py
            )
        )


        BeautyParamsModel(
            App.instance.getString(R.string.face_shape),
            featureBias(
                dist(
                    mid(bms.getPoint(36), bms.getPoint(39)),
                    mid(bms.getPoint(42), bms.getPoint(46))
                )
                        / dist(bms.getPoint(0), bms.getPoint(6)),
                StandartModel(0.508259f, 0.604011f, 0.555307f)
            ).toFloat(),
            bms.meta.faceLandmarks[15][0].toFloat() * px,
            bms.meta.faceLandmarks[15][1].toFloat() * py
        )

        bsModel.params.add(
            BeautyParamsModel(
                App.instance.getString(R.string.nose),
                featureBias(
                    dist(bms.getPoint(31), bms.getPoint(35))
                            / dist(bms.getPoint(39), bms.getPoint(42)),
                    StandartModel(0.638075f, 0.817340f, 0.717391f)
                ).toFloat(),
                bms.meta.faceLandmarks[35][0].toFloat() * px,
                bms.meta.faceLandmarks[35][1].toFloat() * py
            )
        )

        bsModel.params.add(
            BeautyParamsModel(
                App.instance.getString(R.string.lips),
                featureBias(
                    dist(bms.getPoint(8), bms.getPoint(57))
                            / dist(bms.getPoint(51), bms.getPoint(33)),
                    StandartModel(1.792151f, 3.289212f, 2.314976f)
                ).toFloat(),
                bms.meta.faceLandmarks[54][0].toFloat() * px,
                bms.meta.faceLandmarks[54][1].toFloat() * py
            )
        )

        BeautyParamsModel(
            App.instance.getString(R.string.cheekbones),
            featureBias(
                ((dist(bms.getPoint(3), bms.getPoint(48))
                        + dist(bms.getPoint(13), bms.getPoint(54)))
                        / 2)
                        / ((dist(bms.getPoint(1), bms.getPoint(30))
                        + dist(bms.getPoint(15), bms.getPoint(30)))
                        / 2),
                StandartModel(0.455117f, 0.607101f, 0.551697f)
            ).toFloat(),
            bms.meta.faceLandmarks[11][0].toFloat() * px,
            bms.meta.faceLandmarks[11][1].toFloat() * py
        )

        BeautyParamsModel(
            App.instance.getString(R.string.chin),
            featureBias(
                dist(bms.getPoint(57), bms.getPoint(8))
                        / avg(
                    dist(bms.getPoint(42), bms.getPoint(45)),
                    dist(bms.getPoint(36), bms.getPoint(39))
                ),
                StandartModel(0.993067f, 1.578939f, 1.254223f)

            ).toFloat(),
            bms.meta.faceLandmarks[8][0].toFloat() * px,
            bms.meta.faceLandmarks[8][1].toFloat() * py
        )

        // определяем отступы от лица
        val kx = 0.15f // 30% по бокам
        val ky = 0.25f // 50% сверху с низу
        var x =
            (bms.meta.faceRect.x.toFloat() * px - bms.meta.faceRect.w.toFloat() * px * kx).toInt()
        var y =
            (bms.meta.faceRect.y.toFloat() * py - bms.meta.faceRect.h.toFloat() * py * ky * 1.3f).toInt() // на 2/3 выше
        var w =
            (bms.meta.faceRect.w.toFloat() * px + bms.meta.faceRect.w.toFloat() * px * kx * 2f).toInt()
        var h =
            (bms.meta.faceRect.h.toFloat() * py + bms.meta.faceRect.h.toFloat() * py * ky * 2f).toInt()

        // опредиляем соотношение сторон (подгоняем к макету w/h = 3/4)
        if (h * 3 / 4 > w) {
            val wn = h * 3 / 4 - w
            w += wn
            x -= wn / 2
        } else {
            val hn = w * 4 / 3 - h
            h += hn
            y -= hn / 2
        }

        //определяем не вышли ли за границы изображения
        if (x < 0) {
            w += x
            x = 0
        }
        if (y < 0) {
            h += y
            y = 0
        }
        if (orig.width < x + w) {
            w += orig.width - (x + w)
        }

        if (orig.height < y + h) {
            h += orig.height - (y + h)
        }

        for (it in bsModel.params) {
            it.x -= x
            it.y -= y
        }

        Log.d("IMG", "x = $x")
        Log.d("IMG", "y = $y")
        Log.d("IMG", "w = $w")
        Log.d("IMG", "h = $h")

        val resultBm = Bitmap.createBitmap(orig, x, y, w, h, null, true)

        bsModel.width = resultBm.width
        bsModel.height = resultBm.height

        return ImageHelper.toGrayscale(resultBm)

    }

    private fun dist(p1: Point, p2: Point): Double {
        return sqrt(((p1.x - p2.x) * (p1.x - p2.x)).toDouble() + ((p1.y - p2.y) * (p1.y - p2.y)).toDouble())
    }

    private fun mid(p1: Point, p2: Point): Point {
        return Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
    }

    private fun avg(v1: Double, v2: Double): Double {
        return (v1 + v2) / 2
    }

    private fun angle(p1: Point, p2: Point): Double {
        val v1 = (p1.x * p2.x + p1.y * p2.y).toDouble()
        val v2 = sqrt(((p1.x * p1.x) + (p2.x * p2.x)).toDouble())
        val v3 = sqrt(((p1.y * p1.y) + (p2.y * p2.y)).toDouble())
        return acos(cos(v1 / (v2 * v3)))
    }

    private fun convert(p: Point, basis: Point): Point {
        return Point(p.x - basis.x, p.y - basis.y)
    }

    private fun featureBias(value: Double, standart: StandartModel): Double {
        val delta = value - standart.mean
        val v =
            if (delta < 0) delta / (standart.mean - standart.min)
            else delta / (standart.max - standart.mean)
        return (1.0 - abs(v)).coerceAtLeast(0.0)
    }

}