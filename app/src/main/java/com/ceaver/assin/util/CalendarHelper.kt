package com.ceaver.assin.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarHelper {

    companion object {
        fun convertDate(date: LocalDate): String {
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }

        fun convertDate(date: String): LocalDate {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
    }
}