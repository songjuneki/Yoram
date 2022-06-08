package com.sjk.yoram.Model.Adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.sjk.yoram.Model.*
import com.sjk.yoram.R
import com.sjk.yoram.databinding.CardMainBannerBinding
import com.sjk.yoram.databinding.CardMainDptmentBinding
import com.sjk.yoram.databinding.CardMainIdBinding

class CardAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var cards = mutableListOf<Card>()
    private var dptItemClickListener: OnDptItemClickListener? = null
    private var dptSearchBarClickListener: OnSearchBarClickListener? = null
    var rootDptsData = mutableListOf<SimpleDpt>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            CardType.HOME_BANNER.ordinal -> {
                //val binding = DataBindingUtil.inflate(inflater, R.layout.card_main_banner, parent, false)
                //val view = inflater.inflate(R.layout.card_main_banner, parent, false)
                val binding: CardMainBannerBinding = DataBindingUtil.inflate(inflater, R.layout.card_main_banner, parent, false)
                HomeBannerCardViewHolder(binding)
            }
            CardType.HOME_DEPARTMENT.ordinal -> {
                val binding: CardMainDptmentBinding = DataBindingUtil.inflate(inflater, R.layout.card_main_dptment, parent, false)
                HomeDptCardViewHolder(binding)
            }
            CardType.HOME_ID.ordinal -> {
                val binding: CardMainIdBinding = DataBindingUtil.inflate(inflater, R.layout.card_main_id, parent, false)
                HomeIDCardViewHolder(binding)
            }
            else -> throw IllegalArgumentException("not found card type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(cards[position].type)
        {
            CardType.HOME_BANNER -> {
                //(holder as HomeBannerCardViewHolder).bind(cards[position].data!! as BannerData)
                (holder as HomeBannerCardViewHolder).bind()
                holder.setIsRecyclable(false)
            }
            CardType.HOME_DEPARTMENT -> {
                (holder as HomeDptCardViewHolder).bind()
                holder.setIsRecyclable(false)
            }
            CardType.HOME_ID -> {
                (holder as HomeIDCardViewHolder).bind(cards[position].data as userData)
                holder.setIsRecyclable(false)
            }
            else -> throw IllegalArgumentException("not found card type")

        }
    }

    override fun getItemCount(): Int = cards.size

    override fun getItemViewType(position: Int): Int {
        return getCardType(position).ordinal
    }

    fun getCardType(position: Int): CardType {
        return cards[position].type
    }

    inner class HomeBannerCardViewHolder(val binding: CardMainBannerBinding): RecyclerView.ViewHolder(binding.root) {
        val pager = binding.cardMainBannerPager
        val indicator = binding.cardMainBannerIndicator
        fun bind() {
            val adapter = HomeBannerAdapter()

//            val imgUrlList = mutableListOf<String>("https://github.com/bumptech/glide/raw/master/static/glide_logo.png", "http://guseong.org/wp-content/uploads/2021/04/111.jpg", "http://guseong.org/wp-content/uploads/2021/04/111.jpg")
//            val urlList = mutableListOf<String>("111", "222", "333")
            val banners = mutableListOf<BannerData>()
//            banners.add(BannerData(imgUrlList[0], urlList[0]))
//            banners.add(BannerData(imgUrlList[1], urlList[1]))
//            banners.add(BannerData(imgUrlList[2], urlList[2]))
            banners.add(BannerData(R.drawable.banner, "222"))
            banners.add(BannerData("https://velog.velcdn.com/images/jojo_devstory/post/08aa394d-f319-4b4b-bf32-89106daa9ff1/Coil.jpeg", "333"))
            banners.add(BannerData("http://guseong.org/wp-content/uploads/2021/04/111.jpg", "444"))

            pager.adapter = adapter
            indicator.setViewPager2(pager)

            adapter.fetchBanner(banners)
        }
    }

    inner class HomeDptCardViewHolder(val binding: CardMainDptmentBinding): RecyclerView.ViewHolder(binding.root) {
        val searchBar = binding.cardMainDptmentSearchbarCard
        val searchBarEt = binding.cardMainDptmentSearchbarEt
        val searchBarIcon = binding.cardMainDptmentSearchbarIcon
        val dptRecycler = binding.cardMainDptmentRecycler
        @SuppressLint("ClickableViewAccessibility")
        fun bind() {
            searchBar.setOnTouchListener { _, _ -> dptSearchBarClickListener!!.onSearchBarClick(); return@setOnTouchListener false }
            searchBarEt.setOnTouchListener { _, _ -> dptSearchBarClickListener!!.onSearchBarClick(); return@setOnTouchListener true }
            searchBarIcon.setOnTouchListener { _, _ -> dptSearchBarClickListener!!.onSearchBarClick(); return@setOnTouchListener false }

            val layoutManager = LinearLayoutManager(binding.root.context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL

            dptRecycler.layoutManager = layoutManager

            val adapter = HomeDptRecycleAdapter()

            adapter.itemListener = dptItemClickListener

            val dptButtonData = mutableListOf<DptButton>()
            this@CardAdapter.rootDptsData.forEach { dptButtonData.add(DptButton(DptButtonType.DEPARTMENT, it.name, it.code)) }
            dptButtonData.add(DptButton(DptButtonType.POSITION, "직급1", null))
            dptButtonData.add(DptButton(DptButtonType.POSITION, "직급2", null))
            dptButtonData.add(DptButton(DptButtonType.POSITION, "직급3", null))
            dptButtonData.add(DptButton(DptButtonType.NAME, "", null))


            dptRecycler.adapter = adapter


            dptRecycler.addItemDecoration(object: RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if (parent.getChildAdapterPosition(view) != (parent.adapter!!.itemCount-1))
                        outRect.right = 30
                }
            })

            adapter.fetchData(dptButtonData)
        }
    }

    inner class HomeIDCardViewHolder(val binding: CardMainIdBinding): RecyclerView.ViewHolder(binding.root) {
        val avatarView = binding.cardMainIdAvatar
        val dptText = binding.cardMainIdDptTv
        val userNameText = binding.cardMainIdNameTv
        fun bind(data: cardData) {
            val userdata = data as userData

            if (userdata.user.avatar.isNullOrEmpty()) {
                avatarView.load("http://3.39.51.49:8080/api/user/avatar?id=0") {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            } else {
                avatarView.load("http://3.39.51.49:8080/api/user/avatar?id=${userdata.user.id}") {     //userdata.user.avatar
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }
            dptText.text = userdata.user.dptname
            userNameText.text = userdata.user.fname + userdata.user.lname
        }
    }

    fun fetchCard(cards: MutableList<Card>) {
        this.cards = cards
        notifyDataSetChanged()
    }

    fun fetchDpts(dpts: MutableList<SimpleDpt>) {
        this.rootDptsData = dpts
        notifyDataSetChanged()
    }

    interface OnDptItemClickListener {
        fun onItemClick(type: DptButtonType, dptCode: Int)
    }

    fun setOnDptItemClickListener(listener: OnDptItemClickListener) {
        this.dptItemClickListener = listener
    }

    interface OnSearchBarClickListener {
        fun onSearchBarClick()
    }
    fun setOnSearchBarClickListener(listener: OnSearchBarClickListener) {
        this.dptSearchBarClickListener = listener
    }

}
