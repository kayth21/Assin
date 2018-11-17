package com.ceaver.assin.markets

import com.ceaver.assin.assets.Category
import com.ceaver.assin.extensions.isDouble
import com.ceaver.assin.extensions.isInt
import com.ceaver.assin.extensions.isLong
import com.ceaver.assin.extensions.toOptionalDouble
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

object Coinpaprika {

    private val tickerUrl = "https://api.coinpaprika.com/v1/ticker"

    fun load(id: String): Optional<Title> {
        val connection = URL("$tickerUrl/id").openConnection()
        val result = callExchange(connection)
        val jsonObject = JSONObject(result)
        return transform(jsonObject)
    }

    fun loadAllTitles(): Set<Title> {
        val connection = URL(tickerUrl).openConnection()
        val result = callExchange(connection)
        val jsonObject = JSONArray(result)

        val resultSet = mutableSetOf<Title>()
        for (i in 0 until jsonObject.length()) {
            val titleOptional = transform(jsonObject.getJSONObject(i))
            titleOptional.ifPresent { resultSet.add(titleOptional.get()) }
        }
        return resultSet
    }

    private fun callExchange(connection: URLConnection): String {
        val content = StringBuilder()
        BufferedReader(InputStreamReader(connection.getInputStream())).forEachLine {content.append(it) }
        return content.toString()
    }

    private fun transform(row: JSONObject): Optional<Title> {
        val id = row.getString("id")
        val name = row.getString("name")
        val rank = row.getString("rank")
        val symbol = row.getString("symbol")
        val priceUsd = row.getString("price_usd")
        val priceBtc = row.getString("price_btc")
        // val volume24hUsd = row.getString("volume_24h_usd")
        val marketCapUsd = row.getString("market_cap_usd")
        // val circulatingSupply = row.getString("circulating_supply")
        // val totalSupply = row.getString("total_supply")
        // val maxSupply = row.getString("max_supply")
        val percentChange1h = row.getString("percent_change_1h")
        val percentChange24h = row.getString("percent_change_24h")
        val percentChange7d = row.getString("percent_change_7d")
        val lastUpdated = row.getString("last_updated")

        if (id.isNullOrBlank()) {
            return Optional.empty()
        }

        if (name.isNullOrBlank() || symbol.isNullOrBlank()) {
            // println("$name ($symbol) Title will be ignored, because of missing name and/or symbol")
            return Optional.empty()
        }

        if (priceUsd.isNullOrBlank() || !priceUsd.isDouble() || priceBtc.isNullOrBlank() || !priceBtc.isDouble()) {
            // println("$name ($symbol) will be ignored, because of missing or invalid usd and/or btc price information ($priceUsd, $priceBtc)")
            return Optional.empty()
        }

        if (rank.isNullOrBlank() || !rank.isInt()) {
            // println("$name ($symbol) will be ignored, because of missing or invalid ranking information ($rank)")
            return Optional.empty()
        }

        if (marketCapUsd.isNullOrBlank() || !marketCapUsd.isLong()) {
            // println("$name ($symbol) will be ignored, because of missing or invalid market cap information ($marketCapUsd)")
            return Optional.empty()
        }

        if (lastUpdated.isNullOrBlank() || !lastUpdated.isLong() || Duration.between(LocalDateTime.now(), transformTimestamp(lastUpdated.toLong())).toDays() > 2) {
            // println("$name ($symbol) will be ignored, because of missing, invalid or outdated lastUpdate information (${transform(lastUpdated.toLong())})")
            return Optional.empty()
        }

        return Optional.of(Title(id, symbol, Category.CRYPTO, name, priceUsd.toDouble(), priceBtc.toDouble(), marketCapUsd.toLong(), rank.toInt(), percentChange1h.toOptionalDouble(), percentChange24h.toOptionalDouble(), percentChange7d.toOptionalDouble(), transformTimestamp(lastUpdated.toLong())))
    }

    private fun transformTimestamp(lastUpdated: Long) = LocalDateTime.ofInstant(Instant.ofEpochSecond(lastUpdated), TimeZone.getDefault().toZoneId())
}