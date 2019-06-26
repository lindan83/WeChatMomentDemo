package com.lance.wechatmoments.demo.activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.lance.wechatmoments.demo.R
import com.lance.wechatmoments.demo.adapter.MomentAdapter
import com.lance.wechatmoments.demo.common.base.BaseActivity
import com.lance.wechatmoments.demo.common.base.viewmodel.ResponseState
import com.lance.wechatmoments.demo.common.dp2px
import com.lance.wechatmoments.demo.common.imageloader.ImageLoader
import com.lance.wechatmoments.demo.common.toastDefault
import com.lance.wechatmoments.demo.models.Tweet
import com.lance.wechatmoments.demo.models.User
import com.lance.wechatmoments.demo.viewmodel.MomentsViewModel
import kotlinx.android.synthetic.main.activity_moments.*
import qiu.niorgai.StatusBarCompat
import kotlin.math.abs

/**
 * @author lindan
 * 2018-06-24
 * 朋友圈演示入口
 */
class MomentsActivity : BaseActivity<MomentsViewModel>(), LifecycleOwner {
    private val adapter: MomentAdapter by lazy {
        val instance = MomentAdapter(object : MomentAdapter.ActionCallback {
            /**
             * 点赞或取消点赞
             */
            override fun onLike(index: Int, tweet: Tweet) {
                val self = viewModel.getSelfInfo()
                self?.let {
                    tweet.like(it)
                    adapter.notifyItemChanged(index)
                }
            }

            override fun doComment(index: Int, tweet: Tweet) {
                toastDefault("暂未实现")
            }
        })
        instance.setEnableLoadMore(false)
        instance
    }

    override fun initIntentData() {}

    override fun onInitViewModel() {
        val momentsObserver = Observer<List<Tweet>> {
            if (viewModel.pageNo < 2) {
                //刷新操作
                adapter.setNewData(it)
            } else {
                if (it != null) {
                    adapter.addData(it)
                }
            }
            refreshLayout.finishLoadMore()
            refreshLayout.finishRefresh()
        }
        val userProfile = Observer<User> {
            tvName.text = it.nick ?: it.username
            ImageLoader.get().loadUrl(ivCover, it.profileImage ?: "")
            ImageLoader.get().loadRoundRectImage(ivAvatar, it.avatar ?: "", dp2px(12F).toInt())
        }
        viewModel.subscribe(this, momentsObserver, userProfile)

        viewModel.responseStateLiveData.observe(this, Observer {
            if (it == ResponseState.TYPE_NO_MORE) {
                refreshLayout.finishRefresh(0, true, true)
            }
        })
    }

    override fun getLayoutResId() = R.layout.activity_moments

    override fun initViews() {
        StatusBarCompat.translucentStatusBar(this)
        refreshLayout.setOnRefreshListener {
            //下拉刷新
            viewModel.refresh()
        }
        refreshLayout.setOnLoadMoreListener {
            //上拉加载更多
            if (viewModel.noMoreData) {
                refreshLayout.finishLoadMore(0, true, true)
                return@setOnLoadMoreListener
            }
            viewModel.loadMomentsData(false)
        }
        collapseToolbar.setCollapsedTitleTextColor(Color.WHITE)
        collapseToolbar.setExpandedTitleTextColor(ColorStateList.valueOf(Color.WHITE))
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val color = Color.argb(200, 0, 0, 0)
            collapseToolbar.setCollapsedTitleTextColor(color)
            if (abs(verticalOffset) >= appBar.totalScrollRange) {
                //折叠状态
                collapseToolbar.title = "朋友圈"
                ivCover.visibility = View.GONE
                ivAvatar.visibility = View.GONE
            } else {
                //展开状态
                collapseToolbar.title = ""
                ivCover.visibility = View.VISIBLE
                ivAvatar.visibility = View.VISIBLE
            }
        })

        rvMoments.adapter = adapter
    }

    override fun loadData() {
        viewModel.loadUserProfile()
        viewModel.loadMomentsData(true)
    }
}
