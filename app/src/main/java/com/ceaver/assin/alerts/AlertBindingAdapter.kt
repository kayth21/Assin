package com.ceaver.assin.alerts

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.extensions.setInactive

@BindingAdapter("alertImageResource")
fun ImageView.setAlertImageResource(alert: Alert) {
    setImageResource(alert.getImageResource())
    setInactive(!alert.active)
}
