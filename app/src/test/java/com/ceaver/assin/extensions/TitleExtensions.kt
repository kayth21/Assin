package com.ceaver.assin.extensions

import com.ceaver.assin.markets.CryptoTitle
import com.ceaver.assin.markets.Title
import java.time.LocalDateTime
import kotlin.random.Random

fun CryptoTitle.Factory.fromTestdata(
        id: String = "crypto_BTC",
        symbol: String = "BTC",
        name: String = "Bitcoin",
        active: Boolean = true,
        rank: Int = 1,
        circulatingSupply: Long = Random.nextLong(),
        totalSupply: Long = Random.nextLong(),
        maxSupply: Long = Random.nextLong(),
        betaValue: Double = Random.nextDouble(),
        lastUpdated: LocalDateTime = LocalDateTime.now(),
        fiatPrice: Double = Random.nextDouble(),
        cryptoPrice: Double = Random.nextDouble()
): CryptoTitle {
    return CryptoTitle(
            id = id,
            symbol = symbol,
            name = name,
            active = if (active) 75 else -75, // TODO move this active/inactive counter logic to an own class and use it from here as well
            rank = rank,
            circulatingSupply = circulatingSupply,
            totalSupply = totalSupply,
            maxSupply = maxSupply,
            betaValue = betaValue,
            lastUpdated = lastUpdated,
            cryptoQuotes = Title.Quotes(fiatPrice), // TODO create a extension factory method with valid values
            fiatQuotes = Title.Quotes(cryptoPrice) // TODO create a extension factory method with valid values
    )
}