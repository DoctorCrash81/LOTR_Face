package com.wizl.lookalike.logick.helpers

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Handler
import com.wizl.lookalike.logick.FileService
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class ImageHelper {

    companion object {

        @Throws(IOException::class)
        fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri): Bitmap? {
            val MAX_HEIGHT = 1024
            val MAX_WIDTH = 1024

            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var imageStream = context.contentResolver.openInputStream(selectedImage)
            BitmapFactory.decodeStream(imageStream, null, options)
            imageStream?.close()

            // Calculate inSampleSize
            options.inSampleSize =
                calculateInSampleSize(
                    options,
                    MAX_WIDTH,
                    MAX_HEIGHT
                )

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            imageStream = context.contentResolver.openInputStream(selectedImage)
            var img = BitmapFactory.decodeStream(imageStream, null, options)

            img =
                rotateImageIfRequired(
                    context,
                    img!!,
                    selectedImage
                )
            return img
        }


        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and width
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

                // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
                // with both dimensions larger than or equal to the requested height and width.
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

                // This offers some additional logic in case the image has a strange
                // aspect ratio. For example, a panorama may have a much larger
                // width than height. In these cases the total pixels might still
                // end up being too large to fit comfortably in memory, so we should
                // be more aggressive with sample down the image (=larger inSampleSize).

                val totalPixels = (width * height).toFloat()

                // Anything more than 2x the requested pixels we'll sample down further
                val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

                while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                    inSampleSize++
                }
            }
            return inSampleSize
        }

        @Throws(IOException::class)
        private fun rotateImageIfRequired(
            context: Context,
            img: Bitmap,
            selectedImage: Uri
        ): Bitmap {

            val input = context.contentResolver.openInputStream(selectedImage)
            val ei: ExifInterface
            ei = if (Build.VERSION.SDK_INT > 23)
                ExifInterface(input)
            else
                ExifInterface(selectedImage.path)

            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                    img,
                    90f
                )
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                    img,
                    180f
                )
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                    img,
                    270f
                )
                else -> img
            }
        }

        private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree)
            val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
            img.recycle()

            return rotatedImg
        }

        fun saveBitmapByPath(file: File, bitmap: Bitmap) {
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
        }


        fun saveImageForServer(
            handler: Handler,
            file: File,
            complete: (File) -> Unit
        ) {
            Thread {
                val outputFile = File(FileService.instance.directoryDownloads,"${Date().time}.jpg")
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                val wOld = bitmap.width
                val hOld = bitmap.height

                var wNew = 0
                var hNew = 0

                val bt = if (wOld > 450) { //уменьшение размера
                    wNew = 450
                    hNew = wNew * hOld / wOld
                    Bitmap.createScaledBitmap(bitmap, wNew, hNew, true)
                } else {
                    bitmap
                }
                val fOut = FileOutputStream(outputFile)
                bt.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
                handler.post { complete(outputFile) }
            }.start()
        }

        fun saveImageForClient(
            handler: Handler,
            bitmap: Bitmap,
            file: File,
            complete: (File) -> Unit
        ) {
            Thread {
                val fOut = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
                handler.post {
                    complete(file)
                }
            }.start()
        }

        fun toGrayscale(srcImage: Bitmap): Bitmap {
            val bmpGrayscale = Bitmap.createBitmap(
                srcImage.width,
                srcImage.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bmpGrayscale)
            val paint = Paint()
            val cm = ColorMatrix()
            cm.setSaturation(0f)
            paint.colorFilter = ColorMatrixColorFilter(cm)
            canvas.drawBitmap(srcImage, 0f, 0f, paint)
            return bmpGrayscale
        }

        fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }


    }
}