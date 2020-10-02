package com.ceaver.assin.markets

import android.os.Parcelable
import java.time.LocalDateTime

interface Title : Parcelable{
    val id: String
    val symbol: String
    val name: String
    val lastUpdated: LocalDateTime
    val cryptoQuotes: Quotes
    val fiatQuotes: Quotes
    fun getIcon(): Int
    fun toEntity(): TitleEntity
    fun getPercentChange1hString() : String
    fun getPercentChange24hString() : String
    fun getPercentChange7dString() : String
}