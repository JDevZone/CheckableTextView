package com.devzone.checkabletextview

import android.view.View

interface CheckedListener {
    fun onCheckChange(view: View, isChecked: Boolean)
}