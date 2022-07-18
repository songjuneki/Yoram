package com.sjk.yoram.model

import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.iterator
import androidx.databinding.*
import coil.load
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sjk.yoram.R

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

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun setImageUrl(imgView: ImageView, url: String) {
        if (url.isEmpty())
            imgView.setImageDrawable(null)
        else
            imgView.load(url)
    }

    @BindingAdapter("onCheckedChanged")
    @JvmStatic
    fun setOnCheckedButtonChanged(view: MaterialButtonToggleGroup, listener: MaterialButtonToggleGroup.OnButtonCheckedListener) {
        view.clearOnButtonCheckedListeners()
        view.addOnButtonCheckedListener(listener)
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

}