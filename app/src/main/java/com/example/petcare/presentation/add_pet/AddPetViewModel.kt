package com.example.petcare.presentation.add_pet

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.domain.use_case.add_pet.AddPetUseCase
import com.example.petcare.domain.use_case.add_pet_by_key.AddPetByKeyUseCase
import com.example.petcare.domain.use_case.get_pet_by_id.GetPetByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import kotlinx.datetime.todayIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import timber.log.Timber


@HiltViewModel
class AddPetViewModel @Inject constructor(
    private val addPetUseCase: AddPetUseCase,
    private val addPetByKeyUseCase: AddPetByKeyUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(AddPetState())
    val state = _state.asStateFlow()

    fun onModeChange(mode: AddPetMode) {
        _state.update { it.copy(currentMode = mode, error = null, foundPet = null) }
    }
    fun onNameChange(value: String) {
        _state.update { it.copy(name = value) }
    }
    fun onSpeciesChange(value: speciesEnum) {
        _state.update { it.copy(species = value) }
    }
    fun onBreedChange(value: String) {
        _state.update { it.copy(breed = value) }
    }
    fun onSexChange(value: sexEnum) {
        Timber.d("DEBUG: Zmieniono płeć zwierzaka na $value")
        _state.update { it.copy(sex = value) }
    }
    fun onBirthDateChange(date: LocalDate) {
        Timber.d("DEBUG: Zmieniono datę urodzenia zwierzaka na $date")
        _state.update { it.copy(birthDate = date) }
    }
    fun onIdChange(value: String) {
        _state.update { it.copy(petIdToAdd = value) }
    }
    fun onAvatarChange(uri: Uri?) {
        _state.update { it.copy(avatarThumbUrl = uri?.toString()) }
    }

    fun onAddNewPet() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val birthDate = _state.value.birthDate
                ?: Clock.System.todayIn(currentSystemDefault())
            Timber.tag("AddPetViewModel").d("Birth date: $birthDate")

            val imageBytes = _state.value.avatarThumbUrl?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    application.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            Timber.tag("AddPetDebug").d("""
                === PRÓBA ZAPISU ===
                1. Sex ze stanu (_state.value.sex): ${_state.value.sex}
                2. BirthDate ze stanu (_state.value.birthDate): ${_state.value.birthDate}
                3. BirthDate przetworzone (zmienna): $birthDate
                ====================
            """.trimIndent())

            addPetUseCase(
                name = _state.value.name,
                species = _state.value.species,
                breed = _state.value.breed,
                sex = _state.value.sex,
                birthDate = birthDate,
                byteArrayImage = imageBytes
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccessful = true) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Error") }
                    }
                }
            }
        }
    }

    fun onSearchClick() {
        val id = _state.value.petIdToAdd.trim()
        if (id.isBlank()) {
            _state.update { it.copy(error = "Please enter an ID") }
            return
        }
        viewModelScope.launch {
            getPetByIdUseCase(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                foundPet = result.data,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Pet not found") }
                    }
                }
            }
        }
    }

    fun onAddFoundPetClick() {
        val petKey = _state.value.petIdToAdd
        viewModelScope.launch {
            addPetByKeyUseCase(petKey).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccessful = true) }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Error adding pet"
                            )
                        }
                    }
                }
            }
        }
    }

    fun onBackFromConfirmation() {
        _state.update { it.copy(foundPet = null, error = null) }

    }
}