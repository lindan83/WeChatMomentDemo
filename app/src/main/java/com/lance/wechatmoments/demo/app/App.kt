package com.lance.wechatmoments.demo.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.stetho.Stetho
import com.lance.wechatmoments.demo.R
import com.lance.wechatmoments.demo.common.BuildConfig
import com.lance.wechatmoments.demo.common.ContextHolder
import com.lance.wechatmoments.demo.common.config.AppConfig
import com.lance.wechatmoments.demo.common.imageloader.ImageLoader
import com.lance.wechatmoments.demo.common.imageloader.config.DiskCacheStrategy
import com.lance.wechatmoments.demo.common.imageloader.config.ImageLoaderConfig
import com.lance.wechatmoments.demo.common.imageloader.config.Priority
import com.lance.wechatmoments.demo.common.imageloader.glide.GlideLoader
import com.lance.wechatmoments.demo.common.retrofit.RetrofitConfig
import com.lance.wechatmoments.demo.common.retrofit.RetrofitManager
import com.lance.wechatmoments.demo.common.retrofit.interceptors.NetworkStateListenerInterceptor
import timber.log.Timber

/**
 * @author lindan
 * 2019-06-24
 *
 * The application class of this demo
 */
class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        ContextHolder.initial(this)

        //其他配置
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
        //网络配置
        initRetrofit()
        //配置图片加载库
        initImageLoader()
    }

    /**
     * 配置Retrofit參數
     */
    private fun initRetrofit() {
        val retrofitConfig = RetrofitConfig().apply {
            baseUrl = AppConfig.BASE_URL
            isDebugMode = BuildConfig.DEBUG
            connectionTimeout = 20
            readTimeout = 20
            writeTimeout = 30
            interceptors.add(NetworkStateListenerInterceptor(this@App))
            isRetryOnFail = true
        }
        RetrofitManager.init(retrofitConfig)
    }

    private fun initImageLoader() {
        ImageLoader.init(
            GlideLoader(
                ImageLoaderConfig().apply {
                    diskCacheStrategy = DiskCacheStrategy.ALL
                    placeholderResId = R.mipmap.icon_placeholder
                    errorResId = R.mipmap.icon_error
                    fallbackResId = R.mipmap.icon_error
                    priority = Priority.NORMAL
                }
            )
        )
    }
}