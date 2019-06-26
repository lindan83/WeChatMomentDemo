package com.lance.wechatmoments.demo.common.imageloader

/**
 * @author lindan
 * 图片加载框架的入口单例
 */
object ImageLoader {

    /**
     * 获取图片加载器
     */
    private var loader: ILoader? = null

    /**
     * 初始化
     */
    fun init(imageLoader: ILoader): ImageLoader {
        this.loader = imageLoader
        return this
    }

    private var instance: ImageLoader? = null


    /**
     * 获取图片加载器，是getInstance().getLoader()的快捷方式
     */
    fun get(): ILoader {
        if (loader == null) {
            throw IllegalArgumentException("You must call init method first!!")
        }
        return loader!!
    }
}
