package com.lance.wechatmoments.demo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author lindan
 * 避免大图手势异常的ViewPager
 */
class PhotoViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
            false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
    }
}