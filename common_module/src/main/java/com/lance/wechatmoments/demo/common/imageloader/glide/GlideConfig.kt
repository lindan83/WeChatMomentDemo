package com.lance.wechatmoments.demo.common.imageloader.glide

import android.widget.ImageView

import com.bumptech.glide.request.RequestOptions
import com.lance.wechatmoments.demo.common.imageloader.config.DiskCacheStrategy
import com.lance.wechatmoments.demo.common.imageloader.config.ImageLoaderConfig
import com.lance.wechatmoments.demo.common.imageloader.config.Priority

/**
 * @author lindan
 * Glide工具配置
 */
internal class GlideConfig(val config: ImageLoaderConfig) {

    private val requestOptions: RequestOptions

    init {
        var options: RequestOptions
        options = when (config.diskCacheStrategy) {
            DiskCacheStrategy.ALL -> RequestOptions.diskCacheStrategyOf(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            DiskCacheStrategy.AUTOMATIC -> RequestOptions.diskCacheStrategyOf(com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC)
            DiskCacheStrategy.NONE -> RequestOptions.diskCacheStrategyOf(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
            DiskCacheStrategy.RESOURCE -> RequestOptions.diskCacheStrategyOf(com.bumptech.glide.load.engine.DiskCacheStrategy.RESOURCE)
        }
        options = when (config.priority) {
            Priority.IMMEDIATE -> options.priority(com.bumptech.glide.Priority.IMMEDIATE)
            Priority.HIGH -> options.priority(com.bumptech.glide.Priority.HIGH)
            Priority.NORMAL -> options.priority(com.bumptech.glide.Priority.NORMAL)
            Priority.LOW -> options.priority(com.bumptech.glide.Priority.LOW)
        }
        if (config.placeholderResId != null) {
            options = options.placeholder(config.placeholderResId!!)
        }
        if (config.errorResId != null) {
            options = options.error(config.errorResId!!)
        }
        if (config.fallbackResId != null) {
            options = options.fallback(config.fallbackResId!!)
        }
        val scaleType = config.scaleType
        if (scaleType == ImageView.ScaleType.FIT_CENTER
                || scaleType == ImageView.ScaleType.FIT_START
                || scaleType == ImageView.ScaleType.FIT_END) {
            options = options.fitCenter()
        } else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            options = options.centerCrop()
        } else if (scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            options = options.centerInside()
        }
        requestOptions = options
    }

    fun buildRequestOptions(): RequestOptions {
        return requestOptions.autoClone().clone()
    }
}
