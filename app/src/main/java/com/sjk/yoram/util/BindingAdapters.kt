package com.sjk.yoram.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.DatePicker.OnDateChangedListener
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.budiyev.android.codescanner.*
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputLayout
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.kizitonwose.calendarview.utils.yearMonth
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.*
import com.sjk.yoram.presentation.common.adapter.AddressListAdapter
import com.sjk.yoram.presentation.common.listener.RadioItemSelectedListener
import com.sjk.yoram.presentation.common.listener.TextInputChanged
import com.sjk.yoram.presentation.common.model.GiveListItem
import com.sjk.yoram.presentation.common.model.SexState
import com.sjk.yoram.presentation.main.board.BoardCategoryListAdapter
import com.sjk.yoram.presentation.main.department.*
import com.sjk.yoram.presentation.main.department.Department
import com.sjk.yoram.presentation.main.department.manager.ManagerGiveListAdapter
import com.sjk.yoram.presentation.main.department.manager.UserManagerDepartmentListAdapter
import com.sjk.yoram.presentation.main.my.attend.DayViewContainer
import com.sjk.yoram.presentation.main.my.give.GiveListAdapter
import com.sjk.yoram.presentation.main.my.preference.admin.banner.AdminBannerListAdapter
import com.sjk.yoram.presentation.main.my.preference.admin.department.AdminDepartmentListAdapter
import com.sjk.yoram.presentation.main.my.preference.admin.give.AdminGiveTypeListAdapter
import com.sjk.yoram.presentation.main.my.preference.admin.newuser.AdminNewUserListAdapter
import com.sjk.yoram.presentation.main.my.preference.admin.position.AdminPositionListAdapter
import com.sjk.yoram.presentation.main.my.preference.admin.worship.AdminWorshipListAdapter
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerView
import jp.wasabeef.transformers.coil.GrayscaleTransformation
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

object BindingAdapters {
//    @JvmStatic
//    @BindingAdapter("checkedBtnAttrChanged")
//    fun setToggleGroupChangedListener(toggleGroup: MaterialButtonToggleGroup, listener: InverseBindingListener) {
//        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked -> listener.onChange() }
//    }
//
//    @JvmStatic
//    @InverseBindingAdapter(attribute = "checkedBtn", event = "checkedBtnAttrChanged")
//    fun getChecked(toggleGroup: MaterialButtonToggleGroup): Boolean? {
//        if (toggleGroup.checkedButtonIds.size == 0) return null
//        return toggleGroup.checkedButtonId == R.id.init_signup_sex_male_btn
//    }
//
//    @JvmStatic
//    @BindingAdapter("checkedBtn")
//    fun setChecked(toggleGroup: MaterialButtonToggleGroup, bool: Boolean?) {
//        if (bool == null)
//            toggleGroup.clearChecked()
//        else {
//            if (bool) toggleGroup.check(R.id.init_signup_sex_male_btn)
//            else toggleGroup.check(R.id.init_signup_sex_female_btn)
//        }
//    }


    @BindingAdapter("imageUrl", "error", "circleImage", "grayScale", requireAll = false)
    @JvmStatic
    fun setImageUrl(view: ImageView, imageUrl: String?, error: Drawable, circleImage: Boolean = false, grayScale: Boolean = false) {
        if (imageUrl.isNullOrEmpty()) {
            view.load(error) {
                crossfade(true)
                if (circleImage) transformations(CircleCropTransformation())
            }
        } else {
            view.load(imageUrl) {
                error(error)
                crossfade(true)
                placeholder(error)
                if (circleImage) transformations(CircleCropTransformation())
                if (grayScale) transformations(GrayscaleTransformation())
            }
        }
    }
    @BindingAdapter("bitmapImg", "error", "circleImage")
    @JvmStatic
    fun setImageBitmap(view: ImageView, bitmap: Bitmap?, error: Drawable, circleImage: Boolean = false) {
        if (bitmap == null) {
            view.load(error) {
                crossfade(true)
                if (circleImage) transformations(CircleCropTransformation())
            }
        }
        else {
            view.load(bitmap) {
                crossfade(true)
                if (circleImage) transformations(CircleCropTransformation())
            }
        }
    }

    @BindingAdapter("onCheckedChanged")
    @JvmStatic
    fun setOnCheckedButtonChanged(view: MaterialButtonToggleGroup, listener: MaterialButtonToggleGroup.OnButtonCheckedListener) {
        view.clearOnButtonCheckedListeners()
        view.addOnButtonCheckedListener(listener)
    }

