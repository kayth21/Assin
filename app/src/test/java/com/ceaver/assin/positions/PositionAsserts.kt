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

    fun hasId(positionId: Int) {
        Assertions.assertThat(actual.id).isEqualTo(positionId)
    }

    fun hasPosition(quantity: BigDecimal, title: Title, label: String?) {
        assertAll(
                { Assertions.assertThat(actual.quantity).isEqualTo(quantity) },
                { Assertions.assertThat(actual.title).isEqualTo(title) },
                { Assertions.assertThat(actual.label).isEqualTo(label) }
        )
    }

    fun hasOpenValues(date: LocalDate, valueFiat: BigDecimal, valueCrypto: BigDecimal) {
        assertAll(
                { Assertions.assertThat(actual.open.date).isEqualTo(date) },
                { Assertions.assertThat(actual.open.valueFiat).isEqualTo(valueFiat) },
                { Assertions.assertThat(actual.open.valueCrypto).isEqualTo(valueCrypto) }
        )
    }

    fun hasCloseValues(date: LocalDate, valueFiat: BigDecimal, valueCrypto: BigDecimal) {
        Assertions.assertThat(actual.close).isNotNull
        assertAll(
                { Assertions.assertThat(actual.close!!.date).isEqualTo(date) },
                { Assertions.assertThat(actual.close!!.valueFiat).isEqualTo(valueFiat) },
                { Assertions.assertThat(actual.close!!.valueCrypto).isEqualTo(valueCrypto) }
        )
    }

    fun hasNoCloseValues() {
        Assertions.assertThat(actual.close).isNull()
    }
}