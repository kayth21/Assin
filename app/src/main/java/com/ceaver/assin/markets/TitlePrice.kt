package com.ceaver.assin.markets

import com.ceaver.assin.preferences.Preferences

object TitlePrice {

    fun lookupPrice(baseTitle: Title, quoteTitle: Title): Double {
        return when {
            quoteTitle.symbol == Preferences.getFiatTitleSymbol() ->
                baseTitle.fiatQuotes.price
            quoteTitle.symbol == Preferences.getCryptoTitleSymbol() ->
                baseTitle.cryptoQuotes.price
            baseTitle::class == quoteTitle::class ->
                baseTitle.fiatQuotes.price / quoteTitle.fiatQuotes.price
            else ->
                TODO("no implementation for market pair ${baseTitle.symbol}/${quoteTitle.symbol}.")

        }
    }
}