    @BindingAdapter("inputTextChanged")
    @JvmStatic
    fun setOnInputTextChanged(view: TextInputLayout, textChanged: TextInputChanged) {
        if (view.editText == null)
            return
        val watcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                textChanged.afterTextChanged(view, p0.toString())
            }
        }
        view.editText!!.addTextChangedListener(watcher)
    }

    @BindingAdapter("RequiredBooleanList")
    @JvmStatic
    fun isRequireAllDone(view: AppCompatButton, list: MutableList<Boolean>) {
        list.forEach {
            if (it) {
                view.setBackgroundResource(R.color.xd_light_dot_indicator_enabled)
                view.isClickable = true
            } else {
                view.setBackgroundResource(R.color.xd_light_text_hint)
                view.isClickable = false
                return
            }
        }
    }


    @BindingAdapter("sexState")
    @JvmStatic
    fun setSexState(view: MaterialButtonToggleGroup, sexState: SexState?) {
        if (sexState == null)
            view.clearChecked()
        else if (sexState == SexState.MALE)
            view.check(R.id.init_signup_sex_male_btn)
        else if (sexState == SexState.FEMALE)
            view.check(R.id.init_signup_sex_female_btn)
        else
            view.clearChecked()
    }
    @InverseBindingAdapter(attribute = "sexState", event = "sexStateChanged")
    @JvmStatic
    fun getSexState(view: MaterialButtonToggleGroup): SexState {
        return if (view.checkedButtonId == R.id.init_signup_sex_male_btn)
            SexState.MALE
        else if (view.checkedButtonId == R.id.init_signup_sex_female_btn)
            SexState.FEMALE
        else
            SexState.NONE
    }
    @BindingAdapter("sexStateChanged")
    @JvmStatic
    fun sexStateChange(view: MaterialButtonToggleGroup, listener: InverseBindingListener) {
        view.clearOnButtonCheckedListeners()
        view.addOnButtonCheckedListener { group, checkedId, isChecked ->
            listener.onChange()
        }
    }


    @BindingAdapter("findSexCheckedButton")
    @JvmStatic
    fun findSexCheckedButton(view: MaterialButtonToggleGroup, sexState: SexState) {
        when (sexState) {
            SexState.NONE -> view.clearChecked()
            SexState.MALE -> view.check(R.id.init_signup_sex_male_btn)
            SexState.FEMALE -> view.check(R.id.init_signup_sex_female_btn)
        }
    }

    @BindingAdapter("setAddressItems", "highlightKeyword", "onItemClickListener")
    @JvmStatic
    fun setAddressItems(view: RecyclerView, items: List<Juso>?, keyword: String, listener: AddressListAdapter.AddressItemClickListener) {
        if (view.adapter == null) view.adapter = AddressListAdapter(listener)
        if (items.isNullOrEmpty()) (view.adapter as AddressListAdapter).submitList(listOf(), "")
        else (view.adapter as AddressListAdapter).submitList(items, keyword)
    }

    @BindingAdapter("phoneFormatting")
    @JvmStatic
    fun setPhoneFormatting(view: TextInputLayout, bool: Boolean) {
        if (view.editText == null) return
        if (bool) view.editText!!.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    @BindingAdapter("errorMessage")
    @JvmStatic
    fun setErrorMessage(view: TextInputLayout, error: String?) {
        if (view.editText == null) return
        view.error = error
    }

    @BindingAdapter("currencyFormatting")
    @JvmStatic
    fun setCurrencyFormatting(view: TextInputLayout, bool: Boolean) {
        if (view.editText == null) return
        if (!bool) return

        val format = DecimalFormat("###,###")
        view.editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                if (p0.isNullOrEmpty()) {
                    view.editText?.setText("0")
                    return
                }

                val formatting = format.format(p0.toString().replace(",", "").toBigInteger())
                if (formatting.contentEquals(p0)) return
                view.editText?.setText(formatting)
                view.editText?.setSelection(formatting.length)
            }
        })
    }


    @BindingAdapter("PowerSpinnerListener")
    @JvmStatic
    fun setOnSpinnerItemSelectedListener(view: PowerSpinnerView, listener: OnSpinnerItemSelectedListener<String>) {
        view.setOnSpinnerItemSelectedListener(listener)
    }

    @BindingAdapter("PowerSpinnerDefaultSelectIndex")
    @JvmStatic
    fun setDefaultSelectIndex(view: PowerSpinnerView, index: Int) {
        view.selectItemByIndex(index)
    }

    @BindingAdapter("PowerSpinnerSelection")
    @JvmStatic
    fun setSpinnerSelectIndex(view: PowerSpinnerView, selection: Int) {
        if(view.selectedIndex != selection)
            view.selectItemByIndex(selection)
    }
    @BindingAdapter("PowerSpinnerSelectionAttrChanged")
    @JvmStatic
    fun setSelectionChangedListener(view: PowerSpinnerView, listener: InverseBindingListener?) {
        if(view.hasSelection())
            listener?.onChange()
    }

    @InverseBindingAdapter(attribute = "PowerSpinnerSelection")
    @JvmStatic
    fun getSpinnerSelectIndex(view: PowerSpinnerView): Int {
        return view.selectedIndex
    }

    @BindingAdapter("SimpleUserData", "highlightKeyword")
    @JvmStatic
    fun setOnRecyclerAdapterSimpleUserData(view: RecyclerView, data: List<SimpleUser>?, keyword: String) {
        if (data == null)
                return
        (view.adapter as SimpleUserListAdapter).submitList(data, keyword)
    }
    @BindingAdapter("GiveData")
    @JvmStatic
    fun setOnRecyclerAdapterGiveData(view: RecyclerView, data: List<GiveListItem>) {
        (view.adapter as GiveListAdapter).submitList(data)
    }

    @BindingAdapter("DepartmentNodeList")
    @JvmStatic
    fun setOnDepartmentListAdapter(view: RecyclerView, list: List<DepartmentNode>?) {
        (view.adapter as ExpandableDepartmentNodeListAdapter).submitNodeList(list)
    }

    @BindingAdapter("radioItems")
    @JvmStatic
    fun setRadioGroupItem(view: RadioGroup, list: List<String>?) {
        list?.forEach { item ->
            view.addView(MaterialRadioButton(view.context).apply {
                text = item
            })
        }
    }
    @BindingAdapter("radioItemSelectChanged")
    @JvmStatic
    fun setRadioItemSelectedListener(view: RadioGroup, listener: RadioItemSelectedListener) {
        view.setOnCheckedChangeListener { radioGroup, i ->
            listener.onChange(radioGroup, radioGroup.findViewById<MaterialRadioButton>(radioGroup.checkedRadioButtonId).text.toString())
        }
    }

