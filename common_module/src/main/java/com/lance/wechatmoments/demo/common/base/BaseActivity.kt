package com.lance.wechatmoments.demo.common.base

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lance.wechatmoments.demo.common.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * @author lindan
 * 本项目中的Activity基类
 */
abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initIntentData()
        initBaseViewModel()
        onInitViewModel()
        setContentView(getLayoutResId())
        val contentFrameLayout = findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val parentView = contentFrameLayout.getChildAt(0)
        if (parentView != null) {
            parentView.fitsSystemWindows = true
        }
        initViews()
        loadData()
    }

    protected open fun initBaseViewModel() {
        val genericSuperClass = javaClass.genericSuperclass as ParameterizedType
        val type = genericSuperClass.actualTypeArguments[0]
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProviders.of(this)[type as Class<VM>]
        initThrowableLiveDataObserver()
    }

    protected open fun initThrowableLiveDataObserver() {
        viewModel.throwableLiveData.observe(this, Observer { it ->
            it?.message?.let {
                error(it)
            }
        })
    }

    /**
     * 获取Intent中数据
     */
    protected abstract fun initIntentData()

    /**
     * 获取Layout布局XML
     */
    @LayoutRes
    protected abstract fun getLayoutResId(): Int

    /**
     * 初始化自定义ViewModel
     */
    protected open fun onInitViewModel() {}

    /**
     * 初始化视图View
     */
    protected abstract fun initViews()

    /**
     * 加载数据
     */
    protected abstract fun loadData()

    /**
     * 默认的出错提示 toast形式
     */
    protected open fun error(errorMsg : String) {

    }
}