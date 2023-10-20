package com.sjk.yoram.presentation.main.board

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Scale
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.sjk.yoram.R

@SuppressLint("ViewConstructor")
class BoardMediaImageView: ConstraintLayout {
    private val url: String
    private val maxHeight: Int

    private lateinit var lottieView: LottieAnimationView
    private lateinit var imageView: AppCompatImageView

    constructor(url: String, context: Context): super(context) {
        this.url = url
        this.maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()

        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }

//        this.lottieView = getLottieView()
        this.imageView = getImageView()


        this.addView(imageView)
//        this.addView(lottieView)
    }
    constructor(url: String, context: Context, attrs: AttributeSet?): super(context, attrs) {
        this.url = url
        this.maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()

        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }

        this.lottieView = getLottieView()
        this.imageView = getImageView()


        this.addView(imageView)
        this.addView(lottieView)
    }
    constructor(url: String, context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.url = url
        this.maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()

        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }

        this.lottieView = getLottieView()
        this.imageView = getImageView()


        this.addView(imageView)
        this.addView(lottieView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getImageView(): AppCompatImageView {
        val imageView = AppCompatImageView(context)

        val loader = ImageLoader.Builder(context)
            .build()

        val request = ImageRequest.Builder(context)
            .data(this.url)
            .placeholderMemoryCacheKey(url.substringAfter("guseong.org/"))
            .scale(Scale.FIT)
            .error(context.getDrawable(R.drawable.baseline_error_outline_24))
            .listener(onStart = {
                Log.d("JKJK", "image load start")
//                this@BoardMediaImageView.setLottieVisible()
            }, onSuccess = { _, _ ->
                Log.d("JKJK", "image load success")
//                this@BoardMediaImageView.setImageVisible()
            }, onError = { request, error ->
                Log.d("JKJK", "image load error, ${error.throwable}")
//                this@BoardMediaImageView.setImageVisible()
            })
            .target(imageView)
            .build()

        loader.enqueue(request)

        return imageView.apply {
            this.maxHeight = this@BoardMediaImageView.maxHeight
            this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.topToTop = LayoutParams.PARENT_ID
                this.leftToLeft = LayoutParams.PARENT_ID
                this.rightToRight = LayoutParams.PARENT_ID
                this.bottomToBottom = LayoutParams.PARENT_ID
            }
        }
    }

    private fun getLottieView(): LottieAnimationView {
        val lottieView = LottieAnimationView(context).apply {
            this.maxHeight = this@BoardMediaImageView.maxHeight
            this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.topToTop = LayoutParams.PARENT_ID
                this.leftToLeft = LayoutParams.PARENT_ID
                this.rightToRight = LayoutParams.PARENT_ID
                this.bottomToBottom = LayoutParams.PARENT_ID
            }
            this.repeatMode = LottieDrawable.REVERSE
            this.repeatCount = LottieDrawable.INFINITE
            this.setAnimation(R.raw.image_loading)
            this.enableMergePathsForKitKatAndAbove(true)
            this.playAnimation()
        }

        return lottieView
    }

    private fun setImageVisible() {
        this.imageView.visibility = View.VISIBLE
        this.lottieView.visibility = View.GONE
    }

    private fun setLottieVisible() {
        this.imageView.visibility = View.INVISIBLE
        this.lottieView.visibility = View.VISIBLE
    }

}