//    @BindingAdapter("calendarDayBinder")
//    @JvmStatic
//    fun setCalendarDayBinder(view: CalendarView, binder: DayBinder<DayViewContainer>) {
//        view.dayBinder = binder
//    }
//
//    @BindingAdapter("calendarMonthHeaderBinder")
//    @JvmStatic
//    fun setCalendarMonthHeaderBinder(view: CalendarView, binder: MonthHeaderFooterBinder<MonthViewContainer>) {
//        view.monthHeaderBinder = binder
//    }

    @BindingAdapter("calendarCurrentMonth")
    @JvmStatic
    fun setCalendarCurrentMonth(view: CalendarView, currentMonth: YearMonth) {
        view.smoothScrollToMonth(currentMonth)
    }

    @BindingAdapter("calendarMonthScrollListener")
    @JvmStatic
    fun setCalendarMonthScrollListener(view: CalendarView, listener: MonthScrollListener) {
        view.monthScrollListener = listener
    }

    @BindingAdapter("calendarText")
    @JvmStatic
    fun setText(view: TextView, yearMonth: YearMonth) {
        view.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
    }

    @BindingAdapter("calendarCheckDateList")
    @JvmStatic
    fun setCalendarCheckDate(view: CalendarView, data: List<LocalDate>) {
        view.dayBinder = object: DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                container.textView.setOnClickListener {
                }

                if (day.owner == DayOwner.THIS_MONTH) {
                    container.textView.setTextColor(view.context.getColor(R.color.xd_light_title))
                    if (day.date.dayOfWeek == DayOfWeek.SUNDAY)
                        container.textView.setTextColor(view.context.applicationContext.getColor(R.color.xd_light_red_highlight))
                } else {
                    container.textView.setTextColor(view.context.getColor(R.color.xd_light_text_hint))
                    if (day.date.dayOfWeek == DayOfWeek.SUNDAY) {
                        container.textView.setTextColor(view.context.getColor(R.color.xd_light_red_highlight))
                        container.textView.alpha = 0.4f
                    }
                }

                if (day.date == LocalDate.now()) container.textView.setTextColor(view.context.getColor(R.color.xd_light_dot_indicator_enabled))


                if (data.contains(day.date)) {
                    container.checker.visibility = View.VISIBLE
                    container.textView.setTextColor(view.context.getColor(R.color.xd_light_background))
                } else {
                    container.checker.visibility = View.INVISIBLE
                }
            }
        }
        data.forEach {
            view.notifyMonthChanged(it.yearMonth)
        }
    }

    @BindingAdapter("AdminBannerItem")
    @JvmStatic
    fun setAdminBannerItem(view: RecyclerView, list: MutableList<Banner>) {
        (view.adapter as AdminBannerListAdapter).submitList(list)
    }

