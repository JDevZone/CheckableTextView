package com.devzone.ctv_sample

import android.os.Bundle
import android.view.View
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import com.devzone.checkabletextview.CheckedListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), CheckedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkedTV.setOnCheckChangeListener { view, isChecked -> stateTV.text = if (isChecked) "Checked" else "Unchecked" }
        checkedSecondTV.setOnCheckChangeListener(::checkHandler) // function as parameter
        checkedThirdTV.setOnCheckChangeListener(this@MainActivity)


        checkedTV.setAnimInterpolator(AnticipateOvershootInterpolator()) // setting custom interpolator
        checkedSecondTV.setAnimInterpolator(LinearInterpolator())
        checkedThirdTV.setAnimInterpolator(BounceInterpolator())

        checkedThirdTV.setAnimDuration(1000)
    }

    private fun checkHandler(view: View, isChecked: Boolean) {
        when (view.id) {
            R.id.checkedSecondTV -> stateSecondTV.text = if (isChecked) "Checked" else "Unchecked"
        }
    }

    override fun onCheckChange(view: View, isChecked: Boolean) { //lagacy type listener callback
        when (view.id) {
            R.id.checkedThirdTV -> stateThirdTV.text = if (isChecked) "Checked" else "Unchecked"
        }
    }

}
