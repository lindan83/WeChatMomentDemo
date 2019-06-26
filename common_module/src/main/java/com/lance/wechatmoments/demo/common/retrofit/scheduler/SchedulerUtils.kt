package com.lance.wechatmoments.demo.common.retrofit.scheduler

/**
 * @author lindan
 * 线程调度工具
 */
object SchedulerUtils {

    /**
     * 订阅在IO线程，观察在UI线程
     */
    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
}
