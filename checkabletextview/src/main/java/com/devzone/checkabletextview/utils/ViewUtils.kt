package com.devzone.checkabletextview.utils

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import com.devzone.checkabletextview.CheckableTextView

class ViewUtils {
    companion object{
         fun getThemeAccentColor(context: Context): String {
            return try {
                val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    android.R.attr.colorAccent
                } else {
                    //Get colorAccent defined for AppCompat
                    context.resources.getIdentifier("colorAccent", "attr", context.packageName)
                }
                val outValue = TypedValue()
                context.theme.resolveAttribute(colorAttr, outValue, true)
                String.format("#%06X", 0xFFFFFF and outValue.data)
            } catch (e: Exception) {
                "#00FFFFFF"
            }
        }

         fun getRippleDrawable(context: Context): Int {
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            return outValue.resourceId
        }

         fun resetView(animateStyle:Int,view: View, show: Boolean) {
            view.clearAnimation()
            when (animateStyle) {
                CheckableTextView.SCALE -> {
                    view.translationX = CheckableTextView.MIN_VALUE
                    view.translationY = CheckableTextView.MIN_VALUE
                    view.rotation = if (show) CheckableTextView.MIN_VALUE else -CheckableTextView.MAX_ROTATION
                    view.scaleX = if (show) CheckableTextView.MAX_SCALE else CheckableTextView.MIN_VALUE
                    view.scaleY = if (show) CheckableTextView.MAX_SCALE else CheckableTextView.MIN_VALUE
                }
                CheckableTextView.TRANSLATE -> {
                    view.scaleX = CheckableTextView.MAX_SCALE
                    view.scaleY = CheckableTextView.MAX_SCALE
                    view.rotation = if (show) CheckableTextView.MIN_VALUE else CheckableTextView.MAX_ROTATION
                    view.translationY = CheckableTextView.MIN_VALUE
                    view.translationX = if (show) CheckableTextView.MIN_VALUE else view.width.toFloat() + view.width / 2

                }
                CheckableTextView.FALL_DOWN -> {
                    view.scaleX = CheckableTextView.MAX_SCALE
                    view.scaleY = CheckableTextView.MAX_SCALE
                    view.rotation = CheckableTextView.MIN_VALUE
                    view.translationX = CheckableTextView.MIN_VALUE
                    view.translationY = if (show) CheckableTextView.MIN_VALUE else view.height.toFloat() + view.height / 2
                }
            }

        }

    }
}

