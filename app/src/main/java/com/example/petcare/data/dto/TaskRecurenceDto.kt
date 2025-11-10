package com.example.petcare.data.dto

import com.example.petcare.common.frequencyEnum
import com.google.firebase.Timestamp

data class TaskRecurenceDto(
    val task_id: String,             // jesli trzymasz jako subkolekcje, mozesz to pominac
    val freq: frequencyEnum,
    val interval: Int? = null,       // co ile jednostek
    val by_weekday: String? = null,  // "MON,TUE"
    val by_month_day: String? = null,// "1,15,28"
    val until: Timestamp? = null,
    val rrule_text: String? = null
)