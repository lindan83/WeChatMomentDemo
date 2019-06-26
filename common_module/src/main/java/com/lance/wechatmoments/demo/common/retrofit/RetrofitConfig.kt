package com.lance.wechatmoments.demo.common.retrofit

import okhttp3.Interceptor

class RetrofitConfig {
    var baseUrl: String = ""
    var connectionTimeout : Long = 15L
    var readTimeout : Long = 20L
    var writeTimeout : Long = 50L
    /**
     * 响应成功状态码
     */
    var successStatusCode : Int = 0
    /**
     * 失败时是否重试请求
     */
    var isRetryOnFail : Boolean = true
    /**
     * 是否调试模式，调试模式将输出日志并添加StethoInterceptor拦截器
     */
    var isDebugMode : Boolean = true
    /**
     * 网络拦截器
     */
    val networkInterceptors: MutableList<Interceptor> = mutableListOf()
    /**
     * 拦截器
     */
    val interceptors: MutableList<Interceptor> = mutableListOf()
}