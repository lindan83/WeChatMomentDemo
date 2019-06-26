package com.lance.wechatmoments.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lance.wechatmoments.demo.R


/**
 * @author lindan
 * 折叠TextView
 */
class ExpandTextView : LinearLayout {

    private var tvContent: TextView? = null
    private var tvShowMore: TextView? = null

    private var showLines: Int = 0
    private var maxLength: Int = 0

    private var expandText: String? = null
    private var collapseText: String? = null

    /**原始文本内容*/
    private var originContent: CharSequence? = null

    private var onClickContentListener: OnClickContentListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(attrs)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
        initView()
    }

    fun setText(content: CharSequence) {
        originContent = content
        tvContent!!.text = content
        tvContent!!.post {
            val linCount = tvContent!!.lineCount
            if (showLines > 0) {
                if (linCount > showLines) {
                    tvContent!!.maxLines = showLines
                    tvShowMore!!.visibility = View.VISIBLE
                    tvShowMore!!.text = expandText
                } else {
                    tvShowMore!!.visibility = View.GONE
                }
            } else if (maxLength > 0) {
                if (content.length > maxLength) {
                    tvShowMore!!.visibility = View.VISIBLE
                    tvShowMore!!.text = expandText
                } else {
                    tvShowMore!!.visibility = View.GONE
                }
            }
        }
        tvContent!!.movementMethod = DynamicMovementMethod(ContextCompat.getColor(context, R.color.c_CCCCCC))
    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.widget_expand_text, this)
        tvContent = findViewById(R.id.tvContent)
        if (maxLength > 0) {
            tvContent!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            tvContent!!.ellipsize = TextUtils.TruncateAt.END
        }
        tvShowMore = findViewById(R.id.tvShowMore)
        tvShowMore!!.setOnClickListener {
            val textStr = tvShowMore!!.text.toString().trim()
            if (expandText == textStr) {
                if (onClickContentListener != null) {
                    onClickContentListener!!.onExpand()
                } else {
                    tvContent!!.maxLines = Integer.MAX_VALUE
                    tvShowMore!!.text = collapseText
                    tvContent!!.text = originContent
                }
            } else {
                if (onClickContentListener != null) {
                    onClickContentListener!!.onCollapse()
                } else {
                    tvContent!!.maxLines = showLines
                    tvShowMore!!.text = expandText
                    tvContent!!.text = originContent
                }
            }
        }
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ExpandTextView, 0, 0)
        try {
            showLines = typedArray.getInt(R.styleable.ExpandTextView_showLines, DEFAULT_MAX_LINES)
            maxLength = typedArray.getInt(R.styleable.ExpandTextView_maxLength, DEFAULT_MAX_LENGTH)
            expandText = typedArray.getString(R.styleable.ExpandTextView_expandText)
            collapseText = typedArray.getString(R.styleable.ExpandTextView_collapseText)
        } finally {
            typedArray.recycle()
        }
        if (showLines <= 0) {
            showLines = DEFAULT_MAX_LINES
        }
        if (maxLength <= 0) {
            maxLength = DEFAULT_MAX_LENGTH
        }
        if (expandText.isNullOrEmpty()) {
            expandText = DEFAULT_EXPAND_TEXT
        }
        if (collapseText.isNullOrEmpty()) {
            collapseText = DEFAULT_COLLAPSE_TEXT
        }
    }

    /**
     * 内容展开或折叠监听器
     */
    interface OnClickContentListener {
        fun onExpand()

        fun onCollapse()
    }

    fun setOnClickContentListener(listener: OnClickContentListener) {
        onClickContentListener = listener
    }

    companion object {
        private const val TAG = "ExpandTextView"
        private const val DEFAULT_MAX_LINES = Integer.MAX_VALUE
        private const val DEFAULT_MAX_LENGTH = Integer.MAX_VALUE
        private const val DEFAULT_EXPAND_TEXT = "全文"
        private const val DEFAULT_COLLAPSE_TEXT = "收起"
    }
}