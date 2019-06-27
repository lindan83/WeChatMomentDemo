package com.lance.wechatmoments.demo.common.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lance.wechatmoments.demo.common.retrofit.scheduler.SchedulerUtils
import io.reactivex.Observable

/**
 * @author lindan
 * ViewModel基类
 */
abstract class BaseViewModel : ViewModel() {
    val requestTypeLiveData = MutableLiveData<RequestType>()

    val responseStateLiveData = MutableLiveData<ResponseState>()

    val throwableLiveData = MutableLiveData<Throwable>()

    /**
     * 默认的订阅与观察线程调度：IO线程订阅/UI线程观察
     */
    fun <T> Observable<T>.ioToMain(): Observable<T> {
        return this.compose(SchedulerUtils.ioToMain())
    }
}