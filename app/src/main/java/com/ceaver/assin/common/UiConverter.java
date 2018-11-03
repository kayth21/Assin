package com.ceaver.assin.common;

import android.databinding.InverseMethod;

import com.ceaver.assin.assets.Symbol;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UiConverter {
    @InverseMethod("toDouble")
    public static String toString(double value) {
        if (value == 0.0) {
            return "";
        }
        return "" + value;
    }

    public static double toDouble(String value) {
        if (value.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }

    @InverseMethod("toSymbol")
    public static Integer toInt(Symbol value) {
        if(value == null) {
            return null;
        }
        return value.ordinal();
    }

    public static Symbol toSymbol(Integer ordinal) {
        if(ordinal == null) {
            return null;
        }
        return Symbol.values()[ordinal];
    }

    @InverseMethod("toLocalDate")
    public static String toString(LocalDate value) {
        if(value == null) {
            return "";
        }
        return value.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static LocalDate toLocalDate(String value) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}