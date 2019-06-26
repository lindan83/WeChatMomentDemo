package com.lance.wechatmoments.demo.common

import android.content.Context
import android.net.ConnectivityManager
import android.util.TypedValue
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import org.jetbrains.annotations.NotNull

/**
 * @author lindan
 * 扩展函数
 */
private object ToastHolder {
    private var toast: Toast? = null
    fun setToast(toast: Toast) {
        ToastHolder.toast = toast
    }

    fun getToast(): Toast? {
        return toast
    }
}

//------------------------------Context扩展函数相关 begin-----------------------------------//
/**
 * 显示Toast
 */
fun Context.toast(@NotNull message: String, duration: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.BOTTOM) {
    var t = ToastHolder.getToast()
    if (t == null) {
        t = Toast.makeText(this.applicationContext, message, duration)
        t.setText(message)
        t.setGravity(gravity, 0, dp2px(48F).toInt())
        t.show()
        ToastHolder.setToast(t)
    } else {
        t.setText(message)
        t.duration = duration
        t.setGravity(gravity, 0, dp2px(48F).toInt())
        t.show()
    }
}

/**
 * 显示Toast
 */
fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(message), duration)
}


fun Context.toastDefault(@StringRes message: Int) {
    toast(message, Toast.LENGTH_SHORT)
}

fun Context.toastDefault(@NonNull message: String) {
    toast(message, Toast.LENGTH_SHORT)
}

/**
 * dp转px
 */
fun Context.dp2px(dpValue: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, resources.displayMetrics)

/**
 * sp转px
 */
fun Context.sp2px(spValue: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, resources.displayMetrics)

/**
 * px转dp
 */
fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * px转sp
 */
fun Context.px2sp(pxValue: Float): Int {
    val scale = resources.displayMetrics.scaledDensity
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * 检测网络是否连接
 */
fun Context.isNetworkConnected(): Boolean {
    val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectionManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
}


/**
 * 检测移动网络是否连接
 */
@Suppress("DEPRECATION")
fun Context.isMobileConnected(): Boolean {
    val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
}

/**
 * 检测Wifi网络是否连接
 */
@Suppress("DEPRECATION")
fun Context.isWifiConnected(): Boolean {
    val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
}
//------------------------------Context扩展函数相关 end-----------------------------------//

/**
 * 显示Toast
 */
fun Fragment.toast(@NotNull message: String, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(message, duration)
}

/**
 * 显示Toast
 */
fun Fragment.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
    activity?.toast(message, duration)
}