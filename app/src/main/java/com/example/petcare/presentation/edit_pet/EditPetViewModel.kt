package com.example.petcare.presentation.edit_pet

import android.app.Application
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.use_case.delete_pet.DeletePetUseCase
import com.example.petcare.domain.use_case.edit_pet.EditPetUseCase
import com.example.petcare.domain.use_case.get_pet_by_id.GetPetByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val editPetUseCase: EditPetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val application: Application

) : ViewModel() {
    private val _state = MutableStateFlow(EditPetState())
    val state = _state.asStateFlow()

    init {
        val petId = savedStateHandle.get<String>("petId")
        Timber.d("Loading pet data in edit pet view model for petId: $petId")

        if(petId != null) {
            loadPetData(petId)
        }
    }

    private fun loadPetData(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getPetByIdUseCase(id).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        val pet = result.data
                        if (pet != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    petId = pet . id,
                                    ownerUserId = pet.ownerUserId,
                                    name = pet.name,
                                    species = pet.species,
                                    breed = pet.breed ?: "",
                                    sex = pet.sex,
                                    birthDate = pet.birthDate,
                                    avatarThumbUrl = pet.avatarThumbUrl
                                )
                            }
                        }
                    }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                }
            }
        }
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
        _state.update { it.copy(sex = value) }
    }
    fun onBirthDateChange(date: LocalDate) {
        _state.update { it.copy(birthDate = date) }
    }
    fun onAvatarChange(uri: Uri?) {
        _state.update { it.copy(newAvatarThumbUrl = uri.toString()) }
    }
    fun onSaveClick() {
        _state.update { it.copy(showSaveDialog = true) }
    }

    fun onRemoveClick() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDialogs() {
        _state.update { it.copy(showSaveDialog = false, showDeleteDialog = false) }
    }

    fun onConfirmSave() {
        onDismissDialogs()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val imageBytes = _state.value.newAvatarThumbUrl?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    application.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            Timber.d("Editing pet with id: ${_state.value.petId}");
            val petToUpdate = Pet(
                id = _state.value.petId,
                ownerUserId = _state.value.ownerUserId,
                name = _state.value.name,
                species = _state.value.species,
                breed = _state.value.breed,
                sex = _state.value.sex,
                birthDate = _state.value.birthDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                avatarThumbUrl = _state.value.avatarThumbUrl,
                createdAt = _state.value.birthDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            )

            editPetUseCase(
                pet = petToUpdate,
                byteArrayImage = imageBytes
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, isUpdated = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }

                }
            }
        }
    }

    fun onConfirmDelete() {
        onDismissDialogs()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            deletePetUseCase(_state.value.petId).collect { result ->
                when(result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, isDeleted = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }

        }
    }

}