//    @BindingAdapter("AdminBannerItemChanged")
//    @JvmStatic
//    fun setAdminBannerItemChanged(view: RecyclerView, listener: InverseBindingListener) {
//        (view.adapter as AdminBannerListAdapter).setListChangedListener(object: AdminBannerListAdapter.ListChangedListener {
//            override fun onListChanged() {
//                listener.onChange()
//            }
//        })
//    }
//
//    @InverseBindingAdapter(attribute = "AdminBannerItem", event = "AdminBannerItemChanged")
//    @JvmStatic
//    fun getAdminBannerItem(view: RecyclerView): MutableList<Banner> {
//        return (view.adapter as AdminBannerListAdapter).currentList
//    }

    @BindingAdapter("AdminBannerHelper")
    @JvmStatic
    fun setAdminBannerHelper(view: RecyclerView, helper: ItemTouchHelper) {
        helper.attachToRecyclerView(view)
    }

    @BindingAdapter("UserManagerDepartmentItem")
    @JvmStatic
    fun setUserManagerDepartmentItem(view: RecyclerView, list: MutableList<Department>) {
        (view.adapter as UserManagerDepartmentListAdapter).submitList(list)
    }

    @BindingAdapter("spinnerEntries")
    @JvmStatic
    fun setEntries(view: Spinner, entries: List<Any>?) {
        entries?.let {
            val arrayAdapter = ArrayAdapter(view.context, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, entries)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            view.adapter = arrayAdapter
        }
    }

    @BindingAdapter("ManagerGiveItem")
    @JvmStatic
    fun setManagerGiveItem(view: RecyclerView, items: List<Give>?) {
        (view.adapter as ManagerGiveListAdapter).submitList(items)
    }


    @BindingAdapter("datePickerInitDate", "datePickerDateChanged", requireAll = true)
    @JvmStatic
    fun initDatePicker(view: DatePicker, date: LocalDate, listener: OnDateChangedListener) {
        view.init(date.year, date.monthValue-1, date.dayOfMonth, listener)
    }

    @BindingAdapter("AdminWorshipList")
    @JvmStatic
    fun setAdminWorshipList(view: RecyclerView, list: MutableList<WorshipType>) {
        (view.adapter as AdminWorshipListAdapter).submitList(list)
    }

    @BindingAdapter("AdminGiveTypeList")
    @JvmStatic
    fun setAdminGiveTypeList(view: RecyclerView, list: MutableList<GiveType>) {
        (view.adapter as AdminGiveTypeListAdapter).submitList(list)
    }

    @BindingAdapter("AdminDepartmentList")
    @JvmStatic
    fun setAdminDepartmentList(view: RecyclerView, list: MutableList<com.sjk.yoram.data.entity.Department>) {
        (view.adapter as AdminDepartmentListAdapter).submitList(list)
    }

    @BindingAdapter("AdminPositionList")
    @JvmStatic
    fun setAdminPositionList(view: RecyclerView, list: MutableList<Position>) {
        (view.adapter as AdminPositionListAdapter).submitList(list)
    }

    @BindingAdapter("RefreshEvent")
    @JvmStatic
    fun setRefreshRecyclerAction(view: SwipeRefreshLayout, action: () -> Unit) {
        view.setOnRefreshListener {
            action()
        }
    }

    @BindingAdapter("isRefreshing")
    @JvmStatic
    fun setRefreshLayoutIsRefresh(view: SwipeRefreshLayout, isRefresh: Boolean) {
        view.isRefreshing = isRefresh
    }

    @BindingAdapter("AdminNewUserList")
    @JvmStatic
    fun setAdminNewUserList(view: RecyclerView, list: MutableList<NewUserForAdmin>) {
        (view.adapter as AdminNewUserListAdapter).submitList(list)
    }


    @BindingAdapter("selectedCategory")
    @JvmStatic
    fun setSelectedCategory(view: RecyclerView, category: ReservedBoardCategory?) {
        if (category == null)
            (view.adapter as BoardCategoryListAdapter).selectCategoryChange(0)
        val list = (view.adapter as BoardCategoryListAdapter).currentList
        (view.adapter as BoardCategoryListAdapter).selectCategoryChange(list.indexOf(category))
    }

    @InverseBindingAdapter(attribute = "selectedCategory", event = "selectedCategoryChanged")
    @JvmStatic
    fun getSelectedCategory(view: RecyclerView): ReservedBoardCategory? {
        if (view.adapter !is BoardCategoryListAdapter)
            return null

        val list = (view.adapter as BoardCategoryListAdapter).currentList
        val pos = (view.adapter as BoardCategoryListAdapter).currentCategoryPos
        return list.getOrNull(pos)
    }

    @BindingAdapter("selectedCategoryChanged")
    @JvmStatic
    fun selectedCategoryChange(view: RecyclerView, listener: InverseBindingListener) {
        listener.onChange()
    }
}