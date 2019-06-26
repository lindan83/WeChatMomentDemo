package com.lance.wechatmoments.demo.widget

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lance.wechatmoments.demo.R
import com.lance.wechatmoments.demo.models.Comment
import com.lance.wechatmoments.demo.models.User
import com.lance.wechatmoments.demo.util.UrlUtils
import java.util.*

/**
 * @author lindan
 * 评论列表
 */
class CommentListView : LinearLayout {
    private var itemColor: Int = 0
    private var itemSelectorColor: Int = 0
    private var onItemClickListener: OnItemClickListener? = null
    var data: MutableList<Comment> = ArrayList()
        set(value) {
            field.clear()
            if (!value.isNullOrEmpty()) {
                field.addAll(value)
            }
            notifyDataSetChanged()
        }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    constructor(context: Context) : super(context) {
        initAttrs(context, null)
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs)
        init()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommentListView, 0, 0)
            try {
                //TextView的默认颜色
                itemColor = typedArray.getColor(
                    R.styleable.CommentListView_commentItemColor,
                    ContextCompat.getColor(context, R.color.c_333333)
                )
                itemSelectorColor = typedArray.getColor(
                    R.styleable.CommentListView_commentItemSelectorColor,
                    ContextCompat.getColor(context, R.color.c_CCCCCC)
                )
            } finally {
                typedArray.recycle()
            }
        } else {
            itemColor = ContextCompat.getColor(context, R.color.c_333333)
            itemSelectorColor = ContextCompat.getColor(context, R.color.c_CCCCCC)
        }
    }

    private fun init() {
        orientation = VERTICAL
    }

    private fun notifyDataSetChanged() {
        removeAllViews()
        if (this.data.isNullOrEmpty()) {
            return
        }
        val size = this.data.size
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        for (i in 0 until size) {
            val view = getView(i)
            addView(view, i, layoutParams)
        }
    }

    private fun getView(position: Int): View {
        val convertView = LayoutInflater.from(context).inflate(R.layout.widget_item_comment, null, false)

        val tvComment = convertView.findViewById<TextView>(R.id.tvComment)
        val dynamicMovementMethod = DynamicMovementMethod(itemSelectorColor, itemSelectorColor)

        val bean = this.data[position]
        val name = bean.sender!!.nick

        val builder = SpannableStringBuilder()
        builder.append(setClickableSpan(position, name, bean.sender))

        builder.append(": ")
        //转换表情字符
        val contentBodyStr = bean.content
        builder.append(UrlUtils.formatUrlString(contentBodyStr!!))
        tvComment.text = builder

        tvComment.movementMethod = dynamicMovementMethod
        tvComment.setOnClickListener {
            if (dynamicMovementMethod.isPassToTv) {
                if (onItemClickListener != null) {
                    onItemClickListener!!.onItemClick(position)
                }
            }
        }
        tvComment.setOnLongClickListener {
            if (dynamicMovementMethod.isPassToTv) {
                if (onItemClickListener != null) {
                    onItemClickListener!!.onItemLongClick(position)
                }
                true
            }
            false
        }

        return convertView
    }

    private fun setClickableSpan(position: Int, text: String?, user: User?): SpannableString {
        val subjectSpanText = SpannableString(text)
        subjectSpanText.setSpan(object : SpannableClickable(itemColor) {
            override fun onClick(widget: View) {
                if (onItemClickListener != null) {
                    onItemClickListener!!.onItemUserClick(position, user)
                }
            }
        }, 0, subjectSpanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return subjectSpanText
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)

        fun onItemLongClick(position: Int)

        fun onItemMoreClick()

        fun onItemUserClick(position: Int, user: User?)
    }
}