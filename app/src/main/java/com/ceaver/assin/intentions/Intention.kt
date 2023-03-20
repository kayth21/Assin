package com.ceaver.assin.intentions

import android.os.Parcelable
import com.ceaver.assin.common.Exportable
import com.ceaver.assin.intentions.input.IntentionUiState
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.notification.AssinNotification
import kotlinx.parcelize.Parcelize
import org.apache.commons.csv.CSVRecord
import timber.log.Timber
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
@Parcelize
data class Intention(
        val id: Long = 0,
        val type: IntentionType,
        val active: Boolean,
        val quantity: BigDecimal?,
        val baseTitle: Title,
        val quoteTitle: Title,
        val target: BigDecimal,
        val status: IntentionStatus,
        val snoozeNear: LocalDateTime = LocalDateTime.now(),
        val snoozeAct: LocalDateTime = LocalDateTime.now(),
        var comment: String?
) : Exportable, Parcelable {

    val factorToReferencePrice: BigDecimal
        get() {
            val currentValue = baseTitle.lookupPrice(quoteTitle)
            val desiredValue = target.toDouble()

            return when (type) {
                IntentionType.SELL -> currentValue.div(desiredValue).toBigDecimal()
                IntentionType.BUY -> desiredValue.div(currentValue).toBigDecimal()
            }
        }

    val snoozingNear: Boolean
        get() {
            return snoozeNear.isAfter(LocalDateTime.now())
        }

    val snoozingAct: Boolean
        get() {
            return snoozeAct.isAfter((LocalDateTime.now()))
        }

    val actualStatus: IntentionStatus
        get() {
            Timber.i("${factorToReferencePrice.toDouble()}")
            return when (factorToReferencePrice.toDouble()) {
                in 0.0..0.9 -> IntentionStatus.WAIT
                in 0.9..1.0 -> IntentionStatus.NEAR
                else -> IntentionStatus.ACT
            }
        }

    fun copy(uiState: IntentionUiState) : Intention {
        return copy(
            type = uiState.type,
            active = uiState.active,
            quantity = uiState.quantity.toBigDecimalOrNull(),
            baseTitle = uiState.baseTitle!!,
            quoteTitle = uiState.quoteTitle!!,
            target = uiState.target.toBigDecimal(),
            comment = uiState.comment
        )
    }

    companion object Factory {
        fun fromDto(intentionDto: IntentionDto): Intention {
            return Intention(
                    id = intentionDto.intention.id,
                    type = intentionDto.intention.type,
                    active = intentionDto.intention.active,
                    quantity = intentionDto.intention.quantity,
                    baseTitle = intentionDto.baseTitle.toTitle(),
                    quoteTitle = intentionDto.quoteTitle.toTitle(),
                    target = intentionDto.intention.target,
                    status = intentionDto.intention.status,
                    snoozeNear = intentionDto.intention.snoozeNear,
                    snoozeAct = intentionDto.intention.snoozeAct,
                    comment = intentionDto.intention.comment
            )
        }

        suspend fun fromImport(csvRecord: CSVRecord): Intention {
            return Intention(
                    type = IntentionType.valueOf(csvRecord.get(0)),
                    active = csvRecord.get(1).toBoolean(),
                    quantity = csvRecord.get(2).toBigDecimalOrNull(),
                    baseTitle = TitleRepository.loadById(csvRecord.get(3)),
                    quoteTitle = TitleRepository.loadById(csvRecord.get(4)),
                    target = csvRecord.get(5).toBigDecimal(),
                    status = IntentionStatus.valueOf(csvRecord.get(6)),
                    comment = csvRecord.get(7).ifEmpty { null })
        }
    }

    override fun toExport(): List<String> {
        return listOf(
                type.name,
                active.toString(),
                quantity?.toPlainString() ?: "",
                baseTitle.id,
                quoteTitle.id,
                target.toPlainString(),
                status.name,
                comment.orEmpty()
        )
    }

    fun toEntity(): IntentionEntity {
        return IntentionEntity(
                id = id,
                type = type,
                active = active,
                quantity = quantity,
                baseTitleId = baseTitle.id,
                quoteTitleId = quoteTitle.id,
                target = target,
                status = status,
                snoozeNear = snoozeNear,
                snoozeAct = snoozeAct,
                comment = comment
        )
    }

    fun evaluate(): Pair<Intention, AssinNotification?>? {

        if (!active) {
            return null
        }

        fun createNearNotification(): AssinNotification {
            return IntentionNotification.near(id, baseTitle.getIcon(), "${baseTitle.name} ${type.name.toLowerCase(Locale.ROOT)} intention near target", comment.orEmpty())
        }

        fun createActNotification(): AssinNotification {
            return IntentionNotification.act(id, baseTitle.getIcon(), "${baseTitle.name} ${type.name.toLowerCase(Locale.ROOT)} intention hits target!", comment.orEmpty())
        }

        when {
            // TODO only if more than 10% below
            actualStatus < status -> {
                val intention = copy(status = actualStatus)
                return Pair(intention, null)
            }
            actualStatus == status -> {
                when {
                    status == IntentionStatus.WAIT && actualStatus == IntentionStatus.WAIT -> {
                        return null
                    }
                    status == IntentionStatus.NEAR && actualStatus == IntentionStatus.NEAR -> {
                        return if (snoozingNear) {
                            null
                        } else {
                            val notification = createNearNotification()
                            val intention = copy(snoozeNear = LocalDateTime.now().plusDays(1))
                            Pair(intention, notification)
                        }
                    }
                    status == IntentionStatus.ACT && actualStatus == IntentionStatus.ACT -> {
                        return if (snoozingAct) {
                            null
                        } else {
                            val notification = createActNotification()
                            val intention = copy(snoozeAct = LocalDateTime.now().plusDays(1))
                            Pair(intention, notification)
                        }
                    }
                }
            }
            actualStatus > status -> {
                if (actualStatus == IntentionStatus.NEAR) {
                    return if (snoozingNear) {
                        null
                    } else {
                        val notification = createNearNotification()
                        val intention = copy(snoozeNear = LocalDateTime.now().plusDays(1))
                        Pair(intention, notification)
                    }
                } else {
                    return if (snoozingAct) {
                        null
                    } else {
                        val notification = createActNotification()
                        val intention = copy(snoozeAct = LocalDateTime.now().plusDays(1))
                        Pair(intention, notification)
                    }
                }
            }
        }
        throw IllegalStateException()
    }
}