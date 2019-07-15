package com.devzone.checkabletextview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_checkable_text.view.*


class CheckableTextView : RelativeLayout {

    private var isChecked: Boolean = true
    private var listener: CheckedListener? = null
    private val defaultCheckIcon = R.drawable.ic_check_circle_vector
    private val defaultTextColor = android.R.color.black
    private val defaultIconTintColor = android.R.color.transparent
    private var checkIcon = defaultCheckIcon

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context, attributeSet)
    }


    private fun init(context: Context, attributeSet: AttributeSet?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        LayoutInflater.from(context).inflate(
                R.layout.layout_checkable_text,
                this, true)
        attributeSet.let {
            val array: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CheckableTextView)
            val iconTint = array.getColor(
                R.styleable.CheckableTextView_ctv_IconTint,
                ContextCompat.getColor(context, defaultIconTintColor)
            )
            val textColor = array.getColor(
                R.styleable.CheckableTextView_ctv_TextColor,
                ContextCompat.getColor(context, defaultTextColor)
            )
            val text = array.getString(R.styleable.CheckableTextView_ctv_Text)
            isChecked = array.getBoolean(R.styleable.CheckableTextView_ctv_IconChecked, false)
            val textSize = array.getDimensionPixelSize(R.styleable.CheckableTextView_ctv_TextSize, 0)
            val textStyle = array.getResourceId(R.styleable.CheckableTextView_ctv_TextStyle, 0)
            checkIcon = array.getResourceId(R.styleable.CheckableTextView_ctv_Icon, 0)

            //giving applied style attrs least preference (colors n text size will be override by ctv_TextColor & ctv_TextSize as applied later)
            applyTextStyle(textStyle, context)
            validateCheckIcon(context)
            checkedTextTV.text = text
            checkedTextTV.isSelected = true
            checkedTextTV.setTextColor(textColor)
            checkedIV.setImageResource(checkIcon)

            if (isValidRes(textSize))
                checkedTextTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            if (isValidRes(iconTint))
                checkedIV.setColorFilter(iconTint)

            array.recycle()
        }

        animateView(checkedIV, isChecked)
        rootRL.setOnClickListener {
            checkedTextTV.text = checkedTextTV.text
            checkedTextTV.isSelected = true
            isChecked = !isChecked
            animateView(checkedIV, isChecked)
            notifyListener(isChecked)
        }
    }

    private fun applyTextStyle(textStyle: Int, context: Context) {
        if (isValidRes(textStyle)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkedTextTV.setTextAppearance(textStyle)
            } else {
                checkedTextTV.setTextAppearance(context, textStyle)
            }
        }
    }

    private fun animateView(view: View, show: Boolean) {
        view.clearAnimation()
        val scale = if (show) 1f else 0f
        val rotation = if (show) 360f else -360f
        view.animate().setStartDelay(20).scaleX(scale).scaleY(scale).rotation(rotation).setDuration(250)
            .start()
    }

    private fun validateCheckIcon(context: Context) {
        if (isValidRes(checkIcon)) {
            val drawableIcon = ContextCompat.getDrawable(context, checkIcon)
            if (drawableIcon == null) checkIcon = defaultCheckIcon
            drawableIcon.let {
                if (it is ColorDrawable) {
                    checkIcon = defaultCheckIcon
                }
            }
        } else checkIcon = defaultCheckIcon
    }


    private fun isValidRes(res: Int) = res != 0

    private fun notifyListener(isChecked: Boolean) {
        listener?.onCheckChange(this, isChecked)
    }

    fun setOnCheckChangeListener(listener: CheckedListener) {
        this.listener = listener
    }

    fun setChecked(isChecked: Boolean, shouldNotifyListeners: Boolean) {
        this.isChecked = isChecked
        animateView(checkedIV, isChecked)
        if (shouldNotifyListeners)
            notifyListener(isChecked)
    }

    fun setChecked(isChecked: Boolean) {
        setChecked(isChecked, false)
    }

    interface CheckedListener {
        fun onCheckChange(view: View, isChecked: Boolean)
    }
}