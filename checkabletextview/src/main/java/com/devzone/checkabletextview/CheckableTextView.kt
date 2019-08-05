package com.devzone.checkabletextview

import android.animation.TimeInterpolator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.annotation.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_checkable_text.view.*


class CheckableTextView : RelativeLayout {

    companion object {
        const val SCALE = 0
        const val TRANSLATE = 1
        const val FALL_DOWN = 2
    }

    // default values
    private val defaultResValue: Int = 0
    private val defaultAnimDuration: Long = 300
    private val defaultAnimateStyle: Int = SCALE
    private val defaultCheckState: Boolean = true
    private val defaultTextColor = android.R.color.black
    private val defaultCheckIcon = R.drawable.ic_check_circle_vector

    //initialise with default values
    private var checkIcon = defaultCheckIcon
    private var animateStyle = defaultAnimateStyle
    private var isChecked: Boolean = defaultCheckState
    private var animDuration: Long = defaultAnimDuration
    private var animInterpolator: TimeInterpolator = LinearInterpolator()


    // check change listeners
    private var listener: CheckedListener? = null  //Legacy type callback listener using interface (Both java & kotlin)

    /**
     * [Function],[Function2] (for two variables)
     * kotlin.jvm.functions.Function2<View, Boolean, kotlin.Unit>() can be used with java code but requires Kotlin setup in project
     */
    private var listenerNew: ((v: View, isChecked: Boolean) -> Unit)? =
        null //New type introduced by kotlin (Function2 , function as parameter)

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
        attributeSet?.let {
            val array: TypedArray = context.obtainStyledAttributes(it, R.styleable.CheckableTextView)
            if (array.length() > 0) {
                val iconTint = array.getColor(
                    R.styleable.CheckableTextView_ctv_IconTint,
                    Color.parseColor(getThemeAccentColor(context))
                )
                val textColor = array.getColor(
                    R.styleable.CheckableTextView_ctv_TextColor,
                    ContextCompat.getColor(context, defaultTextColor)
                )
                val text = array.getString(R.styleable.CheckableTextView_ctv_Text)
                isChecked = array.getBoolean(R.styleable.CheckableTextView_ctv_IconChecked, defaultCheckState)
                val textSize = array.getDimensionPixelSize(R.styleable.CheckableTextView_ctv_TextSize, defaultResValue)
                val textStyle = array.getResourceId(R.styleable.CheckableTextView_ctv_TextStyle, defaultResValue)
                checkIcon = array.getResourceId(R.styleable.CheckableTextView_ctv_Icon, defaultResValue)
                val gravity = array.getInt(R.styleable.CheckableTextView_ctv_TextGravity, Gravity.CENTER)
                animateStyle = array.getInt(R.styleable.CheckableTextView_ctv_AnimType, SCALE)
                val animDuration =
                    array.getInt(R.styleable.CheckableTextView_ctv_AnimDuration, defaultAnimDuration.toInt()).toLong()
                setAnimDuration(animDuration)

                //giving applied style attrs least preference (colors n text size will be override by ctv_TextColor & ctv_TextSize as applied later)
                applyTextStyle(textStyle, context)
                validateCheckIcon(context)
                setText(text ?: "")
                setTextGravity(gravity)
                checkedTextTV.setTextColor(textColor)
                checkedIV.setImageResource(checkIcon)

                if (isValidRes(textSize))
                    checkedTextTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

                if (isValidRes(iconTint))
                    checkedIV.setColorFilter(iconTint)
            }
            array.recycle()
        }

