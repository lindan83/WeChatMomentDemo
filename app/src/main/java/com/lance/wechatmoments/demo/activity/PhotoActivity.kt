package com.lance.wechatmoments.demo.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.lance.wechatmoments.demo.R
import com.lance.wechatmoments.demo.common.dp2px
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.Serializable
import java.util.*

/**
 * @author lindan
 * 查看大图
 */
class PhotoActivity : AppCompatActivity() {

    private val guideViewList = ArrayList<View>()
    private var imageSize: ImageSize? = null
    private var startPos: Int = 0
    private var imgUrls: ArrayList<String>? = null
    private var currentPos: Int = 0

    /**
     * Intent参数构建器
     */
    class IntentParameter {
        var context: Context? = null
        var imageUrls: List<String>? = ArrayList()
        var position: Int = 0
        var imageSize: ImageSize? = null
        var bundle: Bundle? = null

        /**
         * 构建打开Activity的Intent
         */
        fun toIntent(): Intent {
            if (context == null) {
                throw IllegalArgumentException("context cannot be null")
            }
            if (imageUrls == null || imageUrls!!.isEmpty()) {
                throw IllegalArgumentException("image list cannot be null or empty")
            }
            val intent = Intent(context, PhotoActivity::class.java)
            intent.putStringArrayListExtra(INTENT_IMAGE_URLS, ArrayList(imageUrls!!))
            intent.putExtra(INTENT_POSITION, position)
            intent.putExtra(INTENT_IMAGE_SIZE, imageSize)
            if (bundle != null) {
                intent.putExtra(INTENT_BUNDLE, bundle)
            }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        initIntentData()
        initViews()
    }

    private fun initIntentData() {
        val intent = intent
        if (intent != null) {
            startPos = intent.getIntExtra(INTENT_POSITION, 0)
            currentPos = startPos
            imgUrls = intent.getStringArrayListExtra(INTENT_IMAGE_URLS)
            imageSize = intent.getSerializableExtra(INTENT_IMAGE_SIZE) as ImageSize
        }
    }

    private fun initViews() {
        pbLoading.visibility = View.GONE
        val adapter = ImageAdapter(this)
        adapter.setData(imgUrls)
        imageSize?.let { adapter.setImageSize(it) }
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                currentPos = position
                for (i in guideViewList.indices) {
                    guideViewList[i].isSelected = i == position
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        viewPager.currentItem = startPos

        addGuideView(guideGroup, startPos, imgUrls)
    }

    private fun addGuideView(guideGroup: LinearLayout, startPos: Int, imgUrls: ArrayList<String>?) {
        if (!imgUrls.isNullOrEmpty()) {
            guideViewList.clear()
            val size = dp2px(6F).toInt()
            for (i in imgUrls.indices) {
                val view = View(this)
                view.setBackgroundResource(R.drawable.selector_guide_bg)
                view.isSelected = i == startPos

                val layoutParams = LinearLayout.LayoutParams(size, size)
                layoutParams.setMargins(10, 0, 0, 0)
                guideGroup.addView(view, layoutParams)
                guideViewList.add(view)
            }
        }
    }

    private inner class ImageAdapter internal constructor(private val context: Context) : PagerAdapter() {
        private var data: List<String>? = ArrayList()
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var imageSize: ImageSize? = null
        private var smallImageView: ImageView? = null

        fun setData(data: List<String>?) {
            if (data != null) {
                this.data = data
            }
        }

        internal fun setImageSize(imageSize: ImageSize) {
            this.imageSize = imageSize
        }

        override fun getCount(): Int {
            return if (data == null) {
                0
            } else data!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val frameLayout = inflater.inflate(R.layout.item_photo, container, false)
            val imageView = frameLayout.findViewById<PhotoView>(R.id.image)
            //设置共享元素transactionName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.transitionName = getString(R.string.transition_image_detail)
            }
            imageView.setOnPhotoTapListener { _, _, _ -> (context as Activity).finish() }

            if (imageSize != null) {
                //预览imageView
                smallImageView = ImageView(context)
                val layoutParams = FrameLayout.LayoutParams(imageSize!!.width, imageSize!!.height)
                layoutParams.gravity = Gravity.CENTER
                smallImageView!!.layoutParams = layoutParams
                smallImageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
                (frameLayout as FrameLayout).addView(smallImageView)
            }

            //loading
            val loading = ProgressBar(context)
            val loadingLayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            loadingLayoutParams.gravity = Gravity.CENTER
            loading.layoutParams = loadingLayoutParams
            (frameLayout as FrameLayout).addView(loading)

            val imgUrl = data!![position]
            loading.visibility = View.VISIBLE
            val requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(R.mipmap.icon_placeholder)
                .error(R.mipmap.icon_error)
                .priority(Priority.NORMAL)
            Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .load(imgUrl)
                .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        loading.visibility = View.GONE
                        frameLayout.removeView(loading)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        loading.visibility = View.GONE
                        frameLayout.removeView(loading)
                        return false
                    }
                })
                .into(imageView)

            container.addView(frameLayout, 0)
            return frameLayout
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

        override fun saveState(): Parcelable? {
            return null
        }
    }

    class ImageSize(var width: Int, var height: Int) : Serializable

    companion object {
        private const val TAG = "PhotoActivity"
        const val INTENT_IMAGE_URLS = "imgurls"
        const val INTENT_POSITION = "position"
        const val INTENT_IMAGE_SIZE = "imagesize"
        const val INTENT_BUNDLE = "bundle"

        fun startImagePagerActivity(builder: IntentParameter) {
            ActivityCompat.startActivity(builder.context!!, builder.toIntent(), builder.bundle)
        }
    }
}
