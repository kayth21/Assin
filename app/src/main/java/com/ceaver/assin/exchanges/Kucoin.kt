package com.ceaver.assin.exchanges

import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.markets.MarketRepository
import com.ceaver.assin.markets.Title
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection

object Kucoin {

    fun update(symbol: Symbol) {
        val url = buildRequestUrl(symbol)
        val connection = url.openConnection()
        val jsonObject = callExchange(connection)
        val resultObject = jsonObject.getJSONObject("data")
        val last = resultObject.getDouble("lastDealPrice")
        val open = ((resultObject.getDouble("changeRate")/100) +1) * last // TODO check that, what's change rate?
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
        val symbolA = if (symbol == Symbol.BTC) symbol.name.toUpperCase() else symbol.name.toUpperCase()
        val symbolB = if (symbol == Symbol.BTC) Symbol.USD.name.toUpperCase() else Symbol.BTC.name.toUpperCase()
        return URL("https://api.kucoin.com/v1/open/tick?symbol=$symbolA-$symbolB")
    }
}