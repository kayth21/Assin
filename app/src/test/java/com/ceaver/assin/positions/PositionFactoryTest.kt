package com.ceaver.assin.positions

import com.ceaver.assin.action.*
import com.ceaver.assin.extensions.fromTestdata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PositionFactoryTest {

    private lateinit var positions: List<Position>

    @Nested
    @DisplayName("Zero actions lead in:")
    inner class EmptyActionList {

        @BeforeEach
        fun setup() {
            positions = PositionFactory.fromActions(listOf())
        }

        @Test
        @DisplayName("empty position result list")
        fun emptyResultList() {
            assertThat(positions).isEmpty()
        }
    }

    @Nested
    @DisplayName("Deposit action lead in:")
    inner class DepositAction {

        private lateinit var deposit: Deposit

        @BeforeEach
        fun setup() {
            deposit = Deposit.fromTestdata()
            positions = PositionFactory.fromActions(listOf(deposit))
        }

        @Test
        @DisplayName("position result list of size: 1")
        fun resultListSize() {
            assertThat(positions).hasSize(1)
        }

        @Nested
        @DisplayName("open position with:")
        inner class OpenPosition {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(1)
            }

            @Test
            @DisplayName("position data of deposit action")
            fun positionData() {
                assertThatPosition(position).hasPosition(deposit.quantity, deposit.title, deposit.label)
            }

            @Test
            @DisplayName("open values of deposit action")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, deposit.valueFiat, deposit.valueCrypto)
            }

            @Test
            @DisplayName("no closing data")
            fun closingData() {
                assertThatPosition(position).hasNoCloseValues()
            }
        }
    }

    @Nested
    @DisplayName("Withdraw action lead in:")
    inner class WithdrawAction {

        private lateinit var deposit: Deposit
        private lateinit var withdraw: Withdraw

        @BeforeEach
        fun setup() {
            deposit = Deposit.fromTestdata(valueFiat = 1000.toBigDecimal(), valueCrypto = 100.toBigDecimal())
            withdraw = Withdraw.fromTestdata(sourcePosition = Position.fromDeposit(1, deposit), valueFiat = 2000.toBigDecimal(), valueCrypto = 200.toBigDecimal())
            positions = PositionFactory.fromActions(listOf(deposit, withdraw))
        }

        @Test
        @DisplayName("result list of size: 1")
        fun resultListSize() {
            assertThat(positions).hasSize(1)
        }

        @Nested
        @DisplayName("closed position with:")
        inner class ClosedPosition {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(1)
            }

            @Test
            @DisplayName("position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(withdraw.sourcePosition!!.quantity, withdraw.sourcePosition!!.title, withdraw.sourcePosition!!.label)
            }

            @Test
            @DisplayName("open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(withdraw.sourcePosition!!.open.date, withdraw.sourcePosition!!.open.valueFiat, withdraw.sourcePosition!!.open.valueCrypto)
            }

            @Test
            @DisplayName("close values of withdraw action")
            fun closingData() {
                assertThatPosition(position).hasCloseValues(withdraw.date, withdraw.valueFiat, withdraw.valueCrypto)
            }
        }
    }

    @Nested
    @DisplayName("Trade action lead in:")
    inner class TradeAction {

        private lateinit var deposit: Deposit
        private lateinit var trade: Trade

        @BeforeEach
        fun setup() {
            deposit = Deposit.fromTestdata(valueFiat = 1000.toBigDecimal(), valueCrypto = 10.toBigDecimal())
            trade = Trade.fromTestdata(sellPosition = Position.fromDeposit(1, deposit), valueFiat = 2000.toBigDecimal(), valueCrypto = 20.toBigDecimal())
            positions = PositionFactory.fromActions(listOf(deposit, trade))
        }

        @Test
        @DisplayName("result list of size: 2")
        fun resultListSize() {
            assertThat(positions).hasSize(2)
        }

        @Nested
        @DisplayName("closed position (0) with:")
        inner class ClosedPosition0 {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(1)
            }

            @Test
            @DisplayName("position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(deposit.quantity, deposit.title, deposit.label)
            }

            @Test
            @DisplayName("open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, deposit.valueFiat, deposit.valueCrypto)
            }

            @Test
            @DisplayName("close values of trade action")
            fun closingData() {
                assertThatPosition(position).hasCloseValues(trade.date, trade.valueFiat, trade.valueCrypto)
            }
        }

        @Nested
        @DisplayName("open position (1) with:")
        inner class OpenPosition1 {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[1]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(2)
            }

            @Test
            @DisplayName("position data of trade action")
            fun positionData() {
                assertThatPosition(position).hasPosition(trade.buyQuantity, trade.buyTitle, trade.buyLabel)
            }

            @Test
            @DisplayName("open values of trade action")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(trade.date, trade.valueFiat, trade.valueCrypto)
            }

            @Test
            @DisplayName("no closing data")
            fun closingData() {
                assertThatPosition(position).hasNoCloseValues()
            }
        }
    }


    @Nested
    @DisplayName("Split action (40/60) on open position lead in:")
    inner class SplitActionOnOpenPosition {

        private lateinit var deposit: Deposit
        private lateinit var split: Split

        @BeforeEach
        fun setup() {
            deposit = Deposit.fromTestdata(quantity = 10.toBigDecimal(), valueFiat = 1000.toBigDecimal(), valueCrypto = 100.toBigDecimal())
            split = Split.fromTestdata(quantity = 4.toBigDecimal(), splitPosition = Position.fromDeposit(1, deposit))
            positions = PositionFactory.fromActions(listOf(deposit, split))
        }

        @Test
        @DisplayName("result list of size: 2")
        fun resultListSize() {
            assertThat(positions).hasSize(2)
        }

        @Nested
        @DisplayName("adding open position (0) with:")
        inner class OpenPosition0 {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(2)
            }

            @Test
            @DisplayName("40% of position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(4.toBigDecimal(), deposit.title, deposit.label)
            }

            @Test
            @DisplayName("40% of open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, 400.toBigDecimal(), 40.toBigDecimal())
            }

            @Test
            @DisplayName("no closing data")
            fun closingData() {
                assertThatPosition(position).hasNoCloseValues()
            }
        }

        @Nested
        @DisplayName("adding open position (1) with:")
        inner class OpenPosition1 {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[1]
            }

            @Test
            @DisplayName("id equals inverted index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(2.inv())
            }

            @Test
            @DisplayName("60% of position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(6.toBigDecimal(), deposit.title, deposit.label)
            }

            @Test
            @DisplayName("60% of open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, 600.toBigDecimal(), 60.toBigDecimal())
            }

            @Test
            @DisplayName("no closing data")
            fun closingData() {
                assertThatPosition(position).hasNoCloseValues()
            }
        }
    }

    @Nested
    @DisplayName("Split action (40/60) on closed position lead in:")
    inner class SplitActionOnClosedPosition {

        private lateinit var deposit: Deposit
        private lateinit var withdraw: Withdraw
        private lateinit var split: Split

        @BeforeEach
        fun setup() {
            deposit = Deposit.fromTestdata(quantity = 10.toBigDecimal(), valueFiat = 1000.toBigDecimal(), valueCrypto = 100.toBigDecimal())
            withdraw = Withdraw.fromTestdata(sourcePosition = Position.fromDeposit(1, deposit), valueFiat = 2000.toBigDecimal(), valueCrypto = 200.toBigDecimal())
            split = Split.fromTestdata(quantity = 4.toBigDecimal(), splitPosition = Position.fromDeposit(1, deposit))
            positions = PositionFactory.fromActions(listOf(deposit, withdraw, split))
        }

        @Test
        @DisplayName("result list of size: 2")
        fun resultListSize() {
            assertThat(positions).hasSize(2)
        }

        @Nested
        @DisplayName("closed position (0) with:")
        inner class Position0With {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(3)
            }

            @Test
            @DisplayName("40% of position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(4.toBigDecimal(), deposit.title, deposit.label)
            }

            @Test
            @DisplayName("40% of open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, 400.toBigDecimal(), 40.toBigDecimal())
            }

            @Test
            @DisplayName("40% of close values of source position")
            fun closingData() {
                assertThatPosition(position).hasCloseValues(withdraw.date, 800.toBigDecimal(), 80.toBigDecimal())
            }
        }

        @Nested
        @DisplayName("closed position (1) with:")
        inner class Position1With {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[1]
            }

            @Test
            @DisplayName("id equals inverted index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(3.inv())
            }

            @Test
            @DisplayName("60% of position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(6.toBigDecimal(), deposit.title, deposit.label)
            }

            @Test
            @DisplayName("60% of open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, 600.toBigDecimal(), 60.toBigDecimal())
            }

            @Test
            @DisplayName("60% of close values of source position")
            fun closingData() {
                assertThatPosition(position).hasCloseValues(withdraw.date, 1200.toBigDecimal(), 120.toBigDecimal())
            }
        }
    }

    @Nested
    @DisplayName("Merge action (40/60) lead in:")
    inner class MergeAction {

        private lateinit var deposit1: Deposit
        private lateinit var deposit2: Deposit
        private lateinit var merge: Merge

        @BeforeEach
        fun setup() {
            deposit1 = Deposit.fromTestdata(quantity = 40.toBigDecimal())
            deposit2 = Deposit.fromTestdata(quantity = 60.toBigDecimal(), title = deposit1.title, label = deposit1.label)
            merge = Merge.fromTestdata(valueFiat = 20000.toBigDecimal(), valueCrypto = 200.toBigDecimal(), mergePositionA = Position.fromDeposit(1, deposit1), mergePositionB = Position.fromDeposit(2, deposit2))
            positions = PositionFactory.fromActions(listOf(deposit1, deposit2, merge))
        }

        @Test
        @DisplayName("result list of size: 3")
        fun resultListSize() {
            assertThat(positions).hasSize(3)
        }

        @Nested
        @DisplayName("closed position (0) with:")
        inner class ClosedPosition0 {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(1)
            }

            @Test
            @DisplayName("position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(deposit1.quantity, deposit1.title, deposit1.label)
            }

            @Test
            @DisplayName("open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit1.date, deposit1.valueFiat, deposit1.valueCrypto)
            }

            @Test
            @DisplayName("40% from close values of trade action")
            fun closingData() {
                assertThatPosition(position).hasCloseValues(merge.date, 8000.toBigDecimal(), 80.toBigDecimal())
            }
        }

        @Nested
        @DisplayName("closed position (1) with:")
        inner class ClosedPosition1 {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[1]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(2)
            }

            @Test
            @DisplayName("position data of source position")
            fun positionData() {
                assertThatPosition(position).hasPosition(deposit2.quantity, deposit2.title, deposit2.label)
            }

            @Test
            @DisplayName("open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit2.date, deposit2.valueFiat, deposit2.valueCrypto)
            }

            @Test
            @DisplayName("60% from close values of trade action")
            fun closingData() {
                assertThatPosition(position).hasCloseValues(merge.date, 12000.toBigDecimal(), 120.toBigDecimal())
            }
        }

        @Nested
        @DisplayName("open position (2) with:")
        inner class OpenPosition2 {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[2]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(3)
            }

            @Test
            @DisplayName("position data of accumulated source positions")
            fun positionData() {
                assertThatPosition(position).hasPosition(deposit1.quantity + deposit2.quantity, deposit1.title, deposit1.label)
            }

            @Test
            @DisplayName("open values of merge action")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(merge.date, merge.valueFiat, merge.valueCrypto)
            }

            @Test
            @DisplayName("no closing data")
            fun closingData() {
                assertThatPosition(position).hasNoCloseValues()
            }
        }
    }

    @Nested
    @DisplayName("Move action on open position lead in:")
    inner class MoveActionOnOpenPosition {

        private lateinit var deposit: Deposit
        private lateinit var move: Move

        @BeforeEach
        fun setup() {
            deposit = Deposit.fromTestdata(label = null)
            move = Move.fromTestdata(movePosition = Position.fromDeposit(1, deposit), targetLabel = "Savings")
            positions = PositionFactory.fromActions(listOf(deposit, move))
        }

        @Test
        @DisplayName("result list of size: 1")
        fun resultListSize() {
            assertThat(positions).hasSize(1)
        }

        @Nested
        @DisplayName("position with:")
        inner class PositionWith {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }

            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(1)
            }

            @Test
            @DisplayName("position data of source position with label of move action")
            fun positionData() {
                assertThatPosition(position).hasPosition(deposit.quantity, deposit.title, move.targetLabel)
            }

            @Test
            @DisplayName("open values of source position")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, deposit.valueFiat, deposit.valueCrypto)
            }

            @Test
            @DisplayName("close values of source position")
            fun closingData() {
                assertThatPosition(position).hasNoCloseValues()
            }
        }
    }

    @Nested
    @DisplayName("Move action on closed position lead in:")
    inner class MoveActionOnClosedPosition {

        private lateinit var deposit: Deposit
        private lateinit var withdraw: Withdraw
        private lateinit var move: Move

        @BeforeEach
        fun setup() {
            deposit = Deposit.fromTestdata(label = null)
            withdraw = Withdraw.fromTestdata(sourcePosition = Position.fromDeposit(1, deposit))
            move = Move.fromTestdata(movePosition = Position.fromDeposit(1, deposit), targetLabel = "Savings")
            positions = PositionFactory.fromActions(listOf(deposit, withdraw, move))
        }

        @Test
        @DisplayName("result list of size: 1")
        fun resultListSize() {
            assertThat(positions).hasSize(1)
        }

        @Nested
        @DisplayName("adding position with:")
        inner class AddingPosition {

            private lateinit var position: Position

            @BeforeEach
            fun setup() {
                position = positions[0]
            }


            @Test
            @DisplayName("id equals index of action which created this position")
            fun id() {
                assertThatPosition(position).hasId(1)
            }

            @Test
            @DisplayName("position data of source position with label of move action")
            fun positionData() {
                assertThatPosition(position).hasPosition(deposit.quantity, deposit.title, move.targetLabel)
            }

            @Test
            @DisplayName("open values of source position (deposit)")
            fun openValues() {
                assertThatPosition(position).hasOpenValues(deposit.date, deposit.valueFiat, deposit.valueCrypto)
            }

            @Test
            @DisplayName("close values of source position (withdraw)")
            fun closingData() {
                assertThatPosition(position).hasCloseValues(withdraw.date, withdraw.valueFiat, withdraw.valueCrypto)
            }
        }
    }
}
