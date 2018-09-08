package com.ceaver.assin.database.converter;

import android.arch.persistence.room.TypeConverter;

import com.ceaver.assin.alerts.AlertType;
import com.ceaver.assin.assets.Symbol;
import com.ceaver.assin.trades.TradeStrategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Converters {

    @TypeConverter
    public static Symbol fromStringToTitle(String title) {
        return Symbol.valueOf(title);
    }

    @TypeConverter
    public static String fromTitleToString(Symbol symbol) {
        return symbol.name();
    }

    @TypeConverter
    public static LocalDate daysFromEpochToLocalDate(Long daysFromEpoch) {
        return daysFromEpoch == null ? null : LocalDate.MIN.plusDays(daysFromEpoch);
    }

    @TypeConverter
    public static Long localDateToDaysFromEpoch(LocalDate date) {
        return date == null ? null : ChronoUnit.DAYS.between(LocalDate.MIN, date);
    }

    @TypeConverter
    public static Set<TradeStrategy> toTradeStrategySet(String tradeStrategyString) {
        return Pattern.compile(";").splitAsStream(tradeStrategyString).map(i -> toStatus(i)).collect(Collectors.toSet());
    }

    @TypeConverter
    public static String toTradeStrategyString(Set<TradeStrategy> tradeStrategySet) {
        return tradeStrategySet.stream().map(i -> toOrdinal(i)).collect(Collectors.joining(";"));
    }

    @TypeConverter
    public static TradeStrategy toStatus(String name) {
        return TradeStrategy.valueOf(name);
    }

    @TypeConverter
    public static String toOrdinal(TradeStrategy strategy) {
        return strategy.name();
    }

    @TypeConverter
    public static AlertType toAlertType(String name) {
        return AlertType.valueOf(name);
    }

    @TypeConverter
    public static String fromAlertType(AlertType alertType) {
        return alertType.name();
    }
}