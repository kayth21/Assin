package com.ceaver.assin.common

import android.view.View
import android.widget.AdapterView

class SpinnerSelectionListener(private val listener: () -> Unit) : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        kotlin.run(listener)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        throw IllegalStateException()
    }
}