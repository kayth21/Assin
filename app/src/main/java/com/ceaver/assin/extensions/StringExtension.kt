package com.ceaver.assin.extensions

import java.util.*

fun String.isLong(): Boolean {
    return this.toLongOrNull() != null
}

fun String.isInt(): Boolean {
    return this.toIntOrNull() != null
}

fun String.isDouble(): Boolean {
    return this.toDoubleOrNull() != null
}

fun String.toOptionalDouble(): Optional<Double> {
    return if(this.isDouble()) Optional.of(this.toDouble()) else Optional.empty()
}