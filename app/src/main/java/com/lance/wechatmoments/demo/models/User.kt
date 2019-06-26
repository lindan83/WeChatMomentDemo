package com.lance.wechatmoments.demo.models

import com.google.gson.annotations.SerializedName

/**
 * @author lindan
 * 用户信息Model
 *
{
profile-image: "http://img2.findthebest.com/sites/default/files/688/media/images/Mingle_159902_i0.png",
avatar: "http://info.thoughtworks.com/rs/thoughtworks2/images/glyph_badge.png",
nick: "John Smith",
username: "jsmith"
}
 */
class User {
    /**
     * 封面图
     */
    @SerializedName("profile-image")
    val profileImage: String? = null

    /**
     * 用户头像
     */
    @SerializedName("avatar")
    val avatar: String? = null

    /**
     * 用户昵称
     */
    @SerializedName("nick")
    val nick: String? = null

    /**
     * 用户名称
     */
    @SerializedName("username")
    val username: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (profileImage != other.profileImage) return false
        if (avatar != other.avatar) return false
        if (nick != other.nick) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = profileImage?.hashCode() ?: 0
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + (nick?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        return result
    }


}
