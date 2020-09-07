package com.ceaver.assin.action

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.util.CalendarHelper

@BindingAdapter("actionSourceImage")
fun ImageView.setActionSourceImage(action: Action) {
    setImageResource(action.getLeftImageResource())
}

@BindingAdapter("actionTargetImage")
fun ImageView.setActionTargetImage(action: Action) {
    setImageResource(action.getRightImageResource())
}

@BindingAdapter("actionDate")
fun TextView.setActionDate(action: Action) {
    text = CalendarHelper.convertDate(action.getActionDate())
}