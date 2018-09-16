package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.markets.MarketValuation
import com.ceaver.assin.markets.Title
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection

object Bittrex {

    fun update(symbol: Symbol) {
        val url = buildRequestUrl(symbol)
        val connection = url.openConnection()
        val jsonObject = callExchange(connection)
        val resultObject = jsonObject.getJSONArray("result").getJSONObject(0)
        val last = resultObject.getDouble("Last")
        val open = resultObject.getDouble("PrevDay")
        val unit = if (symbol == Symbol.BTC) Symbol.USD else Symbol.BTC
        MarketValuation.update(Title( symbol, last, open, unit))
    }


    private fun callExchange(connection: URLConnection): JSONObject {
        val content = StringBuilder()
        BufferedReader(InputStreamReader(connection.getInputStream())).forEachLine {
            content.append(it)
        }
        return JSONObject(content.toString())
    }

    private fun buildRequestUrl(symbol: Symbol): URL {
        val symbolA = if (symbol == Symbol.BTC) symbol.name.toUpperCase() else symbol.name.toUpperCase()
        val symbolB = if (symbol == Symbol.BTC) Symbol.USD.name.toUpperCase() else Symbol.BTC.name.toUpperCase()
        return URL("https://bittrex.com/api/v1.1/public/getmarketsummary?market=$symbolB-$symbolA")
    }
}