package com.lance.wechatmoments.demo.viewmodel

import android.util.Log
import com.lance.wechatmoments.demo.common.BuildConfig
import com.lance.wechatmoments.demo.common.config.AppConfig
import com.lance.wechatmoments.demo.common.retrofit.RetrofitConfig
import com.lance.wechatmoments.demo.common.retrofit.RetrofitManager
import com.lance.wechatmoments.demo.repository.MomentRepository
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * @author lindan
 * 接口数据单元测试
 */
@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 14, manifest = Config.NONE, maxSdk = 26)
class MomentsViewModelTest {
    companion object {
        private const val TAG = "Test"
    }

    @Before
    fun setUp() {
        ShadowLog.stream = System.out
        RetrofitManager.init(RetrofitConfig().apply {
            baseUrl = AppConfig.BASE_URL
            isDebugMode = BuildConfig.DEBUG
            connectionTimeout = 20
            readTimeout = 20
            writeTimeout = 30
            isRetryOnFail = true
        })
        initRxJava2()
    }

    private fun initRxJava2() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler {
            Schedulers.trampoline()
        }
        RxAndroidPlugins.reset()
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    /**
     * 测试获取用户信息
     */
    @Test
    fun loadUserProfile() {
        val momentRepository = RetrofitManager.create(MomentRepository::class.java)
        momentRepository.getUserProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Assert.assertTrue(it != null)
                Assert.assertEquals("John Smith", it.nick)
                Assert.assertEquals("jsmith", it.username)
                Assert.assertEquals("http://info.thoughtworks.com/rs/thoughtworks2/images/glyph_badge.png", it.avatar)
                Assert.assertEquals(
                    "http://img2.findthebest.com/sites/default/files/688/media/images/Mingle_159902_i0.png",
                    it.profileImage
                )
            }, {
                Log.e(TAG, it.message, it)
            })
    }

    /**
     * 测试获取动态数据列表
     */
    @Test
    fun getUserTweets() {
        val momentRepository = RetrofitManager.create(MomentRepository::class.java)
        momentRepository.getUserTweets()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Assert.assertEquals(22, it.size)
                val result = it.filter { item -> item.isCorrect() }
                Assert.assertEquals(17, result.size)
            }, {
                Log.e(TAG, it.message, it)
            })
    }
}