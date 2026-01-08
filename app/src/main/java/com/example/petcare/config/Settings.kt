package com.example.petcare.config

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant


object Settings {
    const val MODE = "DEV-FIREBASE" // PROD or DEV or DEV-FIREBASE baza danych w firebase / ram / firebase emulator
    val  MIN_PASSWORD_LENGTH : Int = 8;
    val PET_SHARE_CODE_EXP_SECONDS = 15 * 60 // 15 minutes
    const val USER_ID_KEY = "CURRENT_USER_ID"

    const val WALK_TRACK_POINT_INTERVAL_MS = 20000L
    const val WALK_MIN_UPDATE_DISTANCE_METERS = 1f
    const val WALK_MIN_UPDATE_TIME_MS = 15000L

    const val NOTIFICATION_BEFORE_MINUTES = 25
    const val NOTIFICATION_WINDOW_MINUTES = 10

    const val PERIODIC_WORKER_INTERVAL_MINUTES: Long = 1L
}


