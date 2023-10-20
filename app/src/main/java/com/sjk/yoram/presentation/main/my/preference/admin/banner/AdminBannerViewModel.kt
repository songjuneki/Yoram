package com.sjk.yoram.presentation.main.my.preference.admin.banner

import android.app.Application
import android.content.DialogInterface
import android.graphics.Bitmap
import androidx.lifecycle.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Banner
import com.sjk.yoram.util.Event
import com.sjk.yoram.util.MutableListLiveData
import com.sjk.yoram.data.repository.ServerRepository
import com.sjk.yoram.data.repository.UserRepository
import kotlinx.coroutines.launch

class AdminBannerViewModel(private val serverRepository: ServerRepository, private val userRepository: UserRepository): ViewModel() {
    var immutableBanners: List<Banner> = listOf()
    var banners = MutableListLiveData<Banner>()

    val editBanner = MutableLiveBannerData()

    private val _isChanged = MutableLiveData<Boolean>(false)
    val isChanged: LiveData<Boolean>
        get() = _isChanged

    private val _editCancelEvent = MutableLiveData<Event<Unit>>()
    val editCancelEvent: LiveData<Event<Unit>>
        get() = _editCancelEvent

    private val _editDoneEvent = MutableLiveData<Event<Unit>>()
    val editDoneEvent: LiveData<Event<Unit>>
        get() = _editDoneEvent

    private val _editDeleteEvent = MutableLiveData<Event<DialogInterface.OnClickListener>>()
    val editDeleteEvent: LiveData<Event<DialogInterface.OnClickListener>>
        get() = _editDeleteEvent

    private val _applyEvent = MutableLiveData<Event<Unit>>()
    val applyEvent: LiveData<Event<Unit>>
        get() = _applyEvent

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

    private var bannerClick: AdminBannerListAdapter.AdminBannerClickListener = object: AdminBannerListAdapter.AdminBannerClickListener {
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
            override fun onEndDrag(currentList: List<Banner>) {
                bannerIsChanged()
            }
        })
    }
    private val itemTouchHelper: AdminBannerTouchHelper = AdminBannerTouchHelper(adapter)
    val helper: ItemTouchHelper = ItemTouchHelper(itemTouchHelper)

    init {
        viewModelScope.launch {
            if (userRepository.getMyPermission(userRepository.getLoginID()) > 2)
                loadBanners()
        }
    }

    fun loadBanners() {
        viewModelScope.launch {
            immutableBanners = serverRepository.getAllBanners(true)
            banners.value = serverRepository.getAllBanners(true).toMutableList()
            _isChanged.value = false
        }
    }

    fun btnEvent(btnId: Int) {
        when(btnId) {
            R.id.dialog_my_admin_banner_edit_footer_apply -> bannerEditDone()
            R.id.dialog_my_admin_banner_edit_footer_cancel -> _editCancelEvent.value = Event(Unit)
            R.id.dialog_my_admin_banner_edit_delete -> bannerDelete()
            R.id.frag_my_pref_admin_banner_done -> _applyEvent.value = Event(Unit)
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
        val flag = !banners.isListEquals(immutableBanners)
        _isChanged.value = flag
        return flag
    }

    private fun bannerEditDone() {
        setBannerById(editBanner.getId(), editBanner.value!!)
        _editCancelEvent.value = Event(Unit)
        _editDoneEvent.value = Event(Unit)
    }

    private fun bannerDelete() {
        val listener = DialogInterface.OnClickListener { _, _ ->
            viewModelScope.launch {
                banners.value?.let {
                    serverRepository.deleteBanner(editBanner.value!!)
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
        banners.value?.forEachIndexed { index, it ->
            if (it.id == id) {
                banners.value?.set(index, banner)
                bannerIsChanged()
                return
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
        fun getId(): Int {
            if (value == null) return -1
            return value!!.id
        }
    }
}