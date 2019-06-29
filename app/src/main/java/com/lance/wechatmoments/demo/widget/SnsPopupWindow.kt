package com.lance.wechatmoments.demo.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupWindow
import android.widget.TextView
import com.lance.wechatmoments.demo.R
import com.lance.wechatmoments.demo.app.UserHolder
import com.lance.wechatmoments.demo.common.dp2px
import com.lance.wechatmoments.demo.models.Tweet
import java.util.*

/**
 * @author lindan
 * 点赞评论等社会化操作的弹出框
 */
class SnsPopupWindow(context: Context) : PopupWindow(), OnClickListener {
    private val tvZan: TextView
    private val tvComment: TextView
    var currentIndex: Int = -1
        private set
    var currentTweet: Tweet? = null
        private set

    /**
     * 实例化一个矩形
     */
    private val rect = Rect()
    /**
     * 坐标的位置（x、y）
     */
    private val location = IntArray(2)
    /**
     * 弹窗子类项选中时的监听
     */
    private var itemClickListener: OnItemClickListener? = null
    /**
     * 定义弹窗子类项列表
     */
    private val actionItems = ArrayList<ActionItem>()

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun getActionItems(): List<ActionItem> {
        return actionItems
    }

    fun setActionItems(actionItems: List<ActionItem>?) {
        this.actionItems.clear()
        if (!actionItems.isNullOrEmpty()) {
            this.actionItems.addAll(actionItems)
        }
    }


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.popupwindow_sns_social, null)
        tvZan = view.findViewById(R.id.tvZan)
        tvComment = view.findViewById(R.id.tvComment)
        tvZan.setOnClickListener(this)
        tvComment.setOnClickListener(this)

        this.contentView = view
        this.width = context.dp2px(100F).toInt()
        this.height = context.dp2px(30F).toInt()
        this.isFocusable = true
        this.isOutsideTouchable = true
        this.update()
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0)
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismissListener，设置其他控件变化等操作
        this.setBackgroundDrawable(dw)
        this.animationStyle = R.style.socialPopWindowAnim

        initItemData()
    }

    private fun initItemData() {
        addAction(ActionItem("赞"))
        addAction(ActionItem("评论"))
    }

    fun showPopupWindow(parent: View) {
        parent.getLocationOnScreen(location)
        // 设置矩形的大小
        rect.set(location[0], location[1], location[0] + parent.width, location[1] + parent.height)
        tvZan.text = actionItems[0].title
        if (!this.isShowing) {
            showAtLocation(
                parent,
                Gravity.NO_GRAVITY,
                location[0] - this.width,
                location[1] - (this.height - parent.height) / 2
            )
        } else {
            dismiss()
        }
    }

    override fun onClick(view: View) {
        dismiss()
        when (view.id) {
            R.id.tvZan -> itemClickListener?.onItemClick(actionItems[0], 0)
            R.id.tvComment -> itemClickListener?.onItemClick(actionItems[1], 1)
            else -> {
            }
        }
    }

    /**
     * 添加子类项
     */
    private fun addAction(action: ActionItem?) {
        if (action != null) {
            actionItems.add(action)
        }
    }

    /**
     * 设置当前操作的动态条目Item
     * @param index item index
     * @param tweet current item
     */
    fun setCurrentTweet(index: Int, tweet: Tweet) {
        currentIndex = index
        currentTweet = tweet
        val zanActionItem = actionItems[0]
        val likeUsers = tweet.getLikeUsers()
        zanActionItem.title = if (likeUsers.contains(UserHolder.userProfile)) "取消赞" else "赞"
    }

    /**
     * 弹窗子类项按钮监听事件
     */
    interface OnItemClickListener {
        fun onItemClick(item: ActionItem, position: Int)
    }
}
