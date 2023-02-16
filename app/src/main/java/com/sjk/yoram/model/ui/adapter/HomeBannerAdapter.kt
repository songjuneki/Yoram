package com.sjk.yoram.model.ui.adapter

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Wave
import com.sjk.yoram.repository.retrofit.MyRetrofit
import com.sjk.yoram.model.dto.Banner

class HomeBannerAdapter(): RecyclerView.Adapter<HomeBannerAdapter.BannerHolder>() {
    var data = mutableListOf<Banner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerHolder {
        //var inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val layout = LinearLayout(parent.context)
        val frame = FrameLayout(parent.context)

        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)
        params.gravity = Gravity.CENTER_VERTICAL
        frame.layoutParams = params

        layout.orientation = LinearLayout.HORIZONTAL
        layout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layout.gravity = Gravity.CENTER

        return BannerHolder(frame)
    }

    override fun onBindViewHolder(holder: BannerHolder, position: Int) {
        holder.bind(data[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = data.size

    fun fetchBanner(data: MutableList<Banner>) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class BannerHolder(val layout: FrameLayout): RecyclerView.ViewHolder(layout) {
        fun bind(data: Banner) {
            // http://guseong.org/wp-content/uploads/2021/04/111.jpg
            // data.imgUrl : 이미지 주소
            // data.url : 클릭시 이동하는 주소
            val img = ImageView(layout.context)
            img.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)
            layout.addView(img)

            val progressbar = SpinKitView(layout.context)
            progressbar.setIndeterminateDrawable(Wave())
            progressbar.setColor(android.R.attr.colorPrimary)
            progressbar.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
            layout.addView(progressbar)

            val url = "${MyRetrofit.SERVER}banner?id=${data.id}"

            img.load(url) {
                crossfade(true)
                listener(
                    onStart = {
                        img.visibility = View.INVISIBLE
                        progressbar.visibility = View.VISIBLE },
                    onSuccess = { _, _ ->
                        progressbar.visibility = View.GONE
                        img.visibility = View.VISIBLE }
                )
            }

            img.setOnClickListener {
                Toast.makeText(layout.context, data.link, Toast.LENGTH_SHORT).show()
            }

        }
    }

}