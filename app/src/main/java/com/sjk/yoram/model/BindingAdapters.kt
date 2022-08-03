package com.sjk.yoram.model

import android.graphics.drawable.Drawable
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sjk.yoram.R
import com.sjk.yoram.model.ui.adapter.AddressListAdapter
import com.sjk.yoram.model.dto.Juso
import com.sjk.yoram.model.ui.adapter.DepartmentListAdapter
import com.sjk.yoram.model.ui.listener.AddressItemClickListener
import com.sjk.yoram.model.ui.listener.TextInputChanged
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerView

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

    @BindingAdapter("imageUrl", "error")
    @JvmStatic
    fun setImageUrl(imgView: ImageView, url: String?, error: Drawable?) {
        if (url.isNullOrEmpty())
            imgView.load(error)
        else
            imgView.load(url){
                error(error)
                crossfade(true)
                placeholder(error)
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
    fun setAddressItems(view: RecyclerView, items: List<Juso>?, keyword: String, listener: AddressItemClickListener) {
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



    @BindingAdapter("PowerSpinnerListener")
    @JvmStatic
    fun setOnSpinnerItemSelectedListener(view: PowerSpinnerView, listener: OnSpinnerItemSelectedListener<String>) {
        view.setOnSpinnerItemSelectedListener(listener)
    }

    @BindingAdapter("DepartmentData")
    @JvmStatic
    fun setOnRecyclerAdapterData(view: RecyclerView, data: List<Department>) {
        (view.adapter as DepartmentListAdapter).submitList(data)
    }

}