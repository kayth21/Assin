package com.ceaver.assin.markets

import com.ceaver.assin.assets.Symbol
import java.util.*

data class Title(val symbol: Symbol, val last: Double, val open: Double, val unit: Symbol) {

    fun last(unit: Symbol) : OptionalDouble {
        if(unit == this.symbol) {
            return OptionalDouble.of(1.0)
        }
        if(unit == this.unit) {
            return OptionalDouble.of(last);
        }
        val optional = MarketValuation.load(this.unit, unit)
        if(optional.isPresent) {
            return OptionalDouble.of(last * optional.get().last)
        } else {
            return OptionalDouble.of(0.0)
        }
    }

    fun open(unit: Symbol) : OptionalDouble {
        if(unit == this.symbol) {
            return OptionalDouble.of(1.0)
        }
        if(unit == this.unit) {
            return OptionalDouble.of(open);
        }
        val optional = MarketValuation.load(this.unit, unit)
        if(optional.isPresent) {
            return OptionalDouble.of(open * optional.get().open)
        } else {
            return OptionalDouble.of(0.0)
        }
    }
}