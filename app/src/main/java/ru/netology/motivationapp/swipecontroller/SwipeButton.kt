package ru.netology.motivationapp.swipecontroller

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class SwipeButton(
    private val context: Context,
    private val text: String,
    private val imageResId: Int,
    private val color: Int,
    private val clickListener: IOnSwipeControllerActions
) {
    private var pos: Int = 0
    private var clickRegion: RectF? = null
    fun onClick(x: Float, y: Float): Boolean {
        if (clickRegion != null && clickRegion!!.contains(x, y)) {
            clickListener.onClick(pos)
            return true
        }
        return false
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
        val p = Paint()
        p.color = color
        c.drawRect(rectF, p)
        p.color = Color.WHITE
        p.textSize = Resources.getSystem().displayMetrics.density * 12
        val rect = Rect()
        val cHeight = rectF.height()
        val cWidth = rectF.width()
        p.textAlign = Paint.Align.LEFT
        p.getTextBounds(text, 0, text.length, rect)
        val x = cWidth / 2f - rect.width() / 2f - rect.left.toFloat()
        val y = cHeight / 2f + rect.height() / 2f - rect.bottom.toFloat()
        if (imageResId == 0) {
            c.drawText(text, rectF.left + x, rectF.top + y, p)
        } else {
            val d = ContextCompat.getDrawable(context, imageResId)
            val bitmap = drawableToBitmap(d)
            c.drawText(text, rectF.left + x, rectF.top + y + 10f, p)
            c.drawBitmap(
                bitmap,
                (rectF.left + rectF.right) / 2 - bitmap.width / 2,
                (rectF.top + rectF.bottom) / 2 - bitmap.height,
                p
            )
        }
        clickRegion = rectF
        this.pos = pos
    }

    private fun drawableToBitmap(d: Drawable?): Bitmap {
        if (d is BitmapDrawable) return d.bitmap
        val bitmap =
            Bitmap.createBitmap(d!!.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }
}