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

}