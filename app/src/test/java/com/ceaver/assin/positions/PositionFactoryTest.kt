package com.ceaver.assin.positions

import com.ceaver.assin.action.*
import com.ceaver.assin.extensions.fromTestdata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PositionFactoryTest {

    @Test
    @DisplayName("Zero actions lead to zero position")
    fun zeroActions() {
        // arrange
        val actions = emptyList<Action>()
        // act
        val result = PositionFactory.fromActions(actions)
        // assert
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("Deposit action lead in open position")
    fun depositAction() {
        // arrange
        val deposit = Deposit.fromTestdata()
        // act
        val positions = PositionFactory.fromActions(listOf(deposit))
        // assert
        assertThat(positions).hasSize(1)
        assertThatPosition(positions[0])
                .hasId(BigDecimal.ONE)
                .hasPosition(deposit.quantity, deposit.title, deposit.label)
                .hasOpenValues(deposit.date, deposit.valueFiat, deposit.valueCrypto)
                .isNotClosed()
    }

    @Test
    @DisplayName("Withdraw action lead in closing origin position")
    fun withdrawAction() {
        // arrange
        val deposit = Deposit.fromTestdata()
        val withdraw = Withdraw.fromTestdata(quantity = deposit.quantity, title = deposit.title, label = deposit.label, positionId = BigDecimal.ONE)
        // act
        val positions = PositionFactory.fromActions(listOf(deposit, withdraw))
        // assert
        assertThat(positions).hasSize(1)
        assertThatPosition(positions[0])
                .hasId(BigDecimal.ONE)
                .hasPosition(deposit.quantity, deposit.title, deposit.label)
                .hasOpenValues(deposit.date, deposit.valueFiat, deposit.valueCrypto)
                .hasCloseValues(withdraw.date, withdraw.valueFiat, withdraw.valueCrypto)
    }

    @Test
    @DisplayName("Split action lead in removing origin position and two new open positions")
    fun splitAction() {
        // arrange
        val deposit = Deposit.fromTestdata(quantity = 10.toBigDecimal(), valueFiat = 1000.toBigDecimal(), valueCrypto = 100.toBigDecimal())
        val split = Split.fromTestdata(quantity = 4.toBigDecimal(), remaining = 6.toBigDecimal(), title = deposit.title, label = deposit.label, positionId = BigDecimal.ONE)
        // act
        val positions = PositionFactory.fromActions(listOf(deposit, split))
        // assert
        assertThat(positions).hasSize(2)
        assertThatPosition(positions[0])
                .hasId(1.1.toBigDecimal())
                .hasPosition(4.toBigDecimal(), deposit.title, deposit.label)
                .hasOpenValues(deposit.date, 400.toBigDecimal(), 40.toBigDecimal())
                .isNotClosed()
        assertThatPosition(positions[1])
                .hasId(1.2.toBigDecimal())
                .hasPosition(6.toBigDecimal(), deposit.title, deposit.label)
                .hasOpenValues(deposit.date, 600.toBigDecimal(), 60.toBigDecimal())
                .isNotClosed()
    }

    @Test
    @DisplayName("Trade action lead in closed origin position and new open position")
    fun tradeAction() {
        // arrange
        val deposit = Deposit.fromTestdata()
        val trade = Trade.fromTestdata(sellQuantity = deposit.quantity, sellTitle = deposit.title, sellLabel = deposit.label, positionId = BigDecimal.ONE)
        // act
        val positions = PositionFactory.fromActions(listOf(deposit, trade))
        // assert
        assertThat(positions).hasSize(2)
        assertThatPosition(positions[0])
                .hasId(BigDecimal.ONE)
                .hasPosition(deposit.quantity, deposit.title, deposit.label)
                .hasOpenValues(deposit.date, deposit.valueFiat, deposit.valueCrypto)
                .hasCloseValues(trade.date, trade.valueFiat, trade.valueCrypto)
        assertThatPosition(positions[1])
                .hasId(2.toBigDecimal())
                .hasPosition(trade.buyQuantity, trade.buyTitle, trade.buyLabel)
                .hasOpenValues(trade.date, trade.valueFiat, trade.valueCrypto)
                .isNotClosed()
    }
}