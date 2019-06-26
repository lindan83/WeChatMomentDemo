package com.lance.wechatmoments.demo.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * @author lindan
 * 剪贴板工具
 */
object ClipBoardUtils {
    /**
     * 实现文本复制功能
     *
     * @param context Context
     * @param input   要复制的文本
     */
    fun copy(context: Context, input: String) {
        val cmb = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(null, input)
        if (cmb != null) {
            cmb.primaryClip = clip
        }
    }

    /**
     * 实现粘贴功能
     *
     * @param context Context
     * @return 文本
     */
    fun paste(context: Context): String? {
        val cmb = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (cmb != null && cmb.hasPrimaryClip()) {
            val clipData = cmb.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                return clipData.getItemAt(0).text.toString()
            }
        }
        return null
    }
}
