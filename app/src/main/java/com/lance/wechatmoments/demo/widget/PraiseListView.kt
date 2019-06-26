package com.lance.wechatmoments.demo.widget

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.lance.wechatmoments.demo.R
import com.lance.wechatmoments.demo.models.User

/**
 * @author lindan
 * 点赞列表
 */
class PraiseListView : AppCompatTextView {
    private var itemColor: Int = 0
    private var itemSelectorColor: Int = 0
    var data: List<User>? = null
        set(data) {
            field = data
            notifyDataSetChanged()
        }
    var onItemClickListener: OnItemClickListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PraiseListView, 0, 0)
        try {
            //TextView的默认颜色
            itemColor = typedArray.getColor(
                R.styleable.PraiseListView_praiseItemColor,
                ContextCompat.getColor(context, R.color.c_333333)
            )
            itemSelectorColor = typedArray.getColor(
                R.styleable.PraiseListView_praiseItemSelectedColor,
                ContextCompat.getColor(context, R.color.c_CCCCCC)
            )
        } finally {
            typedArray.recycle()
        }
    }


    fun notifyDataSetChanged() {
        val builder = SpannableStringBuilder()
        if (!this.data.isNullOrEmpty()) {
            //添加点赞图标
            builder.append(setImageSpan())
            var item: User?
            val count = this.data!!.size
            for (i in 0 until count) {
                if (i > 3) {
                    break
                }
                item = this.data!![i]
                builder.append(setClickableSpan(item.nick, i))
                if (i != count - 1) {
                    builder.append(", ")
                }
            }
            if (count > 3) {
                builder.delete(builder.length - 2, builder.length)
                builder.append("......等").append(count.toString()).append("人")
            }
        }

        text = builder
        movementMethod = DynamicMovementMethod(itemSelectorColor)
    }


    private fun setImageSpan(): SpannableString {
        val text = "  "
        val imgSpanText = SpannableString(text)
        imgSpanText.setSpan(
            ImageSpan(context, R.mipmap.icon_praise, DynamicDrawableSpan.ALIGN_BASELINE),
            0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return imgSpanText
    }

    private fun setClickableSpan(textStr: String?, position: Int): SpannableString {
        val subjectSpanText = SpannableString(textStr)
        subjectSpanText.setSpan(object : SpannableClickable(itemColor) {
            override fun onClick(widget: View) {
                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(position)
                }
            }
        }, 0, subjectSpanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return subjectSpanText
    }


    interface OnItemClickListener {
        fun onClick(position: Int)
    }
}
