package com.lance.wechatmoments.demo.common.imageloader.glide.transormations

import android.graphics.*

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

import java.security.MessageDigest

/**
 * @author lindan
 * 圆角图形的Transformation
 * @param borderColor 边框颜色
 * @param borderWidth 边框大小
 * @param radius 圆角半径
 */
class GlideRoundedCornerTransformation(private val borderWidth: Int, borderColor: Int, private val radius: Int) : BitmapTransformation() {
    private val borderPaint: Paint?

    init {
        borderPaint = Paint()
        borderPaint.isDither = true
        borderPaint.isAntiAlias = true
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = this.borderWidth.toFloat()
    }


    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return roundedCornerCrop(pool, toTransform)
    }

    private fun roundedCornerCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) {
            return null
        }
        val width = source.width - borderWidth
        val height = source.height - borderWidth

        val x = (source.width - width) / 2
        val y = (source.height - height) / 2
        val rectangle = Bitmap.createBitmap(source, x, y, width, height)
        val result = pool.get(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader = BitmapShader(rectangle, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        canvas.drawRoundRect(RectF(x.toFloat(), y.toFloat(), (width - borderWidth).toFloat(), (height - borderWidth).toFloat()), radius.toFloat(), radius.toFloat(), paint)
        if (borderPaint != null) {
            canvas.drawRoundRect(RectF(0f, 0f, (width + borderWidth).toFloat(), (height + borderWidth).toFloat()), radius.toFloat(), radius.toFloat(), borderPaint)
        }
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
}
