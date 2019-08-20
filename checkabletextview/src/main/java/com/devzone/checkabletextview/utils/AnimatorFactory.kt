package com.devzone.checkabletextview.utils

import android.view.View
import android.view.ViewPropertyAnimator
import com.devzone.checkabletextview.CheckableTextView

class AnimatorFactory {
    companion object {

        fun getAnimator(animateStyle: Int, view: View, show: Boolean, duration: Long): ViewPropertyAnimator {
            return when (animateStyle) {
                CheckableTextView.SCALE -> getScaleAnimator(view, show, duration)
                CheckableTextView.TRANSLATE -> getTranslateAnimator(view, show, duration)
                CheckableTextView.FALL_DOWN -> getFallDownAnimator(view, show, duration)
                else -> getScaleAnimator(view, show, duration)
            }

        }

        private fun getScaleAnimator(view: View, show: Boolean, animDuration: Long): ViewPropertyAnimator {
            //resetting view to initial state for this animation (if In case user sets new animation on the fly)
            view.translationX = CheckableTextView.MIN_VALUE
            view.translationY = CheckableTextView.MIN_VALUE

            val scale = if (show) CheckableTextView.MAX_SCALE else CheckableTextView.MIN_VALUE
            val rotation = if (show) CheckableTextView.MIN_VALUE else -CheckableTextView.MAX_ROTATION
            return view.animate().setStartDelay(20).scaleX(scale).scaleY(scale).rotation(rotation)
                .setDuration(animDuration)
        }

        private fun getTranslateAnimator(view: View, show: Boolean, animDuration: Long): ViewPropertyAnimator {
            view.scaleX = CheckableTextView.MAX_SCALE
            view.scaleY = CheckableTextView.MAX_SCALE
            view.translationY = CheckableTextView.MIN_VALUE

            val translate = if (show) CheckableTextView.MIN_VALUE else (view.width.toFloat() + view.width / 2)
            val rotation = if (show) CheckableTextView.MIN_VALUE else CheckableTextView.MAX_ROTATION
            return view.animate().setStartDelay(20).translationX(translate).rotation(rotation)
                .setDuration(animDuration)

        }

        private fun getFallDownAnimator(view: View, show: Boolean, animDuration: Long): ViewPropertyAnimator {
            view.scaleX = CheckableTextView.MAX_SCALE
            view.scaleY = CheckableTextView.MAX_SCALE
            view.rotation = CheckableTextView.MIN_VALUE

            val trValue = (view.height.toFloat() + view.height / 2)
            if (show) view.translationY = -trValue
            val translate = if (show) CheckableTextView.MIN_VALUE else trValue
            return view.animate().setStartDelay(20).translationY(translate)
                .setDuration(animDuration)
        }

    }
}