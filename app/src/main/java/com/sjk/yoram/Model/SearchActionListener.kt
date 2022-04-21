package com.sjk.yoram.Model

import com.mancj.materialsearchbar.MaterialSearchBar

class SearchActionListener: MaterialSearchBar.OnSearchActionListener {
    override fun onSearchStateChanged(enabled: Boolean) {
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        TODO("Not yet implemented")
    }

    override fun onButtonClicked(buttonCode: Int) {
        when(buttonCode) {
            MaterialSearchBar.BUTTON_NAVIGATION -> {
                // drawer open
            }
            MaterialSearchBar.BUTTON_BACK -> {
                // drawer close
            }
            else -> {}
        }
    }
}