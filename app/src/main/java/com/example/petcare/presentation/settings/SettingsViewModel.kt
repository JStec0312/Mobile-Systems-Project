package com.example.petcare.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.use_case.edit_notification_settings.EditNotificationSettingsUseCase
import com.example.petcare.domain.use_case.get_notification_settings.GetNotificationSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val editNotificationSettingsUseCase: EditNotificationSettingsUseCase,
    private val petProvider: IPetProvider
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val currentPetIdString = petProvider.getCurrentPetId()
            val petUuid = try {
                if (currentPetIdString != null) UUID.fromString(currentPetIdString) else UUID.randomUUID()
            } catch (e: Exception) {
                UUID.randomUUID()
            }

            getNotificationSettingsUseCase(petId = petUuid).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                settings = result.data ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun onToggleNotification(category: notificationCategoryEnum) {
        viewModelScope.launch {
            editNotificationSettingsUseCase(category).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        loadSettings()
                    }
                    is Resource.Error -> {
                        loadSettings()
                    }
                    is Resource.Loading -> {
                        // Można dodać loader na konkretnym switchu, ale na razie pominimy
                    }
                }
            }
        }
    }
}