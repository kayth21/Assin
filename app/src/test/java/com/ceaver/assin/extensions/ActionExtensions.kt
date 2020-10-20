package com.ceaver.assin.extensions

import com.ceaver.assin.action.*
import com.ceaver.assin.markets.CryptoTitle
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.Position
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
        valueCrypto: BigDecimal = 101.toBigDecimal(),
        valueFiat: BigDecimal = 1001.toBigDecimal(),
        comment: String = "Coinbase",
        sourcePosition: Position
): Withdraw {
    return Withdraw(
            id = id,
            date = date,
            valueCrypto = valueCrypto,
            valueFiat = valueFiat,
            comment = comment,
            sourcePositionId = sourcePosition.id,
            sourcePosition = sourcePosition
    )
}

fun Split.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now().plusDays(2),
        quantity: BigDecimal,
        comment: String = "Coinbase",
        splitPosition: Position
): Split {
    return Split(
            id = id,
            date = date,
            quantity = quantity,
            comment = comment,
            sourcePositionId = splitPosition.id,
            sourcePosition = splitPosition
    )
}

fun Trade.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now(),
        buyTitle: Title = CryptoTitle.fromTestdata(id = "crypto_ETH", symbol = "ETH", name = "Ethereum", rank = 2),
        buyLabel: String? = "Trading",
        buyQuantity: BigDecimal = BigDecimal.TEN,
        valueCrypto: BigDecimal = BigDecimal.ONE,
        valueFiat: BigDecimal = 10000.toBigDecimal(),
        comment: String = "Coinbase",
        sellPosition: Position
): Trade {
    return Trade(
            id = id,
            date = date,
            buyTitle = buyTitle,
            buyLabel = buyLabel,
            buyQuantity = buyQuantity,
            valueFiat = valueFiat,
            valueCrypto = valueCrypto,
            comment = comment,
            sellPositionId = sellPosition.id,
            sellPosition = sellPosition
    )
}

fun Merge.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now(),
        valueFiat: BigDecimal = 200.toBigDecimal(),
        valueCrypto: BigDecimal = 2000.toBigDecimal(),
        comment: String = "Coinbase",
        mergePositionA: Position,
        mergePositionB: Position
): Merge {
    return Merge(
            id = id,
            date = date,
            valueFiat = valueFiat,
            valueCrypto = valueCrypto,
            comment = comment,
            sourcePositionIdA = mergePositionA.id,
            sourcePositionIdB = mergePositionB.id,
            sourcePositionA = mergePositionA,
            sourcePositionB = mergePositionB
    )
}

fun Move.Factory.fromTestdata(
        id: Long = Random.nextLong(),
        date: LocalDate = LocalDate.now(),
        targetLabel: String? = "Savings",
        movePosition: Position,
        comment: String = "Coinbase"
): Move {
    return Move(
            id = id,
            date = date,
            targetLabel = targetLabel,
            sourcePositionId = movePosition.id,
            sourcePosition = movePosition,
            comment = comment
    )
}