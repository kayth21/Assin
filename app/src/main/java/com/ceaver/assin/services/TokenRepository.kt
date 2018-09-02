package com.ceaver.assin.services

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

object TokenRepository {

    fun lookupPrice(coinmarketcapId: Int) : Double {
        val content = StringBuilder()
        val connection = URL("https://api.coinmarketcap.com/v2/ticker/$coinmarketcapId").openConnection()
        BufferedReader(InputStreamReader(connection.getInputStream())).forEachLine {
            content.append(it)
        }
        val rootJsonObject = JSONObject(content.toString())
        val dataJsonObject = rootJsonObject.getJSONObject("data")
        val quotesJsonObject = dataJsonObject.getJSONObject("quotes")
        val usdJsonObject = quotesJsonObject.getJSONObject("USD")
        return usdJsonObject.getDouble("price")
    }

    fun provideCoinmarketcapList() {
        try {
            val content = StringBuilder()
            val ur = URL("https://api.coinmarketcap.com/v2/ticker/")
            val urlConnection = ur.openConnection()
            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
            bufferedReader.forEachLine {
                content.append(it)
            }
//            applicationContext
            val rootJsonObject = JSONObject(content.toString())
            val dataJsonObject = rootJsonObject.getJSONObject("data")
            for (key in dataJsonObject.keys()) {
//                val place = Place()
//                val cryptoJsonObject = dataJsonObject.getJSONObject(key)
//                place.countryName = cryptoJsonObject.getString("name")
//                place.capitalName = cryptoJsonObject.getJSONObject("quotes").getJSONObject("USD").getString("last") + " USD"
//                countryList?.add(place)
            }
//            adapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            throw e
        }
    }
}