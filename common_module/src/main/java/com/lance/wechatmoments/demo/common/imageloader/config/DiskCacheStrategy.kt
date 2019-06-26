package com.lance.wechatmoments.demo.common.imageloader.config

/**
 * @author lindan
 * 磁盘缓存策略
 */
enum class DiskCacheStrategy(val value: Int) {
    /**
     * 缓存所有
     */
    ALL(1),

    /**
     * 智能自主决定
     */
    AUTOMATIC(2),

    /**
     * 不缓存
     */
    NONE(3),

    /**
     * 只缓存原始数据
     */
    RESOURCE(4),
}
