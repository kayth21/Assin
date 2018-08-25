package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Symbol
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection

object Bitstamp {

    fun update(symbol: Symbol) {
        val url = buildRequestUrl(symbol)
        val connection = url.openConnection()
        val jsonObject = callExchange(connection)
        val last = jsonObject.getDouble("last")
        val open = jsonObject.getDouble("open")
        symbol.updateLastBtc(if (symbol == Symbol.BTC) 1.0 else last)
        symbol.updateLastUsd(if (symbol == Symbol.BTC) last else last * Symbol.BTC.loadLastUsd())
        symbol.updateOpenBtc(if (symbol == Symbol.BTC) 1.0 else open)
        symbol.updateOpenUsd(if (symbol == Symbol.BTC) open else open * Symbol.BTC.loadOpenUsd())
    }


    private fun callExchange(connection: URLConnection): JSONObject {
        val content = StringBuilder()
        BufferedReader(InputStreamReader(connection.getInputStream())).forEachLine {
            content.append(it)
        }
        return JSONObject(content.toString())
    }

    private fun buildRequestUrl(symbol: Symbol): URL {
        val symbolA = if (symbol == Symbol.BTC) symbol.name.toLowerCase() else symbol.name.toLowerCase()
        val symbolB = if (symbol == Symbol.BTC) Symbol.USD.name.toLowerCase() else Symbol.BTC.name.toLowerCase()
        val url = URL("https://www.bitstamp.net/api/v2/ticker/$symbolA$symbolB")
        return url
    }
}