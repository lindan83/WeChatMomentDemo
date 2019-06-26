package com.lance.wechatmoments.demo.widget

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff.Mode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatImageView

/**
 * @author lindan
 * 实现图像根据按下抬起动作变化颜色
 */
open class ColorFilterImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    AppCompatImageView(context, attrs, defStyle), OnTouchListener {

    init {
        @Suppress("LeakingThis")
        setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                // 按下时图像变灰
                setColorFilter(Color.GRAY, Mode.MULTIPLY)
            MotionEvent.ACTION_UP,
                // 手指离开或取消操作时恢复原色
            MotionEvent.ACTION_CANCEL -> setColorFilter(Color.TRANSPARENT)
            else -> {
            }
        }
        return false
    }
}
