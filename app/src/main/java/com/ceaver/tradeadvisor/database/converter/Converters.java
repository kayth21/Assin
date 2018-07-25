package com.ceaver.tradeadvisor.database.converter;

import android.arch.persistence.room.TypeConverter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Converters {
    @TypeConverter
    public static LocalDate daysFromEpochToLocalDate(Long daysFromEpoch) {
        return daysFromEpoch == null ? null : LocalDate.MIN.plusDays(daysFromEpoch);
    }

    @TypeConverter
    public static Long localDateToDaysFromEpoch(LocalDate date) {
        return date == null ? null : ChronoUnit.DAYS.between(LocalDate.MIN, date);
    }
}