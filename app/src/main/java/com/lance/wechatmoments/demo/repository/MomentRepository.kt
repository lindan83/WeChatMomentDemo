package com.lance.wechatmoments.demo.repository

import com.lance.wechatmoments.demo.models.Tweet
import com.lance.wechatmoments.demo.models.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author lindan
 * 数据访问接口
 */
interface MomentRepository {
    /**
     * 获取指定用户个人资料，默认为Jsmith
     */
    @GET("/user/{username}")
    fun getUserProfile(@Path("username") username: String = "jsmith"): Observable<User>

    /**
     * 获取指定用户的朋友圈评论列表，默认为Jsmith
     */
    @GET("/user/{username}/tweets")
    fun getUserTweets(@Path("username") username: String = "jsmith"): Observable<List<Tweet>>
}