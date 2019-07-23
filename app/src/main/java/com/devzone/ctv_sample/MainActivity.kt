package com.devzone.ctv_sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.devzone.checkabletextview.CheckedListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), CheckedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkedTV.setOnCheckChangeListener(this@MainActivity)
        checkedSecondTV.setOnCheckChangeListener(this@MainActivity)
    }

    override fun onCheckChange(view: View, isChecked: Boolean) {
        when (view.id) {
            R.id.checkedTV -> stateTV.text = if (isChecked) "Checked" else "Unchecked"
            R.id.checkedSecondTV -> stateSecondTV.text = if (isChecked) "Checked" else "Unchecked"
        }
    }
}
