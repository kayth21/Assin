package com.ceaver.assin.positions

import com.ceaver.assin.markets.Title
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal
import java.time.LocalDate

fun assertThatPosition(actual: Position): PositionIdAssert {
    return PositionIdAssert(actual)
}

class PositionIdAssert(actual: Position) : AbstractAssert<PositionIdAssert, Position>(actual, PositionIdAssert::class.java) {

    fun hasId(positionId: BigDecimal): PositionAssert {
        Assertions.assertThat(actual.id).isEqualTo(positionId)
        return PositionAssert(actual)
    }
}

class PositionAssert(actual: Position) : AbstractAssert<PositionAssert, Position>(actual, PositionAssert::class.java) {

    fun hasPosition(quantity: BigDecimal, title: Title, label: String?): PositionOpenAssert {
        assertAll(
                { Assertions.assertThat(actual.quantity).isEqualTo(quantity) },
                { Assertions.assertThat(actual.title).isEqualTo(title) },
                { Assertions.assertThat(actual.label).isEqualTo(label) }
        )
        return PositionOpenAssert(actual)
    }
}

class PositionOpenAssert(actual: Position) : AbstractAssert<PositionOpenAssert, Position>(actual, PositionOpenAssert::class.java) {

    fun hasOpenValues(date: LocalDate, valueFiat: BigDecimal, valueCrypto: BigDecimal): PositionCloseAssert {
        assertAll(
                { Assertions.assertThat(actual.openQuotes.date).isEqualTo(date) },
                { Assertions.assertThat(actual.openQuotes.valueFiat).isEqualTo(valueFiat) },
                { Assertions.assertThat(actual.openQuotes.valueCrypto).isEqualTo(valueCrypto) }
        )
        return PositionCloseAssert(actual)
    }
}

class PositionCloseAssert(actual: Position) : AbstractAssert<PositionCloseAssert, Position>(actual, PositionCloseAssert::class.java) {

    fun hasCloseValues(date: LocalDate, valueFiat: BigDecimal, valueCrypto: BigDecimal) {
        Assertions.assertThat(actual.closedQuotes).isNotNull
        assertAll(
                { Assertions.assertThat(actual.closedQuotes!!.date).isEqualTo(date) },
                { Assertions.assertThat(actual.closedQuotes!!.valueFiat).isEqualTo(valueFiat) },
                { Assertions.assertThat(actual.closedQuotes!!.valueCrypto).isEqualTo(valueCrypto) }
        )
    }

    fun isNotClosed() {
        Assertions.assertThat(actual.closedQuotes).isNull()
    }
}