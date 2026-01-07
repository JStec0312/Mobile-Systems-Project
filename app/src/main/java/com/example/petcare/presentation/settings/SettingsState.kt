package com.example.petcare.presentation.settings

import com.example.petcare.domain.model.NotificationSettings

data class SettingsState(
    val settings: List<NotificationSettings> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)