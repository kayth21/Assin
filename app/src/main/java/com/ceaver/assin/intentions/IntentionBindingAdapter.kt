package com.ceaver.assin.intentions

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ceaver.assin.extensions.asCurrencyString
import com.ceaver.assin.extensions.asPercentString
import com.ceaver.assin.extensions.asQuantityString
import com.ceaver.assin.extensions.setInactive

@BindingAdapter("intentionBaseImageResource")
fun ImageView.setIntentionBaseImageResource(intention: Intention) {
    setImageResource(intention.baseTitle.getIcon())
    setInactive(!intention.active)
}

@BindingAdapter("intentionQuoteImageResource")
fun ImageView.setIntentionQuoteImageResource(intention: Intention) {
    setImageResource(intention.quoteTitle.getIcon())
    setInactive(!intention.active)
}

@BindingAdapter("intentionTitleText")
fun TextView.setIntentionTitleText(intention: Intention) {
    val type = if (intention.type == IntentionType.BUY) "Buy " else "Sell "
    val quantity = intention.quantity?.asQuantityString()?.plus(" ") ?: ""
    val base = "${intention.baseTitle.symbol} (${intention.baseTitle.name})"
    text = type + quantity + base
}

@BindingAdapter("intentionSubtitleText")
fun TextView.setIntentionSubtitleText(intention: Intention) {
    text = "Target Price: ${intention.target.asCurrencyString(intention.quoteTitle)}"
}

@BindingAdapter("intentionPercentText")
fun TextView.setIntentionPercentText(intention: Intention) {
    text = "${intention.factorToReferencePrice.asPercentString()}"
}