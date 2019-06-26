package com.lance.wechatmoments.demo.util

import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import com.lance.wechatmoments.demo.widget.SpannableClickable
import java.util.regex.Pattern

/**
 * Created by lindan
 * 2019-06-25
 * Url正则工具
 */
object UrlUtils {
    /**
     * 移动号码正则表达式，仅简单处理
     */
    private const val MOBILE_PATTERN = "[1]\\d{10}"

    /**
     * 格式化包含URL的内容
     * @param contentStr 要格式化的内容
     * @return 转换之后的Span
     */
    fun formatUrlString(contentStr: String): SpannableStringBuilder {
        val sp: SpannableStringBuilder
        if (!TextUtils.isEmpty(contentStr)) {

            sp = SpannableStringBuilder(contentStr)
            try {
                //处理url匹配
                val urlPattern =
                    Pattern.compile("(http|https|ftp|svn)://([a-zA-Z0-9]+[/?.?])" + "+[a-zA-Z0-9]*\\??([a-zA-Z0-9]*=[a-zA-Z0-9]*&?)*")
                val urlMatcher = urlPattern.matcher(contentStr)

                while (urlMatcher.find()) {
                    val url = urlMatcher.group()
                    if (!TextUtils.isEmpty(url)) {
                        sp.setSpan(object : SpannableClickable() {
                            override fun onClick(widget: View) {
                                val uri = Uri.parse(url)
                                val context = widget.context
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
                                context.startActivity(intent)
                            }
                        }, urlMatcher.start(), urlMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }

                //处理电话匹配
                val phonePattern = Pattern.compile(MOBILE_PATTERN)
                val phoneMatcher = phonePattern.matcher(contentStr)
                while (phoneMatcher.find()) {
                    val phone = phoneMatcher.group()
                    if (!TextUtils.isEmpty(phone)) {
                        sp.setSpan(object : SpannableClickable() {
                            override fun onClick(widget: View) {
                                val context = widget.context
                                //用intent启动拨打电话
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            }
                        }, phoneMatcher.start(), phoneMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            sp = SpannableStringBuilder()
        }
        return sp
    }
}
