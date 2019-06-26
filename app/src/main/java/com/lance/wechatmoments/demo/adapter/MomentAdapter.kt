package com.lance.wechatmoments.demo.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.os.Build
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView
import com.lance.wechatmoments.demo.R
import com.lance.wechatmoments.demo.activity.PhotoActivity
import com.lance.wechatmoments.demo.common.dp2px
import com.lance.wechatmoments.demo.common.imageloader.ImageLoader
import com.lance.wechatmoments.demo.models.Tweet
import com.lance.wechatmoments.demo.models.User
import com.lance.wechatmoments.demo.util.UrlUtils
import com.lance.wechatmoments.demo.widget.*

/**
 * @author lindan
 * 列表适配器
 */
class MomentAdapter(val actionCallback: ActionCallback) :
    BaseQuickAdapter<Tweet, BaseViewHolder>(R.layout.item_moments),
    BaseQuickAdapter.OnItemChildClickListener {

    private val avatarRectRadius: Int by lazy {
        mContext.dp2px(4F).toInt()
    }

    private val snsPopupWindow: SnsPopupWindow by lazy {
        val instance = SnsPopupWindow(mContext)
        instance.setItemClickListener(object : SnsPopupWindow.OnItemClickListener {
            override fun onItemClick(item: ActionItem, position: Int) {
                when (position) {
                    0 -> {
                        //点赞
                        val currentIndex = instance.currentIndex
                        if (currentIndex !in 0 until data.size) {
                            return
                        }
                        val tweet = data[instance.currentIndex]
                        actionCallback.onLike(currentIndex, tweet)
                    }
                    else -> {
                        //评论
                        val currentIndex = instance.currentIndex
                        if (currentIndex !in 0 until data.size) {
                            return
                        }
                        val tweet = data[instance.currentIndex]
                        actionCallback.doComment(currentIndex, tweet)
                    }
                }

            }
        })
        instance
    }

    init {
        setEnableLoadMore(true)
        setLoadMoreView(SimpleLoadMoreView())
        setPreLoadNumber(1)
        isUpFetchEnable = false
        onItemChildClickListener = this
    }

    override fun convert(helper: BaseViewHolder?, item: Tweet?) {
        if (helper == null || item == null) {
            return
        }
        //头像
        ImageLoader.get().loadRoundRectImage(
            helper.getView(R.id.ivAvatar), item.sender?.avatar
                ?: "", avatarRectRadius
        )

        //姓名
        val name = item.sender?.nick ?: item.sender?.username
        helper.setText(R.id.tvName, name)

        //内容
        val content = item.content
        val tvContent = helper.getView<ExpandTextView>(R.id.expandContent)
        if (content.isNullOrEmpty()) {
            tvContent.visibility = View.GONE
        } else {
            tvContent.visibility = View.VISIBLE
            tvContent.setText(UrlUtils.formatUrlString(content))
        }

        //图片
        val images = item.images
        val nineImageGridView = helper.getView<NineImageGridView>(R.id.nineImageList)
        nineImageGridView.onItemClickListener = object : NineImageGridView.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                //点击查看大图
                val size = mContext.dp2px(40F).toInt()
                val imageSize = PhotoActivity.ImageSize(size, size)
                PhotoActivity.startImagePagerActivity(
                    PhotoActivity.IntentParameter().apply {
                        this.context = mContext
                        this.imageUrls = images?.map { it.url ?: "" }
                        this.position = position
                        this.imageSize = imageSize
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            val transitionName = mContext.getString(R.string.transition_image_detail)
                            val bundle =
                                ActivityOptions.makeSceneTransitionAnimation(mContext as Activity, view, transitionName)
                                    .toBundle()
                            this.bundle = bundle
                        }
                    }
                )
            }
        }
        if (images.isNullOrEmpty()) {
            nineImageGridView.visibility = View.GONE
            nineImageGridView.setImageUrls(emptyList())
        } else {
            nineImageGridView.visibility = View.VISIBLE
            nineImageGridView.setImageUrls(images.map { it.url ?: "" })
        }

        //点击点赞、评论
        helper.addOnClickListener(R.id.ivSns)

        //点赞列表
        val likes = item.getLikeUsers()
        val hasLikes = likes.isNotEmpty()
        val praiseListView = helper.getView<PraiseListView>(R.id.praiseListView)
        if (hasLikes) {
            praiseListView.visibility = View.VISIBLE
            praiseListView.data = likes
        } else {
            praiseListView.visibility = View.GONE
        }

        //评论列表
        val comments = item.comments
        val hasComments = !comments.isNullOrEmpty()
        val commentListView = helper.getView<CommentListView>(R.id.commentListView)
        commentListView.setOnItemClickListener(object : CommentListView.OnItemClickListener {
            /**
             * 点击评论项，预留
             */
            override fun onItemClick(position: Int) {
            }

            /**
             * 长按评论项
             */
            override fun onItemLongClick(position: Int) {
            }

            /**
             * 点击更多，预留
             */
            override fun onItemMoreClick() {
            }

            /**
             * 点击评论人名称，预留
             */
            override fun onItemUserClick(position: Int, user: User?) {
            }
        })
        if (!hasComments) {
            commentListView.visibility = View.GONE
        } else {
            commentListView.visibility = View.VISIBLE
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.ivSns -> {
                //弹出点击、评论操作窗口
                snsPopupWindow.setCurrentTweet(position, data[position])
                snsPopupWindow.showPopupWindow(view)
            }
        }
    }

    /**
     * 点赞或评论操作回调监听
     */
    interface ActionCallback {
        /**
         * 点赞
         * @param index 点赞的动态索引
         * @param tweet 点赞的动态
         */
        fun onLike(index: Int, tweet: Tweet)

        /**
         * 发起评论
         * @param index 评论的动态索引
         * @param tweet 评论的动态
         */
        fun doComment(index: Int, tweet: Tweet)
    }
}