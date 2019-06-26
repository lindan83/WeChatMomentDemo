package com.lance.wechatmoments.demo.common.imageloader.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.lance.wechatmoments.demo.common.imageloader.ILoader
import com.lance.wechatmoments.demo.common.imageloader.config.ImageLoaderConfig
import com.lance.wechatmoments.demo.common.imageloader.glide.transormations.GlideCircleTransformation
import com.lance.wechatmoments.demo.common.imageloader.listener.ImageRequestListener
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

/**
 * @author lindan
 * Glide图片加载工具
 */
class GlideLoader(private val config: ImageLoaderConfig) : ILoader {
    private val glideConfig: GlideConfig = GlideConfig(config)

    /**
     * 自定义RequestOptions加载图片
     */
    override fun loadUrl(imageView: ImageView, url: String, config: ImageLoaderConfig) {
        loadImage(imageView, url, newGlideConfig(config).buildRequestOptions())
    }

    /**
     * 按默认配置加载图片
     */
    override fun loadUrl(imageView: ImageView, url: String) {
        loadImage(imageView, url, glideConfig.buildRequestOptions())
    }

    /**
     * 监听加载图片的过程
     */
    override fun loadUrlWithListener(imageView: ImageView,
                                     url: String?,
                                     config: ImageLoaderConfig,
                                     requestListener: ImageRequestListener<Drawable>) {
        var urlString = url
        if (urlString == null) {
            urlString = ""
        }
        Glide.with(imageView.context)
                .applyDefaultRequestOptions(newGlideConfig(config).buildRequestOptions())
                .load(urlString)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any,
                                              target: Target<Drawable>,
                                              isFirstResource: Boolean): Boolean {
                        return requestListener.onLoadFailed(e!!)
                    }

                    override fun onResourceReady(resource: Drawable, model: Any,
                                                 target: Target<Drawable>,
                                                 dataSource: DataSource,
                                                 isFirstResource: Boolean): Boolean {
                        return requestListener.onResourceReady(resource)
                    }
                }).into(imageView)
    }

    /**
     * 加载圆形图
     */
    override fun loadCircleImage(imageView: ImageView, url: String) {
        loadImage(imageView, url, glideConfig.buildRequestOptions().circleCrop())
    }

    /**
     * 加载圆形图带边框
     */
    override fun loadCircleImage(imageView: ImageView, url: String,
                                 @Dimension borderWidth: Int, @ColorInt borderColor: Int) {
        Glide.with(imageView).load(url)
                .apply(glideConfig.buildRequestOptions().circleCrop()
                        .transform(GlideCircleTransformation(borderWidth, borderColor)))
                .into(imageView)
    }

    /**
     * 加载圆角图片
     *
     * @param radius 圆角半径
     */
    override fun loadRoundRectImage(imageView: ImageView, url: String, @Px radius: Int) {
        val multi = MultiTransformation(CenterCrop(),
                RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.ALL))
        loadImage(imageView, url, glideConfig.buildRequestOptions().transform(multi))
    }

    //    @Override
    //    public void loadRoundRectImage(@NonNull ImageView imageView, @NonNull String url, @Px int radius, @Px int borderWidth, @ColorInt int borderColor) {
    //        MultiTransformation<Bitmap> multi = new MultiTransformation<>(new CenterCrop(),
    //                new RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.ALL));
    //        loadImage(imageView, url, glideConfig.buildRequestOptions().transform(multi).transform(new GlideRoundedCornerTransformation(borderWidth, borderColor, radius)));
    //    }

    /**
     * 加载本地资源图片
     */
    override fun loadDrawable(imageView: ImageView, @DrawableRes drawableId: Int, config: ImageLoaderConfig) {
        Glide.with(imageView.context)
                .load(drawableId)
                .apply(newGlideConfig(config).buildRequestOptions())
                .into(imageView)
    }

    /**
     * 加载本地资源图片
     */
    override fun loadDrawable(imageView: ImageView, @DrawableRes drawableId: Int) {
        Glide.with(imageView.context)
                .load(drawableId)
                .apply(glideConfig.buildRequestOptions())
                .into(imageView)
    }

    /**
     * 加载指定Uri资源
     */
    override fun loadUri(imageView: ImageView, uri: Uri, config: ImageLoaderConfig) {
        Glide.with(imageView.context)
                .load(uri)
                .apply(newGlideConfig(config).buildRequestOptions())
                .into(imageView)
    }

    /**
     * 加载指定Uri资源
     */
    override fun loadUri(imageView: ImageView, uri: Uri) {
        Glide.with(imageView.context)
                .load(uri)
                .apply(glideConfig.buildRequestOptions())
                .into(imageView)
    }

    /**
     * 加载文件
     */
    override fun loadFile(imageView: ImageView, file: File) {
        Glide.with(imageView.context)
                .load(file)
                .apply(glideConfig.buildRequestOptions())
                .into(imageView)
    }

    /**
     * 加载指定Uri资源
     */
    override fun loadFile(imageView: ImageView, file: File, config: ImageLoaderConfig) {
        Glide.with(imageView.context)
                .load(file)
                .apply(newGlideConfig(config).buildRequestOptions())
                .into(imageView)
    }

    /**
     * 清除磁盘缓存
     */
    override fun clearDiskCache(context: Context) {
        object : Thread() {
            override fun run() {
                super.run()
                Glide.get(context).clearDiskCache()
            }
        }.start()
    }

    /**
     * 清除内存缓存
     */
    override fun clearMemoryCache(context: Context) {
        Glide.get(context).clearMemory()
    }

    /**
     * 恢复任务
     *
     * @param recursive 是否递归
     */
    override fun resumeRequest(context: Context, recursive: Boolean) {
        if (recursive) {
            Glide.with(context).resumeRequestsRecursive()
        } else {
            Glide.with(context).resumeRequests()
        }
    }

    /**
     * 暂停任务
     *
     * @param recursive 是否递归
     */
    override fun pauseRequest(context: Context, recursive: Boolean) {
        if (recursive) {
            Glide.with(context).pauseRequestsRecursive()
        } else {
            Glide.with(context).pauseRequests()
        }
    }

    override fun getConfig(): ImageLoaderConfig {
        return config
    }

    /**
     * 加载指定URL的图片
     */
    private fun loadImage(imageView: ImageView, url: String?, requestOptions: RequestOptions) {
        var urlString = url
        if (urlString == null) {
            urlString = ""
        }
        Glide.with(imageView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(urlString)
                .into(imageView)
    }

    /**
     * 创建GlideConfig
     */
    private fun newGlideConfig(config: ImageLoaderConfig): GlideConfig {
        return GlideConfig(config)
    }
}