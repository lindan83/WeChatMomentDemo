package com.lance.wechatmoments.demo.widget

import android.graphics.Color
import android.text.Selection
import android.text.Spannable
import android.text.method.BaseMovementMethod
import android.text.method.Touch
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView


/**
 * @author lindan
 * MovementMethod 使TextView中的Spannable可点击
 */
class DynamicMovementMethod : BaseMovementMethod {
    /**
     * 整个TextView的背景色
     */
    private var textViewBgColor: Int = 0
    /**
     * 点击部分文字时部分文字的背景色
     */
    private var clickableSpanBgColor: Int = 0

    private var bgSpan: BackgroundColorSpan? = null
    private var clickLinks: Array<ClickableSpan>? = null
    /**
     * true：响应TextView的点击事件， false：响应设置的ClickableSpan事件
     */
    var isPassToTv = true
        private set

    constructor() {
        this.textViewBgColor = DEFAULT_COLOR
        this.clickableSpanBgColor = DEFAULT_CLICKABLE_COLOR
    }

    /**
     * @param clickableSpanBgColor 点击选中部分时的背景色
     */
    constructor(clickableSpanBgColor: Int) {
        this.clickableSpanBgColor = clickableSpanBgColor
        this.textViewBgColor = DEFAULT_COLOR
    }

    /**
     * @param clickableSpanBgColor 点击选中部分时的背景色
     * @param textViewBgColor      整个textView点击时的背景色
     */
    constructor(clickableSpanBgColor: Int, textViewBgColor: Int) {
        this.textViewBgColor = textViewBgColor
        this.clickableSpanBgColor = clickableSpanBgColor
    }

    override fun onTouchEvent(
        widget: TextView, buffer: Spannable,
        event: MotionEvent
    ): Boolean {

        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            clickLinks = buffer.getSpans(off, off, ClickableSpan::class.java)
            if (!clickLinks.isNullOrEmpty()) {
                // 点击的是Span区域，不要把点击事件传递
                isPassToTv = false
                Selection.setSelection(
                    buffer,
                    buffer.getSpanStart(clickLinks!![0]),
                    buffer.getSpanEnd(clickLinks!![0])
                )
                //设置点击区域的背景色
                bgSpan = BackgroundColorSpan(clickableSpanBgColor)
                buffer.setSpan(
                    bgSpan,
                    buffer.getSpanStart(clickLinks!![0]),
                    buffer.getSpanEnd(clickLinks!![0]),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                isPassToTv = true
                // TextView选中效果
                widget.setBackgroundColor(textViewBgColor)
            }

        } else if (action == MotionEvent.ACTION_UP) {
            if (!clickLinks.isNullOrEmpty()) {
                clickLinks!![0].onClick(widget)
                if (bgSpan != null) {
                    //移除点击时设置的背景span
                    buffer.removeSpan(bgSpan)
                }
            }
            Selection.removeSelection(buffer)
            widget.setBackgroundResource(android.R.color.transparent)
        } else {
            if (bgSpan != null) {
                //移除点击时设置的背景span
                buffer.removeSpan(bgSpan)
            }
            widget.setBackgroundResource(android.R.color.transparent)
        }
        return Touch.onTouchEvent(widget, buffer, event)
    }

    companion object {
        private const val TAG = "DynamicMovementMethod"
        private const val DEFAULT_COLOR = Color.TRANSPARENT
        private val DEFAULT_CLICKABLE_COLOR = Color.parseColor("#FF3752")
    }
}
