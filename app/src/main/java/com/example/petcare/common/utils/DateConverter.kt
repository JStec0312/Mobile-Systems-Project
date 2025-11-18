package com.example.petcare.common.utils

import kotlinx.datetime.LocalDate

object DateConverter {


    fun localDateToString(date: LocalDate): String {
        return date.toString()
    }

    fun stringToLocalDate(dateString: String?): LocalDate {
        return try {
            LocalDate.parse(dateString)
        } catch (e: IllegalArgumentException) {
            LocalDate(1970, 1, 1)
        }
    }
}