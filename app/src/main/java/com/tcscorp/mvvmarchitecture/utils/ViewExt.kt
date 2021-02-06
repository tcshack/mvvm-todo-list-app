package com.tcscorp.mvvmarchitecture.utils

import androidx.appcompat.widget.SearchView

/**
 * Created on 31/01/2021 16:57
 * @author tcscorp
 */
inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }
    })
}