package com.example.composesaratest.uiMap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.composesaratest.R

@SuppressLint("ViewConstructor")
class PlaceMarkView @JvmOverloads constructor(context : Context, attrs : AttributeSet? = null, style: Int = 0, private val text : String?) : View(context,attrs,style) {

    private companion object {
        const val PADDING_DP = 10
        const val WIDTH_MARK_DP = 100
        const val HEIGHT_VIEW_DP = 400
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val bounds = Rect()
        var widthText = 0

        if(text != null) {
            paintText.getTextBounds(text, 0, text.length, bounds)
            widthText = bounds.width()
        }
        setMeasuredDimension(2 * (WIDTH_MARK_DP/2 + widthText + PADDING_DP*3), HEIGHT_VIEW_DP)
    }

    private val bitmapSourcePlaceMark:    Bitmap = BitmapFactory.decodeResource(resources, R.drawable.pin_icon)
    private val bitmapPlaceMark:          Bitmap = Bitmap.createBitmap(bitmapSourcePlaceMark, 0, 0,  bitmapSourcePlaceMark.width ,bitmapSourcePlaceMark.height)

    private val paint = Paint().apply { color = Color.rgb(10, 180, 255)
        isAntiAlias = true
    }

    private val paintWithe = Paint().apply { color = Color.rgb(255, 255, 255)
        isAntiAlias = true
        setShadowLayer(10.0f, -2f, 2f, Color.GRAY)
    }

    private val paintText = Paint().apply { color = Color.rgb(80, 80, 80)
        isAntiAlias = true
        textSize = 25f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawPlaceMark(canvas)

        if(text != null) {
            drawCardText(canvas)
        }
    }

    private fun drawPlaceMark(canvas: Canvas) {
        val ratio = (bitmapSourcePlaceMark.height.toFloat() / bitmapSourcePlaceMark.width.toFloat())
        val w = WIDTH_MARK_DP
        val h = (ratio * w).toInt()

        canvas.drawBitmap(bitmapPlaceMark, null, Rect((width/2 - w/2),height/2 - h, (width/2 + w/2),height/2), paint)
    }

    private fun drawCardText(canvas: Canvas) {
        val w = WIDTH_MARK_DP

        val heightRect = height/8f + PADDING_DP
        val widthRect = (width/2f + w/2f)

        canvas.drawRoundRect(RectF(widthRect, heightRect, width.toFloat() - 10f, 0f + PADDING_DP ), 26f,26f,paintWithe)

        val bounds = Rect()
        text?.let { paintText.getTextBounds(text, 0, it.length, bounds) }

        val heightText: Int = bounds.height()

        if (text != null) {
            canvas.drawText(text,widthRect + PADDING_DP,heightRect/2f + heightText/2f,paintText)
        }
    }
}