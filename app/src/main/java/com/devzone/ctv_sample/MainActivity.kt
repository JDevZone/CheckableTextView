package com.devzone.ctv_sample

import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.devzone.checkabletextview.CheckableTextView
import com.devzone.checkabletextview.CheckableTextView.Companion.SCALE
import com.devzone.checkabletextview.CheckableTextView.Companion.TRANSLATE
import com.devzone.checkabletextview.CheckedListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), CheckedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkedTV.setOnCheckChangeListener { view, isChecked -> stateTV.text = if (isChecked) "Checked" else "Unchecked"}
        checkedSecondTV.setOnCheckChangeListener(::checkHandler) // function as parameter
        checkedThirdTV.setOnCheckChangeListener(this@MainActivity)


        checkedTV.animInterpolator = AnticipateOvershootInterpolator() // setting custom interpolator
        checkedSecondTV.animInterpolator = LinearInterpolator()
        checkedThirdTV.animInterpolator = BounceInterpolator()

        checkedThirdTV.setAnimDuration(1000)
    }

    private fun checkHandler(view: View, isChecked: Boolean) {

        when (view.id) {
            R.id.checkedSecondTV -> stateSecondTV.text = if (isChecked) "Checked" else "Unchecked"
        }
    }

    override fun onCheckChange(view: View, isChecked: Boolean) { //lagacy type listener callback
        checkedSecondTV.setAnimStyle(if(isChecked)TRANSLATE else SCALE)
        when (view.id) {
            R.id.checkedThirdTV -> stateThirdTV.text = if (isChecked) "Checked" else "Unchecked"
        }
    }

}
