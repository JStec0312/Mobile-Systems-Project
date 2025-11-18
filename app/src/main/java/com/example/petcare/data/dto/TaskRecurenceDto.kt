package com.example.petcare.data.dto

import com.example.petcare.common.frequencyEnum
import com.google.firebase.Timestamp

data class TaskRecurenceDto(
    val taskId: String,             // jesli trzymasz jako subkolekcje, mozesz to pominac
    val freq: frequencyEnum,
    val interval: Int? = null,       // co ile jednostek
    val byWeekday: String? = null,  // "MON,TUE"
    val byMonthDay: String? = null,// "1,15,28"
    val until: Timestamp? = null,
    val rruleText: String? = null
)