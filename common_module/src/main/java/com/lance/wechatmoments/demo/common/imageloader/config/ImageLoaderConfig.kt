package com.lance.wechatmoments.demo.common.imageloader.config

import android.widget.ImageView
import androidx.annotation.DrawableRes

/**
 * @author lindan
 * 图片加载配置
 */
class ImageLoaderConfig {
    /**
     * 加载过程的普通占位图
     */
    @DrawableRes
    var placeholderResId: Int? = null
    /**
     * 错误占位图
     */
    @DrawableRes
    var errorResId: Int? = null
    /**
     * 加载失败占位图
     */
    @DrawableRes
    var fallbackResId: Int? = null
    /**
     * 优先级
     */
    var priority: Priority = Priority.NORMAL
    /**
     * 磁盘缓存方式
     */
    var diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL
    /**
     * 缩放模式
     */
    var scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER
}
