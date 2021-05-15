package com.devzone.checkabletextview

import android.animation.TimeInterpolator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.annotation.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import com.devzone.checkabletextview.utils.AnimatorFactory.Companion.getAnimator
import com.devzone.checkabletextview.utils.ViewUtils.Companion.getRippleDrawable
import com.devzone.checkabletextview.utils.ViewUtils.Companion.getThemeAccentColor
import com.devzone.checkabletextview.utils.ViewUtils.Companion.resetView
import kotlinx.android.synthetic.main.layout_checkable_text.view.*

class CheckableTextView : RelativeLayout {

    companion object {
        const val SCALE = 0
        const val TRANSLATE = 1
        const val FALL_DOWN = 2

        //-------------------------------------------------------------------------------//
        const val MIN_VALUE = 0f //(initial values for scale, translate etc.)
        const val MAX_SCALE = 1f //(max values for scale)
        const val MAX_ROTATION = 360f //(max values for rotation)
    }


    private var rippleTint: Int = Color.LTGRAY
    private var rippleAlpha: Int = 33
    private val defaultAnimFirstTime = true
    private val defaultResValue: Int = 0
    private val defaultAnimDuration: Long = 300
    private val defaultAnimateStyle = SCALE
    private val defaultCheckState = true
    private val defaultTextColor = android.R.color.black
    private val defaultCheckIcon = R.drawable.ic_check_circle_vector

    //initialise with default values
    private var checkIcon = defaultCheckIcon
    var animInterpolator: TimeInterpolator = LinearInterpolator()
    var animateFirstTime = defaultAnimFirstTime

    var isRippleFillEnabled = false
        private set

    var isChecked = defaultCheckState
        private set
    var animDuration = defaultAnimDuration
        private set
    var animateStyle = defaultAnimateStyle
        private set

    // check change listeners
    private var listener: CheckedListener? =
        null  //Legacy type callback listener using interface (Both java & kotlin)

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
        val root = LayoutInflater.from(context).inflate(
                R.layout.layout_checkable_text,
                this, true)

        initValuesFromAttrs(attributeSet, context)

        root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (animateFirstTime) {
                    resetView(animateStyle, checkedIV, !isChecked)
                    animateView(checkedIV, isChecked)
                } else
                    resetView(animateStyle, checkedIV, isChecked)

            }
        })
        rootRL.setOnClickListener(clickListener())
    }

    private fun initValuesFromAttrs(attributeSet: AttributeSet?, context: Context) {
        attributeSet?.apply {
            val array: TypedArray = context.obtainStyledAttributes(this, R.styleable.CheckableTextView)
            if (array.length() > 0) {

                rippleTint = array.getColor(
                    R.styleable.CheckableTextView_ctv_RippleTint,
                    Color.parseColor(getThemeAccentColor(context))
                )

                val iconTint = array.getColor(
                    R.styleable.CheckableTextView_ctv_IconTint,
                    Color.parseColor(getThemeAccentColor(context))
                )
                val textColor = array.getColor(
                    R.styleable.CheckableTextView_ctv_TextColor,
                    ContextCompat.getColor(context, defaultTextColor)
                )
                val text = array.getString(R.styleable.CheckableTextView_ctv_Text)
                animateFirstTime =
                    array.getBoolean(
                        R.styleable.CheckableTextView_ctv_AnimFirstTime,
                        defaultAnimFirstTime
                    )
                isChecked = array.getBoolean(
                    R.styleable.CheckableTextView_ctv_IconChecked,
                    defaultCheckState
                )
                isRippleFillEnabled =
                    array.getBoolean(R.styleable.CheckableTextView_ctv_RippleFillEnabled, false)
                val textSize = array.getDimensionPixelSize(
                    R.styleable.CheckableTextView_ctv_TextSize,
                    defaultResValue
                )
                val textStyle = array.getResourceId(
                    R.styleable.CheckableTextView_ctv_TextStyle,
                    defaultResValue
                )
                checkIcon =
                    array.getResourceId(R.styleable.CheckableTextView_ctv_Icon, defaultResValue)
                val gravity =
                    array.getInt(R.styleable.CheckableTextView_ctv_TextGravity, Gravity.CENTER)
                animateStyle = array.getInt(R.styleable.CheckableTextView_ctv_AnimType, SCALE)
                rippleAlpha = array.getInt(R.styleable.CheckableTextView_ctv_RippleAlpha, 33)
                normalizeRippleAlpha()
                val animDuration =
                    array.getInt(
                        R.styleable.CheckableTextView_ctv_AnimDuration,
                        defaultAnimDuration.toInt()
                    ).toLong()
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
    }

    private fun normalizeRippleAlpha() {
        if (rippleAlpha < 0)
            rippleAlpha = 0
        if (rippleAlpha > 255)
            rippleAlpha = 255
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
        if (isRippleFillEnabled)
            triggerFillAnimation(show)
        val animator = getAnimator(animateStyle, view, show, animDuration)
        animator.setInterpolator(animInterpolator).start()
    }

    private fun triggerFillAnimation(show: Boolean) {
        if (rippleAlpha <= 0) return
        val scale = if (show) width * 2f / fillIV.width else 0f
        val delay = if (show) animDuration / 2 else 0
        ViewCompat.setBackgroundTintList(
            fillIV,
            ColorStateList.valueOf(
                ColorUtils.setAlphaComponent(
                    rippleTint, rippleAlpha
                )
            )
        )
        fillIV.animate().setStartDelay(delay).scaleX(scale).scaleY(scale)
            .setDuration((animDuration * 1f / 1.1).toLong())
            .setInterpolator(AccelerateInterpolator())
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

    private fun isValidRes(res: Int) = res != defaultResValue
    private fun emptyNullCheck(text: String?) = text != null && !text.isBlank()

    private fun notifyListener(isChecked: Boolean) {
        listener?.onCheckChange(this, isChecked)
        listenerNew?.invoke(this, isChecked)
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
            { rootRL.setBackgroundResource(if (isClickable) getRippleDrawable(context) else android.R.color.transparent) },
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

    fun setChecked(isChecked: Boolean, shouldNotifyListeners: Boolean = false) {
        this.isChecked = isChecked
        animateView(checkedIV, isChecked)
        if (shouldNotifyListeners)
            notifyListener(isChecked)
    }

    fun setRippleFillEnabled(isEnabled: Boolean) {
        this.isRippleFillEnabled = isEnabled
    }

    fun setRippleTint(color: Int) {
        val extractedColor = try {
            ContextCompat.getColor(context, color)
        } catch (e: Exception) {
            color
        }
        setRippleFillEnabled(true)
        rippleTint = extractedColor
    }

    fun setRippleAlpha(alpha: Int) {
        this.rippleAlpha = alpha
        normalizeRippleAlpha()
    }

    fun setIconTint(color: Int) {
        val extractedColor = try {
            ContextCompat.getColor(context, color)
        } catch (e: Exception) {
            color
        }
        checkedIV.setColorFilter(extractedColor)

    }

    fun setTextSize(@DimenRes resId: Int) {
        if (isValidRes(resId)) {
            val dimension = resources.getDimensionPixelSize(resId)
            checkedTextTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension.toFloat())
        }
    }

    fun setTextColor(color: Int) {
        val extractedColor = try {
            ContextCompat.getColor(context, color)
        } catch (e: Exception) {
            color
        }
        checkedTextTV.setTextColor(extractedColor)

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
        animateStyle = if (animType in SCALE..FALL_DOWN) animType else animateStyle
    }

    fun setAnimDuration(duration: Long) {
        if (duration == 0L || duration < 0) return
        animDuration = duration
    }

}