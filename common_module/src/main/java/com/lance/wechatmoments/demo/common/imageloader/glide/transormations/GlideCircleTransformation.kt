package com.lance.wechatmoments.demo.common.imageloader.glide.transormations

import android.graphics.*

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

import java.security.MessageDigest
import kotlin.math.min

/**
 * @author lindan
 * 圆形图片的Transformation
 * @param borderColor 边框颜色
 * @param borderWidth 边框大小
 */
class GlideCircleTransformation(borderWidth: Int, borderColor: Int) : BitmapTransformation() {
    private val borderPaint: Paint?
    private val borderWidth: Float = borderWidth.toFloat()

    init {
        borderPaint = Paint()
        borderPaint.isDither = true
        borderPaint.isAntiAlias = true
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = this.borderWidth
    }


    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) {
            return null
        }

        val size = (min(source.width, source.height) - borderWidth / 2).toInt()
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squared = Bitmap.createBitmap(source, x, y, size, size)
        val result = pool.get(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        if (borderPaint != null) {
            val borderRadius = r - borderWidth / 2
            canvas.drawCircle(r, r, borderRadius, borderPaint)
        }
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
}
