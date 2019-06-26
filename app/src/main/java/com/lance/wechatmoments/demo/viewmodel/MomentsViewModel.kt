package com.lance.wechatmoments.demo.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lance.wechatmoments.demo.app.UserHolder
import com.lance.wechatmoments.demo.common.base.viewmodel.BaseViewModel
import com.lance.wechatmoments.demo.common.base.viewmodel.RequestType
import com.lance.wechatmoments.demo.common.base.viewmodel.ResponseState
import com.lance.wechatmoments.demo.common.retrofit.RetrofitManager
import com.lance.wechatmoments.demo.models.Tweet
import com.lance.wechatmoments.demo.models.User
import com.lance.wechatmoments.demo.repository.MomentRepository
import io.reactivex.Observable
import timber.log.Timber

/**
 * @author lindan
 * 2019-06-24
 * ViewModel实现类
 */
class MomentsViewModel : BaseViewModel() {
    /**
     * 数据访问对象
     */
    private val momentRepository: MomentRepository = RetrofitManager.create(MomentRepository::class.java)

    /**
     * 数据集
     */
    private val data = mutableListOf<Tweet>()

    /**
     * 订阅每页的数据LiveData
     */
    private val momentListLiveData = MutableLiveData<List<Tweet>>()

    /**
     * 订阅用户数据LiveData
     */
    private val userProfileLiveData = MutableLiveData<User>()

    /**
     * 当前页码
     */
    var pageNo: Int = 0
        private set

    /**
     * 没有更多数据了
     */
    var noMoreData: Boolean = false
        private set


    /**
     * 订阅LiveData
     */
    fun subscribe(lifecycleOwner: LifecycleOwner, momentsObserver: Observer<List<Tweet>>, userProfileObserver: Observer<User>) {
        momentListLiveData.observe(lifecycleOwner, momentsObserver)
        userProfileLiveData.observe(lifecycleOwner, userProfileObserver)
    }

    /**
     * 加载朋友圈评论分页数据
     * @param reload 是否重新加载
     */
    @SuppressLint("CheckResult")
    fun loadMomentsData(reload: Boolean) {
        requestTypeLiveData.value = if (reload) RequestType.TYPE_REFRESH else RequestType.TYPE_LOAD_MORE
        val page = if (reload) 1 else pageNo + 1
        if (data.isEmpty()) {
            //如果数据还未加载，先加载到内存中
            loadAllMoments {
                if (data.isEmpty()) {
                    //数据仍然为空
                    responseStateLiveData.value = ResponseState.TYPE_EMPTY
                } else {
                    loadMomentsData(reload)
                }
            }
        } else {
            Observable.just(data)
                .map {
                    val dataList = mutableListOf<Tweet>()
                    if(noMoreData) {
                        responseStateLiveData.value = ResponseState.TYPE_NO_MORE
                        return@map dataList.toList()
                    }
                    //数据已加载，取出指定页面的数据
                    val fromIndex = if (reload) {
                        //刷新操作
                        0
                    } else {
                        (page - 1) * PAGE_SIZE
                    }
                    if (fromIndex > data.size - 1) {
                        //没有数据了
                        noMoreData = true
                        responseStateLiveData.value = ResponseState.TYPE_NO_MORE
                        return@map dataList.toList()
                    }
                    //更新页码
                    pageNo = page
                    var endIndex = fromIndex + PAGE_SIZE
                    if (endIndex > data.size) {
                        //数据不足一页
                        endIndex = data.size
                        noMoreData = true
                    }
                    for(i in fromIndex until endIndex) {
                        dataList.add(data[i])
                    }
                    dataList.toList()
                }
                .ioToMain()
                .subscribe({
                    momentListLiveData.value = it
                }, {
                    Timber.tag(TAG).e(it)
                    throwableLiveData.value = it
                })
        }
    }

    /**
     * 重新刷新的操作
     */
    fun refresh() {
        pageNo = 0
        noMoreData = false
        data.clear()
        loadUserProfile()
        loadMomentsData(true)
    }

    /**
     * 加载所有数据到内存中
     */
    @SuppressLint("CheckResult")
    private fun loadAllMoments(callback: () -> Unit) {
        momentRepository.getUserTweets()
                .ioToMain()
                .subscribe({
                    if (it != null) {
                        data.addAll(it.filter { tweet -> tweet.isCorrect() })
                    }
                    callback.invoke()
                }, {
                    Timber.tag(TAG).e(it)
                    throwableLiveData.value = it
                })
    }

    /**
     * 加载用户数据
     */
    @SuppressLint("CheckResult")
    fun loadUserProfile() {
        momentRepository.getUserProfile()
                .ioToMain()
                .subscribe({
                    UserHolder.userProfile = it
                    userProfileLiveData.value = it
                }, {
                    Timber.tag(TAG).e(it)
                    throwableLiveData.value = it
                })
    }

    /**
     * 取得用户信息
     */
    fun getSelfInfo() = userProfileLiveData.value

    override fun onCleared() {
        super.onCleared()
        data.clear()
    }

    companion object {
        const val PAGE_SIZE = 5
        private const val TAG = "MomentsViewModel"
    }
}