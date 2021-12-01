package com.falcofemoralis.hdrezkaapp.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.falcofemoralis.hdrezkaapp.R
import com.falcofemoralis.hdrezkaapp.constants.DeviceType
import com.falcofemoralis.hdrezkaapp.objects.SettingsData


object Highlighter {
    const val ANIM_DURATION = 250L

    fun zoom(context: Context, layout: LinearLayout, posterLayout: View, titleView: TextView, vararg subViews: TextView?) {
        if (SettingsData.deviceType == DeviceType.TV) {
            layout.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    posterLayout.foreground = ColorDrawable(ContextCompat.getColor(context, R.color.transparent))
                    titleView.setTextColor(context.getColor(R.color.white))
                    for (subView in subViews) {
                        subView?.setTextColor(context.getColor(R.color.gray))
                    }

                    val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_in_tv)
                    v.startAnimation(anim)
                    anim.fillAfter = true
                } else {
                    posterLayout.foreground = ColorDrawable(ContextCompat.getColor(context, R.color.unselected_film))
                    titleView.setTextColor(context.getColor(R.color.unselected_title))
                    for (subView in subViews) {
                        subView?.setTextColor(context.getColor(R.color.unselected_subtitle))
                    }

                    val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_out_tv)
                    v.startAnimation(anim)
                    anim.fillAfter = true
                }
            }
        }
    }

    fun highlightButton(btn: TextView, context: Context, isHint: Boolean = false) {
        if (SettingsData.deviceType == DeviceType.TV) {
            btn.setOnFocusChangeListener { v, hasFocus ->
                if (v is TextView) {
                    val bgColorFrom: Int
                    val bgColorTo: Int
                    val textColorFrom: Int
                    val textColorTo: Int

                    if (hasFocus) {
                        bgColorFrom = ContextCompat.getColor(context, R.color.main_color_3)
                        bgColorTo = ContextCompat.getColor(context, R.color.white)
                        textColorFrom = ContextCompat.getColor(context, R.color.white)
                        textColorTo = ContextCompat.getColor(context, R.color.black)
                    } else {
                        bgColorFrom = ContextCompat.getColor(context, R.color.white)
                        bgColorTo = ContextCompat.getColor(context, R.color.main_color_3)
                        textColorFrom = ContextCompat.getColor(context, R.color.black)
                        textColorTo = ContextCompat.getColor(context, R.color.white)
                    }

                    val colorAnimationBg = ValueAnimator.ofObject(ArgbEvaluator(), bgColorFrom, bgColorTo)
                    colorAnimationBg.duration = ANIM_DURATION
                    colorAnimationBg.addUpdateListener { animator -> v.setBackgroundColor(animator.animatedValue as Int) }
                    colorAnimationBg.start()

                    val colorAnimationText = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
                    colorAnimationText.duration = ANIM_DURATION
                    colorAnimationText.addUpdateListener { animator ->
                        v.setTextColor(animator.animatedValue as Int)
                        for (drawable in v.compoundDrawables) {
                            if (drawable != null) {
                                drawable.colorFilter = PorterDuffColorFilter(animator.animatedValue as Int, PorterDuff.Mode.SRC_IN)
                            }
                        }

                        if(isHint){
                            if(v is AutoCompleteTextView){
                                v.setHintTextColor(animator.animatedValue as Int)
                            }
                        }
                    }
                    colorAnimationText.start()
                }
            }
        }
    }

    fun highlightImage(iv: ImageView, context: Context){
        if (SettingsData.deviceType == DeviceType.TV) {
            iv.setOnFocusChangeListener { v, hasFocus ->
                if (v is ImageView) {
                    val textColorFrom: Int
                    val textColorTo: Int

                    if (hasFocus) {
                        textColorFrom = ContextCompat.getColor(context, R.color.white)
                        textColorTo = ContextCompat.getColor(context, R.color.black)
                    } else {
                        textColorFrom = ContextCompat.getColor(context, R.color.black)
                        textColorTo = ContextCompat.getColor(context, R.color.white)
                    }

                    val colorAnimationText = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
                    colorAnimationText.duration = ANIM_DURATION
                    colorAnimationText.addUpdateListener { animator ->
                        v.colorFilter = PorterDuffColorFilter(animator.animatedValue as Int, PorterDuff.Mode.SRC_IN)
                    }
                    colorAnimationText.start()
                }
            }
        }
    }

    fun highlightText(btn: View, context: Context, isBackground: Boolean = false) {
        if (SettingsData.deviceType == DeviceType.TV) {
            btn.setOnFocusChangeListener { v, hasFocus ->
                if (v is TextView) {
                    val textColorFrom: Int
                    val textColorTo: Int

                    if (hasFocus) {
                        textColorFrom = ContextCompat.getColor(context, R.color.white)
                        textColorTo = ContextCompat.getColor(context, R.color.main_color_3)
                        if (isBackground) {
                            (v.getBackground() as TransitionDrawable).startTransition(ANIM_DURATION.toInt())
                        }
                    } else {
                        textColorFrom = ContextCompat.getColor(context, R.color.main_color_3)
                        textColorTo = ContextCompat.getColor(context, R.color.white)
                        if (isBackground) {
                            (v.getBackground() as TransitionDrawable).reverseTransition(ANIM_DURATION.toInt())
                        }
                    }

                    val colorAnimationText = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
                    colorAnimationText.duration = ANIM_DURATION
                    colorAnimationText.addUpdateListener { animator ->
                        v.setTextColor(animator.animatedValue as Int)
                        for (drawable in v.compoundDrawables) {
                            if (drawable != null) {
                                drawable.colorFilter = PorterDuffColorFilter(animator.animatedValue as Int, PorterDuff.Mode.SRC_IN)
                            }
                        }


                    }
                    colorAnimationText.start()
                }
            }
        }
    }
}