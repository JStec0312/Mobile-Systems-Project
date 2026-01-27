package com.example.petcare.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.use_case.edit_notification_settings.EditNotificationSettingsUseCase
import com.example.petcare.domain.use_case.get_notification_settings.GetNotificationSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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
                    is Resource.Loading -> {
                        // Loader tylko jeśli nie mamy żadnych danych
                        if (_state.value.settings.isEmpty()) {
                            _state.update { it.copy(isLoading = true, error = null) }
                        }
                    }
                    is Resource.Success -> {
                        val fetchedSettings = result.data ?: emptyList()

                        val finalSettings = if (fetchedSettings.isEmpty()) {
                            notificationCategoryEnum.values().map { category ->
                                NotificationSettings(
                                    id = UUID.randomUUID().toString(),
                                    userId = "temp",
                                    category = category,
                                    updatedAt = Clock.System.now(),
                                    enabled = false
                                )
                            }
                        } else {
                            fetchedSettings
                        }

                        val isGlobalEnabled = finalSettings.any { it.enabled }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                settings = finalSettings,
                                areNotificationsEnabled = isGlobalEnabled
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun toggleGlobalNotifications(shouldEnable: Boolean) {
        val settingsSnapshot = _state.value.settings

        viewModelScope.launch {
            // 1. OPTYMISTYCZNA AKTUALIZACJA UI
            // Ustawiamy suwak na taką pozycję, jaką chce użytkownik.
            _state.update { currentState ->
                val updatedSettings = currentState.settings.map {
                    it.copy(enabled = shouldEnable)
                }
                currentState.copy(
                    areNotificationsEnabled = shouldEnable,
                    settings = updatedSettings
                )
            }

            // 2. WYSYŁKA DO BACKENDU
            val deferredUpdates = notificationCategoryEnum.values().mapNotNull { category ->
                val currentSetting = settingsSnapshot.find { it.category == category }
                val isCurrentlyEnabled = currentSetting?.enabled ?: false

                if (shouldEnable != isCurrentlyEnabled) {
                    async {
                        editNotificationSettingsUseCase(category = category).first()
                    }
                } else {
                    null
                }
            }

            // Czekamy na odpowiedź serwera
            val results = deferredUpdates.awaitAll()
            val anyError = results.any { it is Resource.Error }

            // 3. OBSŁUGA WYNIKU
            if (anyError) {
                // JEŚLI BŁĄD -> Wtedy (i tylko wtedy) odświeżamy dane z serwera,
                // co spowoduje cofnięcie suwaka do prawdziwego stanu.
                loadSettings()
            }

            // JEŚLI SUKCES (else) -> NIC NIE ROBIMY.
            // Zostawiamy suwak włączony (tak jak ustawiliśmy w punkcie 1).
            // Ufamy, że backend przyjął zmianę. Nie pytamy go ponownie, bo może jeszcze "nie widzieć" swoich zmian.
        }
    }
}