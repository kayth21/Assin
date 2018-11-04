package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.markets.MarketRepository
import com.ceaver.assin.markets.Title
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
        val reference = if (symbol == Symbol.BTC) Symbol.USD else Symbol.BTC
        MarketRepository.update(Title( symbol, last, open, reference))
        MarketRepository.update(Title( reference, 1/last, 1/open, symbol))
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