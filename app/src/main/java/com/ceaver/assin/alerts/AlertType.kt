package com.ceaver.assin.alerts

import java.math.BigDecimal
import java.util.*

enum class AlertType {
    ONE_TIME {
        override fun check(alert: Alert, currentPrice: BigDecimal): Optional<Alert> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    RECURRING_STABLE {
        override fun check(alert: Alert, currentPrice: BigDecimal): Optional<Alert> {
            return when {
                currentPrice <= (alert.source - alert.target) -> Optional.of(alert.copy(source= alert.source - alert.target))
                currentPrice >= (alert.source + alert.target) -> Optional.of(alert.copy(source= alert.source + alert.target))
                else -> Optional.empty()
            }
        }
    },
    RECURRING_PERCENTAGE {
        override fun check(alert: Alert, currentPrice: BigDecimal): Optional<Alert> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    };
    abstract fun check(alert: Alert, currentPrice: BigDecimal): Optional<Alert>
}