package com.lance.wechatmoments.demo.common.retrofit

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author lindan
 * Retrofit简易管理单例
 */
object RetrofitManager {

    private var networkConfig: RetrofitConfig? = null
    /**
     * 获取客户端
     */
    /**
     * 设置客户端
     */
    private var okHttpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    /**
     * 初始化Retrofit
     */
    fun init(config: RetrofitConfig) {
        this.networkConfig = config
        val builder = OkHttpClient.Builder()
                .connectTimeout(config.connectionTimeout, TimeUnit.SECONDS)
                .readTimeout(config.readTimeout, TimeUnit.SECONDS)
                .writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(config.isRetryOnFail)
        if (config.isDebugMode) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
            builder.addNetworkInterceptor(StethoInterceptor())
        }
        //添加自定义的拦截器
        val interceptors = config.interceptors
        if (interceptors.isNotEmpty()) {
            for (interceptor in interceptors) {
                builder.addInterceptor(interceptor)
            }
        }
        val networkInterceptors = config.networkInterceptors
        if (networkInterceptors.isNotEmpty()) {
            for (interceptor in networkInterceptors) {
                builder.addNetworkInterceptor(interceptor)
            }
        }
        okHttpClient = builder.build()

        val retrofitBuilder = Retrofit.Builder()
                .baseUrl(config.baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient!!)
        retrofit = retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    /**
     * 获取接口访问服务类对象，不缓存
     *
     * @param service 服务类Class
     * @param <T>     服务类Class泛型类型
     * @return 服务类对象
    </T> */
    fun <T> create(service: Class<T>): T {
        if(retrofit == null) {
            throw IllegalStateException("You must call init method first!!")
        }
        return retrofit!!.create(service)
    }
}
