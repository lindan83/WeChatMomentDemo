package com.lance.wechatmoments.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import com.lance.wechatmoments.demo.R

/**
 * @author lindan
 * 根据宽高比例自动计算高度ImageView
 */
class RatioImageView : ColorFilterImageView {
    /**
     * 宽高比例
     */
    private var ratio = 0F

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)

        ratio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0F)
        typedArray.recycle()
    }

    constructor(context: Context) : super(context)

    /**
     * 设置ImageView的宽高比
     *
     * @param ratio 宽高比值
     */
    fun setRatio(ratio: Float) {
        this.ratio = ratio
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasure = heightMeasureSpec
        val width = MeasureSpec.getSize(widthMeasureSpec)
        if (ratio != 0f) {
            val height = width / ratio
            heightMeasure = MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasure)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val drawable = drawable
                drawable?.mutate()?.setColorFilter(
                    Color.GRAY,
                    PorterDuff.Mode.MULTIPLY
                )
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                val drawableUp = drawable
                drawableUp?.mutate()?.clearColorFilter()
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }
}