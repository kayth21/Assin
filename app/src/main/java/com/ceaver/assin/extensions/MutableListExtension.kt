package com.ceaver.assin.extensions

fun <T> MutableList<T>.replace(origin: T, replace: T) {
    set(indexOf(origin), replace)
}