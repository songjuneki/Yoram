package com.sjk.yoram.viewmodel

import android.app.Application
import android.content.DialogInterface
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.MutableListLiveData
import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.ui.adapter.AdminBannerListAdapter
import com.sjk.yoram.model.ui.decorator.AdminBannerTouchHelper
import com.sjk.yoram.model.ui.listener.AdminBannerClickListener
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AdminBannerViewModel(private val serverRepository: ServerRepository, private val userRepository: UserRepository): ViewModel() {
    var immutableBanners: List<Banner> = listOf()
    val banners = MutableListLiveData<Banner>()

    val editBanner = MutableLiveBannerData()

    private val _editCancelEvent = MutableLiveData<Event<Unit>>()
    val editCancelEvent: LiveData<Event<Unit>>
        get() = _editCancelEvent

    private val _editDoneEvent = MutableLiveData<Event<Unit>>()
    val editDoneEvent: LiveData<Event<Unit>>
        get() = _editDoneEvent

    private val _editDeleteEvent = MutableLiveData<Event<DialogInterface.OnClickListener>>()
    val editDeleteEvent: LiveData<Event<DialogInterface.OnClickListener>>
        get() = _editDeleteEvent

    private val _uploadDoneEvent = MutableLiveData<Event<Unit>>()
    val uploadDoneEvent: LiveData<Event<Unit>>
        get() = _uploadDoneEvent

    private val _uploadFailEvent = MutableLiveData<Event<Unit>>()
    val uploadFailEvent: LiveData<Event<Unit>>
        get() = _uploadFailEvent

    private val _uploadNewBannerEvent = MutableLiveData<Event<Unit>>()
    val uploadNewBannerEvent: LiveData<Event<Unit>>
        get() = _uploadNewBannerEvent

    val bannerDetailEvent = MutableLiveData<Event<Unit>>()

    private var bannerClick: AdminBannerClickListener = object: AdminBannerClickListener {
        override fun onClick(item: Banner) {
            editBanner.value = item
            bannerDetailEvent.value = Event(Unit)
        }
    }
    val adapter: AdminBannerListAdapter = AdminBannerListAdapter(bannerClick).apply {
        this.setDragListener(object: AdminBannerListAdapter.ItemDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                helper.startDrag(viewHolder)
            }
        })
    }
    private val itemTouchHelper: AdminBannerTouchHelper = AdminBannerTouchHelper(adapter)
    val helper: ItemTouchHelper = ItemTouchHelper(itemTouchHelper)

    init {
        viewModelScope.launch {
            loadBanners()
        }
    }

    fun loadBanners() {
        viewModelScope.launch {
            immutableBanners = serverRepository.getAllBanners(true)
            banners.value = serverRepository.getAllBanners(true).toMutableList()
        }
    }

    fun btnEvent(btnId: Int) {
        when(btnId) {
            R.id.dialog_my_admin_banner_edit_footer_apply -> bannerEditDone()
            R.id.dialog_my_admin_banner_edit_footer_cancel -> _editCancelEvent.value = Event(Unit)
            R.id.dialog_my_admin_banner_edit_delete -> bannerDelete()
            R.id.frag_my_pref_admin_banner_done -> uploadBanners()
            R.id.frag_my_pref_admin_banner_header_add -> _uploadNewBannerEvent.value = Event(Unit)
        }
    }

    fun changedValueApply() {
        uploadBanners()
    }

    fun uploadNewBanner(img: Bitmap?) {
        viewModelScope.launch {
            val upload = serverRepository.uploadNewBanner(img, userRepository.getLoginID())
            if (upload) {
                banners.value?.add(serverRepository.getAllBanners(true).last())
                editBanner.value = banners.value!!.last()
                bannerDetailEvent.value = Event(Unit)
            }
        }
    }

    fun bannerIsChanged(): Boolean {
        return !banners.isListEquals(immutableBanners)
    }

    private fun bannerEditDone() {
        setBannerById(editBanner.getId(), editBanner.value!!)
        this._editCancelEvent.value = Event(Unit)
        this._editDoneEvent.value = Event(Unit)
    }

    private fun bannerDelete() {
        val listener = DialogInterface.OnClickListener { _, _ ->
            viewModelScope.launch {
                banners.value?.let {
                    serverRepository.deleteBanner(editBanner.value!!)
//                    adapter.deleteItem(editBanner.value!!)
                    it.removeAt(getBannerPositionById(editBanner.value!!.id))
                    adapter.notifyDataSetChanged()
                }
            }
        }
        _editDeleteEvent.value = Event(listener)
        _editCancelEvent.value = Event(Unit)
        _editDoneEvent.value = Event(Unit)
    }

    private fun uploadBanners() {
        viewModelScope.launch {
            val res = serverRepository.uploadBanners(banners.toList())
            if (res) _uploadDoneEvent.value = Event(Unit)
            else _uploadFailEvent.value = Event(Unit)
        }
    }

    private fun setBannerById(id: Int, banner: Banner) {
        banners.value?.let {
            for (i in 0 until it.size) {
                if (it[i].id == id) {
                    it[i] = banner
                    return@let
                }
            }
        }
    }

    private fun getBannerPositionById(id: Int): Int {
        banners.value?.let { list ->
            for (i in 0 until list.size) {
                if (list[i].id == id)
                    return i
            }
        }
        return -1
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminBannerViewModel(ServerRepository.getInstance(application)!!, UserRepository.getInstance(application)!!) as T
        }
    }


    inner class MutableLiveBannerData: MutableLiveData<Banner>() {
        init {
            value = null
        }

        fun getId(): Int {
            if (value == null) return -1
            return value!!.id
        }

        fun modifyTitle(title: String) {
            if (value == null)
                return
            val current = value!!
            current.title = title
            value = current
        }

        fun modifyLink(link: String) {
            if (value == null) return
            val current = value!!
            current.link = link
            value = current
        }

        fun modifyExpire(expire: String) {
            if (value == null) return
            val current = value!!
            current.expire = expire
            value = current
        }

        fun modifyShow() {
            if (value == null) return
            val current = value!!
            current.show = !current.show
            value = current
        }

        fun notifyChange() {
            val current = value
            value = current
        }
    }
}