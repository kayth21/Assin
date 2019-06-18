package com.ceaver.assin.database

import android.arch.persistence.room.TypeConverter
import com.ceaver.assin.alerts.AlertType
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.intentions.IntentionStatus
import com.ceaver.assin.intentions.IntentionType
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class Converters {

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate): Long = ChronoUnit.DAYS.between(LocalDate.MIN, localDate)

    @TypeConverter
    fun toLocalDate(long: Long): LocalDate = LocalDate.MIN.plusDays(long)

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): Long = ChronoUnit.SECONDS.between(LocalDateTime.MIN, localDateTime)

    @TypeConverter
    fun toLocalDateTime(long: Long): LocalDateTime = LocalDateTime.MIN.plusSeconds(long)

    @TypeConverter
    fun fromUuid(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toUuid(string: String): UUID = UUID.fromString(string)

    @TypeConverter
    fun fromOptionalString(optional: Optional<String>): String = optional.map { it.toString() }.orElse("")

    @TypeConverter
    fun toOptionalString(string: String): Optional<String> = if (string.isEmpty()) Optional.empty() else Optional.of(string)

    @TypeConverter
    fun fromOptionalDouble(optional: Optional<Double>): String = if (optional.isPresent) optional.get().toString() else ""

    @TypeConverter
    fun toOptionalDouble(string: String): Optional<Double> = if (string.isEmpty()) Optional.empty() else Optional.of(string.toDouble())

    @TypeConverter
    fun fromOptionalLocalDateTime(optional: Optional<LocalDateTime>): Long = if (optional.isPresent) ChronoUnit.SECONDS.between(LocalDateTime.MIN, optional.get()) else -1

    @TypeConverter
    fun toOptionalLocalDateTime(long: Long): Optional<LocalDateTime> = if (long == -1L) Optional.empty() else Optional.of(LocalDateTime.MIN.plusSeconds(long))

    @TypeConverter
    fun fromTitle(title: Title): String = title.id

    @TypeConverter
    fun toTitle(string: String): Title = TitleRepository.loadTitle(string)

    @TypeConverter
    fun fromOptionalTitle(title: Optional<Title>): String = if (title.isPresent) title.get().id else ""

    @TypeConverter
    fun toOptionalTitle(string: String): Optional<Title> = if (string.isEmpty()) Optional.empty() else Optional.of(TitleRepository.loadTitle(string))

    @TypeConverter
    fun fromAlertType(alertType: AlertType): String = alertType.name

    @TypeConverter
    fun toAlertType(string: String): AlertType = AlertType.valueOf(string)

    @TypeConverter
    fun fromIntentionStatus(intentionStatus: IntentionStatus): String = intentionStatus.name

    @TypeConverter
    fun toIntentionStatus(string: String): IntentionStatus = IntentionStatus.valueOf(string)

    @TypeConverter
    fun fromIntentionType(intentionType: IntentionType): String = intentionType.name

    @TypeConverter
    fun toIntentionType(string: String): IntentionType = IntentionType.valueOf(string)

    @TypeConverter
    fun fromAssetCategory(assetCategory: AssetCategory): String = assetCategory.name

    @TypeConverter
    fun toAssetCategory(string: String): AssetCategory = AssetCategory.valueOf(string)

}