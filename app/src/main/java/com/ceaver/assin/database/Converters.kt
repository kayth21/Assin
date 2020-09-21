package com.ceaver.assin.database

import androidx.room.TypeConverter
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.alerts.AlertType
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.intentions.IntentionStatus
import com.ceaver.assin.intentions.IntentionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class Converters {

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): Long? = localDate?.let { ChronoUnit.DAYS.between(LocalDate.MIN, localDate) }

    @TypeConverter
    fun toLocalDate(long: Long?): LocalDate? = long?.let { LocalDate.MIN.plusDays(it) }

    @TypeConverter
    fun fromBigDecimal(bigDecimal: BigDecimal?): String? = bigDecimal?.toPlainString()

    @TypeConverter
    fun toBigDecimal(string: String?): BigDecimal? = string?.toBigDecimalOrNull()

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): Long? = localDateTime?.let { ChronoUnit.SECONDS.between(LocalDateTime.MIN, localDateTime) }

    @TypeConverter
    fun toLocalDateTime(long: Long?): LocalDateTime? = long?.let { LocalDateTime.MIN.plusSeconds(it) }

    @TypeConverter
    fun fromUuid(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUuid(string: String?): UUID? = string?.let { UUID.fromString(string) }

    @TypeConverter
    fun fromAlertType(alertType: AlertType?): String? = alertType?.name

    @TypeConverter
    fun toAlertType(string: String?): AlertType? = string?.let { AlertType.valueOf(it) }

    @TypeConverter
    fun fromIntentionStatus(intentionStatus: IntentionStatus?): String? = intentionStatus?.name

    @TypeConverter
    fun toIntentionStatus(string: String?): IntentionStatus? = string?.let { IntentionStatus.valueOf(it) }

    @TypeConverter
    fun fromIntentionType(intentionType: IntentionType?): String? = intentionType?.name

    @TypeConverter
    fun toIntentionType(string: String?): IntentionType? = string?.let { IntentionType.valueOf(it) }

    @TypeConverter
    fun fromAssetCategory(assetCategory: AssetCategory?): String? = assetCategory?.name

    @TypeConverter
    fun toAssetCategory(string: String?): AssetCategory? = string?.let { AssetCategory.valueOf(it) }

    @TypeConverter
    fun fromActionType(assetType: ActionType?): String? = assetType?.name

    @TypeConverter
    fun toActionType(string: String?): ActionType? = string?.let { ActionType.valueOf(it) }
}