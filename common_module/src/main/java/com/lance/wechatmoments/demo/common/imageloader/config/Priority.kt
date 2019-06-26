package com.lance.wechatmoments.demo.common.imageloader.config

/**
 * @author lindan
 * 优先级
 */
enum class Priority(val value: Int) {
    /**
     * 立即，最高优先级
     */
    IMMEDIATE(1),

    /**
     * 高优先级
     */
    HIGH(2),

    /**
     * 一般优先级
     */
    NORMAL(3),

    /**
     * 低优先级
     */
    LOW(4),
}
