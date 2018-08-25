package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Symbol
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

object Bitstamp {

    fun update(symbol: Symbol) {
        val price = lookup(symbol)
        symbol.updateBtcPrice(if (symbol == Symbol.BTC) 1.0 else price)
        symbol.updateUsdPrice(if (symbol == Symbol.BTC) price else price * Symbol.BTC.loadUsdPrice())
    }

    private fun lookup(symbol: Symbol): Double {
        val content = StringBuilder()
        val symbolA = if(symbol == Symbol.BTC) symbol.name.toLowerCase() else symbol.name.toLowerCase()
        val symbolB = if(symbol == Symbol.BTC) Symbol.USD.name.toLowerCase() else Symbol.BTC.name.toLowerCase()
        val connection = URL("https://www.bitstamp.net/api/v2/ticker/$symbolA$symbolB").openConnection()

        BufferedReader(InputStreamReader(connection.getInputStream())).forEachLine {
            content.append(it)
        }
        val rootJsonObject = JSONObject(content.toString())
        val last = rootJsonObject.getDouble("last")
        return last
    }
}