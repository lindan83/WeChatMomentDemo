package com.lance.wechatmoments.demo.common.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.lance.wechatmoments.demo.common.imageloader.config.ImageLoaderConfig;
import com.lance.wechatmoments.demo.common.imageloader.listener.ImageRequestListener;

import java.io.File;

/**
 * @author lindan
 * 抽象图片加载接口
 */
public interface ILoader {
    /**
     * 自定义RequestOptions加载图片
     */
    void loadUrl(@NonNull ImageView imageView, @NonNull String url, @NonNull ImageLoaderConfig config);

    /**
     * 按默认配置加载图片
     */
    void loadUrl(@NonNull ImageView imageView, @NonNull String url);

    /**
     * 监听加载图片的过程
     */
    void loadUrlWithListener(@NonNull ImageView imageView,
                             String url,
                             @NonNull ImageLoaderConfig config,
                             ImageRequestListener<Drawable> requestListener);

    /**
     * 加载圆形图
     */
    void loadCircleImage(@NonNull ImageView imageView, @NonNull String url);

    /**
     * 加载带边框圆形图
     */
    void loadCircleImage(@NonNull ImageView imageView, @NonNull String url, @Px int borderWidth, @ColorInt int borderColor);

    /**
     * 加载圆角图片
     *
     * @param radius 圆角半径
     */
    void loadRoundRectImage(@NonNull ImageView imageView, @NonNull String url, @Px int radius);

//    /**
//     * 加载带边框圆角图片
//     *
//     * @param radius 圆角半径
//     */
//    void loadRoundRectImage(@NonNull ImageView imageView, @NonNull String url, @Px int radius, @Px int borderWidth, @ColorInt int borderColor);

    /**
     * 加载本地资源图片
     */
    void loadDrawable(@NonNull ImageView imageView, @DrawableRes int drawableId, @NonNull ImageLoaderConfig config);

    /**
     * 加载本地资源图片
     */
    void loadDrawable(@NonNull ImageView imageView, @DrawableRes int drawableId);

    /**
     * 加载指定Uri资源
     */
    void loadUri(@NonNull ImageView imageView, @NonNull Uri uri, @NonNull ImageLoaderConfig config);

    /**
     * 加载指定Uri资源
     */
    void loadUri(@NonNull ImageView imageView, @NonNull Uri uri);

    /**
     * 加载文件
     */
    void loadFile(@NonNull ImageView imageView, @NonNull File file);

    /**
     * 加载指定Uri资源
     */
    void loadFile(@NonNull ImageView imageView, @NonNull File file, @NonNull ImageLoaderConfig config);

    /**
     * 清除磁盘缓存
     */
    void clearDiskCache(Context context);

    /**
     * 清除内存缓存
     */
    void clearMemoryCache(Context context);

    /**
     * 恢复任务
     *
     * @param recursive 是否递归
     */
    void resumeRequest(Context context, boolean recursive);

    /**
     * 暂停任务
     *
     * @param recursive 是否递归
     */
    void pauseRequest(Context context, boolean recursive);

    /**
     * 获取图片加载配置选项
     * @return 配置
     */
    ImageLoaderConfig getConfig();
}
