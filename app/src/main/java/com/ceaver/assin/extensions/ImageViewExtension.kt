package com.ceaver.assin.extensions

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView

fun ImageView.setInactive(inactive: Boolean) {
    if (inactive) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)  //0 means grayscale
        val cf = ColorMatrixColorFilter(matrix)
        colorFilter = cf
        imageAlpha = 128   // 128 = 0.5
    } else {
        colorFilter = null
        imageAlpha = 255
    }
}