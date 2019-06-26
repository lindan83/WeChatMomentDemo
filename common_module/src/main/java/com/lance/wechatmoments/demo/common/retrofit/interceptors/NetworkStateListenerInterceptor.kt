package com.lance.wechatmoments.demo.common.retrofit.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.lance.wechatmoments.demo.common.R
import com.lance.wechatmoments.demo.common.retrofit.exception.NetworkException
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author lindan
 * 网络状态监测拦截器
 */
class NetworkStateListenerInterceptor(context: Context) : Interceptor {
    private val applicationContext: Context = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        val connected = isConnectInternet(applicationContext)
        if (connected) {
            try {
                return chain.proceed(chain.request())
            } catch (e: Exception) {
                when (e) {
                    is SocketTimeoutException -> throw NetworkException(applicationContext.getString(R.string.common_str_network_timeout))
                    is UnknownHostException -> throw NetworkException(applicationContext.getString(R.string.common_str_network_hostname_resolve_failure))
                    else -> throw NetworkException(e.message ?: "")
                }
            }

        }
        throw NetworkException(applicationContext.getString(R.string.common_str_network_error))
    }

    private fun isConnectInternet(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        var networkInfo: NetworkInfo? = null
        if (conManager != null) {
            networkInfo = conManager.activeNetworkInfo
        }
        return networkInfo != null && networkInfo.isAvailable
    }
}
