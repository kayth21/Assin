package com.ceaver.tradeadvisor.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarHelper {

    companion object {
        fun convertDate(date: Date): String {
            return SimpleDateFormat("dd.MM.yyyy").format(date)
        }

        fun convertDate(date: String): Date {
            return Date.from(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay(ZoneId.systemDefault()).toInstant())
        }

        fun convertDate(dayOfMonth: Int, month: Int, year: Int): Date {
            return Date.from(LocalDate.of(year, month, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
    }
}