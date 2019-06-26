package com.lance.wechatmoments.demo.widget

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt

/**
 * @author lindan
 */
abstract class SpannableClickable : ClickableSpan, View.OnClickListener {
    /**
     * text颜色
     */
    @ColorInt
    private var textColor: Int = 0

    constructor() {
        this.textColor = DEFAULT_COLOR
    }

    constructor(textColor: Int) {
        this.textColor = textColor
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)

        ds.color = textColor
        ds.isUnderlineText = false
        ds.clearShadowLayer()
    }

    companion object {
        @ColorInt
        private val DEFAULT_COLOR = Color.parseColor("#8290AF")
    }
}