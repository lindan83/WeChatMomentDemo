package com.lance.wechatmoments.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import kotlin.math.ceil


/**
 * @author lindan
 * 九宫格图片
 * 参考 https://github.com/HMY314/NineGridLayout.git
 */
abstract class AbstractNineImageGridView : ViewGroup {
    private var spacing = DEFAULT_SPACING
    private var columns: Int = 0
    private var rows: Int = 0
    private var totalWidth: Int = 0
    private var singleWidth: Int = 0

    private var showAll = false
    private var first = true
    private val imageList = ArrayList<String>()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray =
            context.obtainStyledAttributes(attrs, com.lance.wechatmoments.demo.R.styleable.AbstractNineImageGridView)

        spacing = typedArray.getDimension(
            com.lance.wechatmoments.demo.R.styleable.AbstractNineImageGridView_spacing,
            DEFAULT_SPACING
        )
        typedArray.recycle()
        init()
    }

    private fun init() {
        if (getListSize(imageList) == 0) {
            visibility = View.GONE
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        singleWidth = ((totalWidth - spacing * (3 - 1)) / 3).toInt()
        if (first) {
            notifyDataSetChanged()
            first = false
        }
    }

    /**
     * 设置间隔
     *
     * @param spacing
     */
    fun setSpacing(spacing: Float) {
        this.spacing = spacing
    }

    /**
     * 设置是否显示所有图片（超过最大数时）
     *
     * @param isShowAll
     */
    fun setShowAll(isShowAll: Boolean) {
        showAll = isShowAll
    }

    fun setImageUrls(imageUrls: List<String>) {
        if (getListSize(imageUrls) == 0) {
            visibility = View.GONE
            return
        }
        visibility = View.VISIBLE

        imageList.clear()
        imageList.addAll(imageUrls)

        if (!first) {
            notifyDataSetChanged()
        }
    }

    fun notifyDataSetChanged() {
        post(object : TimerTask() {
            override fun run() {
                refresh()
            }
        })
    }

    private fun refresh() {
        removeAllViews()
        val size = getListSize(imageList)
        visibility = if (size > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (size == 1) {
            val url = imageList[0]
            val imageView = createImageView(0, url)

            //避免在ListView中一张图未加载成功时，布局高度受其他item影响
            val params = layoutParams
            params.height = singleWidth
            layoutParams = params
            imageView.layout(0, 0, singleWidth, singleWidth)

            val isShowDefault = displayOneImage(imageView, url, totalWidth)
            if (isShowDefault) {
                layoutImageView(imageView, 0, url, false)
            } else {
                addView(imageView)
            }
            return
        }

        generateChildrenLayout(size)
        layoutParams()

        for (i in 0 until size) {
            val url = imageList[i]
            val imageView: RatioImageView
            if (!showAll) {
                if (i < MAX_COUNT - 1) {
                    imageView = createImageView(i, url)
                    layoutImageView(imageView, i, url, false)
                } else { //第9张时
                    if (size <= MAX_COUNT) {
                        //刚好第9张
                        imageView = createImageView(i, url)
                        layoutImageView(imageView, i, url, false)
                    } else {//超过9张
                        imageView = createImageView(i, url)
                        layoutImageView(imageView, i, url, true)
                        break
                    }
                }
            } else {
                imageView = createImageView(i, url)
                layoutImageView(imageView, i, url, false)
            }
        }
    }

    private fun layoutParams() {
        val singleHeight = singleWidth

        //根据子view数量确定高度
        val params = layoutParams
        params.height = (singleHeight * rows + spacing * (rows - 1)).toInt()
        layoutParams = params
    }

    private fun createImageView(i: Int, url: String): RatioImageView {
        val imageView = RatioImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setOnClickListener { v -> onClickImage(v, i, url, imageList) }
        return imageView
    }

    /**
     * @param imageView
     * @param url
     * @param showNumFlag 是否在最大值的图片上显示还有未显示的图片张数
     */
    @SuppressLint("SetTextI18n")
    private fun layoutImageView(imageView: RatioImageView, i: Int, url: String, showNumFlag: Boolean) {
        val singleWidth = ((totalWidth - spacing * (3 - 1)) / 3).toInt()
        val singleHeight = singleWidth

        val position = findPosition(i)
        val left = ((singleWidth + spacing) * position[1]).toInt()
        val top = ((singleHeight + spacing) * position[0]).toInt()
        val right = left + singleWidth
        val bottom = top + singleHeight

        imageView.layout(left, top, right, bottom)

        addView(imageView)
        if (showNumFlag) {
            //添加超过最大显示数量的文本
            val overCount = getListSize(imageList) - MAX_COUNT
            if (overCount > 0) {
                val textSize = 30f
                val textView = TextView(context)
                textView.text = "+$overCount"
                textView.setTextColor(Color.WHITE)
                textView.setPadding(0, singleHeight / 2 - getFontHeight(textSize), 0, 0)
                textView.textSize = textSize
                textView.gravity = Gravity.CENTER
                textView.setBackgroundColor(Color.BLACK)
                textView.background.alpha = 120

                textView.layout(left, top, right, bottom)
                addView(textView)
            }
        }
        displayImage(imageView, url)
    }

    private fun findPosition(childNum: Int): IntArray {
        val position = IntArray(2)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (i * columns + j == childNum) {
                    //行
                    position[0] = i
                    //列
                    position[1] = j
                    break
                }
            }
        }
        return position
    }

    /**
     * 根据图片个数确定行列数量
     *
     * @param length
     */
    private fun generateChildrenLayout(length: Int) {
        if (length <= 3) {
            rows = 1
            columns = length
        } else if (length <= 6) {
            rows = 2
            columns = 3
            if (length == 4) {
                columns = 2
            }
        } else {
            columns = 3
            if (showAll) {
                rows = length / 3
                val b = length % 3
                if (b > 0) {
                    rows++
                }
            } else {
                rows = 3
            }
        }

    }

    protected fun setOneImageLayoutParams(imageView: RatioImageView, width: Int, height: Int) {
        imageView.layoutParams = LayoutParams(width, height)
        imageView.layout(0, 0, width, height)

        val params = layoutParams
        params.height = height
        layoutParams = params
    }

    private fun getListSize(list: List<String>?): Int {
        return if (list.isNullOrEmpty()) 0 else list.size
    }

    private fun getFontHeight(fontSize: Float): Int {
        val paint = Paint()
        paint.textSize = fontSize
        val fm = paint.fontMetrics
        return ceil((fm.descent - fm.ascent).toDouble()).toInt()
    }

    /**
     * 显示一张图片
     *
     * @param imageView   ImageView
     * @param url         图片URL
     * @param parentWidth 父控件宽度
     * @return true 代表按照九宫格默认大小显示，false 代表按照自定义宽高显示
     */
    protected abstract fun displayOneImage(imageView: RatioImageView, url: String, parentWidth: Int): Boolean

    /**
     * 显示图片（非单张）
     *
     * @param imageView ImageView
     * @param url       图片URL
     */
    protected abstract fun displayImage(imageView: RatioImageView, url: String)

    /**
     * 单击图片
     * @param view      ImageView
     * @param position  图片索引
     * @param url       图片URL
     * @param imageUrls 图片列表
     */
    protected abstract fun onClickImage(view: View, position: Int, url: String, imageUrls: List<String>)

    companion object {
        private const val DEFAULT_SPACING = 3F
        private const val MAX_COUNT = 9
    }
}
