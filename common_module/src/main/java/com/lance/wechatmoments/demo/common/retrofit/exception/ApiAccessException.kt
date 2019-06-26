package com.lance.wechatmoments.demo.common.retrofit.exception

import java.util.*

/**
 * @author lindan
 * 网络接口访问异常基类
 */
class ApiAccessException(val status: Int, message: String) : Exception(message) {

    override fun toString(): String {
        return String.format(Locale.CHINA, "status code = %1\$d, message = %2\$s", status, message)
    }
}