        animateView(checkedIV, isChecked)
        rootRL.setOnClickListener(clickListener())
    }

    private fun clickListener(): (v: View) -> Unit {
        return {
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
        val animator = when (animateStyle) {
            SCALE -> getScaleAnimator(view, show)
            TRANSLATE -> getTranslateAnimator(view, show)
            FALL_DOWN -> getFallDownAnimator(view, show)
            else -> getScaleAnimator(view, show)
        }
        animator.setInterpolator(animInterpolator).start()
    }

    private fun getScaleAnimator(view: View, show: Boolean): ViewPropertyAnimator {
        //resetting view to initial state for this animation (if In case user sets new animation on the fly)
        view.translationX = 0f
        view.translationY = 0f

        val scale = if (show) 1f else 0f
        val rotation = if (show) 0f else -360f
        return view.animate().setStartDelay(20).scaleX(scale).scaleY(scale).rotation(rotation)
            .setDuration(animDuration)
    }

    private fun getTranslateAnimator(view: View, show: Boolean): ViewPropertyAnimator {
        view.scaleX = 1f
        view.scaleY = 1f
        view.translationY = 0f

        val translate = if (show) 0f else (view.width.toFloat() + view.width / 2)
        val rotation = if (show) 0f else 360f
        return view.animate().setStartDelay(20).translationX(translate).rotation(rotation)
            .setDuration(animDuration)

    }

    private fun getFallDownAnimator(view: View, show: Boolean): ViewPropertyAnimator {
        view.scaleX = 1f
        view.scaleY = 1f
        view.rotation = 0f

        val trValue = (view.height.toFloat() + view.height / 2)
        if (show) view.translationY = -trValue
        val translate = if (show) 0f else trValue
        return view.animate().setStartDelay(20).translationY(translate)
            .setDuration(animDuration)
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


    private fun isValidRes(res: Int) = res != defaultResValue
    private fun emptyNullCheck(text: String?) = text != null && !text.isBlank();

    private fun notifyListener(isChecked: Boolean) {
        listener?.onCheckChange(this, isChecked)
        listenerNew?.invoke(this, isChecked)
    }


    private fun getRippleDrawable(): Int {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        return outValue.resourceId
    }

    private fun getThemeAccentColor(context: Context): String {
        try {
            val colorAttr: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAttr = android.R.attr.colorAccent
            } else {
                //Get colorAccent defined for AppCompat
                colorAttr = context.resources.getIdentifier("colorAccent", "attr", context.packageName)
            }
            val outValue = TypedValue()
            context.theme.resolveAttribute(colorAttr, outValue, true)
            return String.format("#%06X", 0xFFFFFF and outValue.data)
        } catch (e: Exception) {
            return "#00FFFFFF"
        }
    }

    /*-------------------------------------------------public functions------------------------------------------------------------------------------------------*/



    /**
     * Change [CheckableTextView] click state
     * @param isClickable = pass true for enable clicks and false for disable clicks.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun setClickEnabled(isClickable: Boolean) {
        // 0.5 second delay added to ongoing ripple animation to complete (if any)
        rootRL.postDelayed(
            { rootRL.setBackgroundResource(if (isClickable) getRippleDrawable() else android.R.color.transparent) },
            500
        )
        rootRL.setOnClickListener(if (isClickable) clickListener() else null)
    }

    fun setOnCheckChangeListener(listener: CheckedListener) {
        this.listener = listener //only one type listener will invoke
        this.listenerNew = null
    }


    fun setOnCheckChangeListener(listenerNew: (view: View, isChecked: Boolean) -> Unit) {
        this.listener = null
        this.listenerNew = listenerNew
    }

    fun setChecked(isChecked: Boolean, shouldNotifyListeners: Boolean=false) {
        this.isChecked = isChecked
        animateView(checkedIV, isChecked)
        if (shouldNotifyListeners)
            notifyListener(isChecked)
    }


    fun isChecked(): Boolean {
        return this.isChecked
    }


    ////---------------------------setters------------------------------------------------------------------------------------------////

    fun setIconTint(@ColorRes resId: Int) {
        if (isValidRes(resId)) {
            val color = ContextCompat.getColor(context, resId)
            checkedIV.setColorFilter(color)
        }
    }

    fun setTextSize(@DimenRes resId: Int) {
        if (isValidRes(resId)) {
            val dimension = resources.getDimensionPixelSize(resId)
            checkedTextTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension.toFloat())
        }
    }

    fun setTextColor(@ColorRes resId: Int) {
        if (isValidRes(resId)) {
            val color = ContextCompat.getColor(context, resId)
            checkedTextTV.setTextColor(color)
        }
    }

    fun setText(@StringRes resId: Int) {
        if (isValidRes(resId)) {
            val string = context.getString(resId)
            setText(string)
        }
    }

    fun setText(text: String) {
        if (emptyNullCheck(text)) {
            checkedTextTV.text = text
            checkedTextTV.isSelected = true
        }
    }

    fun setTextGravity(gravity: Int) {
        checkedTextTV.gravity = gravity
    }

    fun setIcon(@DrawableRes resId: Int) {
        if (isValidRes(resId)) {
            checkIcon = resId
            validateCheckIcon(context)
            checkedIV.setImageResource(checkIcon)
        }
    }

    fun setTextStyle(@StyleRes resId: Int) {
        if (isValidRes(resId))
            applyTextStyle(resId, context)
    }

    /**
     * @param animType should be [SCALE],[TRANSLATE],[FALL_DOWN]
     */
    fun setAnimStyle(animType: Int) {
        animateStyle = when (animType) {
            SCALE -> SCALE
            TRANSLATE -> TRANSLATE
            FALL_DOWN -> FALL_DOWN
            else -> SCALE
        }
    }

    fun setAnimDuration(duration: Long) {
        if (duration.toInt() == 0 || duration < 0) return
        animDuration = duration
    }

    fun setAnimInterpolator(interpolator: TimeInterpolator) {
        animInterpolator = interpolator
    }

}