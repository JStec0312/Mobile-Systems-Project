package com.example.petcare.common.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.petcare.config.Settings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

object DateConverter {

    fun localDateToString(date: LocalDate?): String {
        return date.toString()
    }

    fun stringToLocalDate(dateString: String?): LocalDate {
        return try {
            LocalDate.parse(dateString ?: throw IllegalArgumentException("Date string cannot be null"))
        } catch (e: IllegalArgumentException) {
            LocalDate(1970, 1, 1)
        }
    }
    fun localDateNow(): LocalDate{
        return Clock.System.todayIn(TimeZone.UTC)
    }

    fun setPetShareCodeExpiryDate(): Instant {
        return Clock.System.now().plus(Settings.PET_SHARE_CODE_EXP_SECONDS, kotlinx.datetime.DateTimeUnit.SECOND)
    }

    fun instantNow(): Instant {
        return Clock.System.now()
    }
    fun stringToInstant(dateString: String?): Instant {
        return try {
            Instant.parse(dateString ?: throw IllegalArgumentException("Date string cannot be null"))
        } catch (e: IllegalArgumentException) {
            Instant.DISTANT_PAST
        }
    }

}