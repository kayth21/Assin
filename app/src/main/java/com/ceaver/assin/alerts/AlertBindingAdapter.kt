package com.ceaver.assin.alerts

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.extensions.setInactive

@BindingAdapter("alertBaseImageResource")
fun ImageView.setAlertBaseImageResource(alert: Alert) {
    setImageResource(alert.getBaseImageResource())
    setInactive(!alert.active)
}

@BindingAdapter("alertQuoteImageResource")
fun ImageView.setAlertQuoteImageResource(alert: Alert) {
    setImageResource(alert.getQuoteImageResource())
    setInactive(!alert.active)
}