package com.lance.wechatmoments.demo.common.base.viewmodel

/**
 * @author lindan
 * 响应类型
 */
enum class ResponseState {
    TYPE_REFRESHING,//正在刷新
    TYPE_REFRESHING_SUCCESS,//刷新成功，有数据
    TYPE_EMPTY,//刷新完成，无数据
    TYPE_LOADING,//正在加载更多
    TYPE_LOADING_SUCCESS,//加载更多成功，有数据
    TYPE_NO_MORE_AND_GONE_VIEW,//没有更多数据，且隐藏FootView
    TYPE_NO_MORE,//没有更多数据，不隐藏FootView
    TYPE_ERROR,//出现错误
    TYPE_NET_ERROR,//出现网络错误
}