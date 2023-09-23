package com.sjk.yoram.model.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.sjk.yoram.R
import com.sjk.yoram.model.dto.BoardMedia

@SuppressLint("ViewConstructor")
class BoardMediaYoutubeView: ConstraintLayout {
    private val media: BoardMedia.Youtube
    private val lifecycle: Lifecycle
    private val maxHeight: Int

    private lateinit var lottieView: LottieAnimationView
    private lateinit var playerView: YouTubePlayerView

    constructor(media: BoardMedia.Youtube, lifecycle: Lifecycle, context: Context): super(context) {
        this.media = media
        this.lifecycle = lifecycle
        this.maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()

        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }

        this.lottieView = getLottieView()
        this.playerView = getYoutubeView()


        this.addView(playerView)
        this.addView(lottieView)
    }
    constructor(media: BoardMedia.Youtube, lifecycle: Lifecycle, context: Context, attrs: AttributeSet?): super(context, attrs) {
        this.media = media
        this.lifecycle = lifecycle
        this.maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()

        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }


        this.addView(playerView)
        this.addView(lottieView)
    }
    constructor(media: BoardMedia.Youtube, lifecycle: Lifecycle, context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.media = media
        this.lifecycle = lifecycle
        this.maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()

        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }

        this.lottieView = getLottieView()
        this.playerView = getYoutubeView()

        this.addView(playerView)
        this.addView(lottieView)
    }

    private fun getYoutubeView(): YouTubePlayerView {
        val playerView = YouTubePlayerView(context).apply {
            this.enableAutomaticInitialization = false
            this.enableBackgroundPlayback(false)

            val iFramePlayerOptions = IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(0)
                .build()

            this.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val startIndex = media.url.indexOf("?start=")
                    val start = if (startIndex != -1)
                        media.url.substring(startIndex + 7,
                            if (media.url.indexOf("?", startIndex + 7) == -1) media.url.lastIndex + 1
                        else
                            media.url.indexOf("?", startIndex + 7)
                        ).toFloat()
                    else 0f
                    youTubePlayer.loadVideo(videoId = media.id, startSeconds = start)
                    youTubePlayer.pause()
                    setYoutubeVisible()
                }
            }, iFramePlayerOptions)


        }

        return playerView
    }

    private fun getLottieView(): LottieAnimationView {
        val lottieView = LottieAnimationView(context).apply {
            this.maxHeight = this@BoardMediaYoutubeView.maxHeight
            this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.topToTop = LayoutParams.PARENT_ID
                this.leftToLeft = LayoutParams.PARENT_ID
                this.rightToRight = LayoutParams.PARENT_ID
                this.bottomToBottom = LayoutParams.PARENT_ID
                this.setMargins(32, 32, 32, 32)
            }
            this.repeatMode = LottieDrawable.RESTART
            this.repeatCount = LottieDrawable.INFINITE
            this.setAnimation(R.raw.youtube_loading)
            this.enableMergePathsForKitKatAndAbove(true)
            this.playAnimation()
        }

        return lottieView
    }

    private fun setYoutubeVisible() {
        this.playerView.visibility = View.VISIBLE
        this.lottieView.visibility = View.GONE
    }

    private fun setLottieVisible() {
        this.playerView.visibility = View.INVISIBLE
        this.lottieView.visibility = View.VISIBLE
    }

    fun releaseYoutube() {
        playerView.release()
    }

}