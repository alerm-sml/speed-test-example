package com.sml.stp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat

object CanvasUtils {

    fun makeStrokePaint(width: Float, strokeColor: Int): Paint =
            with(Paint()) {
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                strokeWidth = width
                style = Paint.Style.STROKE
                color = strokeColor
                this
            }


    fun makeText(textColor: Int, newTextSize: Float): Paint =
            with(Paint()) {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = textColor
                textSize = newTextSize
                this
            }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        drawable?.let {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = DrawableCompat.wrap(it).mutate()
            }

            bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
        }
        return bitmap
    }
}