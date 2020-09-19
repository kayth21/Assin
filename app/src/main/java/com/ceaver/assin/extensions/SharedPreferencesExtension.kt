package com.ceaver.assin.extensions

import android.content.SharedPreferences
import com.ceaver.assin.markets.Title
import com.google.gson.Gson

fun SharedPreferences.getString(key: String): String {
    return if (contains(key)) getString(key, "")!! else throw IllegalStateException()
}

fun SharedPreferences.getTitle(key: String) : Title {
    val json = getString(key)
    return Gson().fromJson(json, Title::class.java)
}

fun SharedPreferences.Editor.setTitle(key: String, title: Title): SharedPreferences.Editor {
    return putString(key, Gson().toJson(title))
}