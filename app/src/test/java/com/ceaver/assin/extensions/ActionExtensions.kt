package com.ceaver.assin.extensions

import com.ceaver.assin.action.*
import com.ceaver.assin.markets.CryptoTitle
import com.ceaver.assin.markets.Title
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random

fun Deposit.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now(),
        title: Title = CryptoTitle.fromTestdata(),
        label: String? = "Savings",
        quantity: BigDecimal = 10.toBigDecimal(),
        valueFiat: BigDecimal = 100.toBigDecimal(),
        valueCrypto: BigDecimal = 1000.toBigDecimal(),
        comment: String = "Coinbase"
): Deposit {
    return Deposit(id = id, date = date, title = title, label = label, quantity = quantity, valueCrypto = valueCrypto, valueFiat = valueFiat, comment = comment)
}

fun Withdraw.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now().plusDays(1),
        title: Title,
        label: String?,
        quantity: BigDecimal,
        valueCrypto: BigDecimal = 101.toBigDecimal(),
        valueFiat: BigDecimal = 1001.toBigDecimal(),
        comment: String = "Coinbase",
        positionId: BigDecimal
): Withdraw {
    return Withdraw(
            id = id,
            date = date,
            title = title,
            label = label,
            quantity = quantity,
            valueCrypto = valueCrypto,
            valueFiat = valueFiat,
            comment = comment,
            positionId = positionId
    )
}

fun Split.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now().plusDays(2),
        title: Title,
        label: String?,
        quantity: BigDecimal,
        remaining: BigDecimal,
        comment: String = "Coinbase",
        positionId: BigDecimal
): Split {
    return Split(
            id = id,
            date = date,
            title = title,
            label = label,
            quantity = quantity,
            remaining = remaining,
            comment = comment,
            positionId = positionId
    )
}

fun Trade.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now(),
        buyTitle: Title = CryptoTitle.fromTestdata(id = "crypto_ETH", symbol = "ETH", name = "Ethereum", rank = 2),
        buyLabel: String? = "Trading",
        buyQuantity: BigDecimal = BigDecimal.TEN,
        sellTitle: Title,
        sellLabel: String?,
        sellQuantity: BigDecimal,
        valueCrypto: BigDecimal = BigDecimal.ONE,
        valueFiat: BigDecimal = 10000.toBigDecimal(),
        comment: String = "Coinbase",
        positionId: BigDecimal
): Trade {
    return Trade(
            id = id,
            date = date,
            buyTitle = buyTitle,
            buyLabel = buyLabel,
            buyQuantity = buyQuantity,
            sellTitle = sellTitle,
            sellLabel = sellLabel,
            sellQuantity = sellQuantity,
            valueFiat = valueFiat,
            valueCrypto = valueCrypto,
            comment = comment,
            positionId = positionId
    )
}

fun Merge.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now(),
        title: Title = CryptoTitle.fromTestdata(),
        label: String? = "Savings",
        quantityPartA: BigDecimal = 10.toBigDecimal(),
        quantityPartB: BigDecimal = 10.toBigDecimal(),
        valueFiat: BigDecimal = 200.toBigDecimal(),
        valueCrypto: BigDecimal = 2000.toBigDecimal(),
        comment: String = "Coinbase",
        sourcePositionA: BigDecimal,
        sourcePositionB: BigDecimal
): Merge {
    return Merge(
            id = id,
            date = date,
            title = title,
            label = label,
            quantityPartA = quantityPartA,
            quantityPartB = quantityPartB,
            valueFiat = valueFiat,
            valueCrypto = valueCrypto,
            comment = comment,
            sourcePositionA = sourcePositionA,
            sourcePositionB = sourcePositionB
    )
}