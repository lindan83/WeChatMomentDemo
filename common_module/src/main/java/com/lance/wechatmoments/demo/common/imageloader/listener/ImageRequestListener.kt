package com.lance.wechatmoments.demo.common.imageloader.listener

/**
 * @author lindan
 * A class for monitoring the status of a request while images load.
 *
 * @param <R> The type of resource being loaded.
</R> */
interface ImageRequestListener<R> {
    /**
     * 加载失败
     * @param e 异常
     */
    fun onLoadFailed(e: Throwable): Boolean

    /**
     * 加载就绪
     * @param resource 加载的资源
     */
    fun onResourceReady(resource: R): Boolean
}

