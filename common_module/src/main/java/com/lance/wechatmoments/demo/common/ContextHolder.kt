package com.lance.wechatmoments.demo.common

import android.app.Application
import android.content.Context


/**
 * @author lindan
 * 用于获取全局Application Context
 */
object ContextHolder {
    private var applicationContext: Context? = null

    /**
     * 初始化context，如果由于不同机型导致反射获取context失败可以在Application调用此方法
     *
     * @param context Context
     */
    fun initial(context: Context) {
        applicationContext = context.applicationContext
    }

    /**
     * 全局Context
     */
    val context: Context
        get() {
            if (applicationContext == null) {
                try {
                    val application = Class.forName("android.app.ActivityThread")
                            .getMethod("currentApplication").invoke(null, null) as? Application
                    if (application != null) {
                        applicationContext = application
                        return application
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    val application = Class.forName("android.app.AppGlobals")
                            .getMethod("getInitialApplication").invoke(null, null) as? Application
                    if (application != null) {
                        applicationContext = application
                        return application
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                throw IllegalStateException("ContextHolder is not initialed, it is recommend to init with application context.")
            }
            return applicationContext!!
        }
}