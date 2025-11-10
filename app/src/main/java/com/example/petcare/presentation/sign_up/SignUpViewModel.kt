package com.example.petcare.presentation.sign_up


import androidx.lifecycle.ViewModel
import com.example.petcare.domain.use_case.sign_up.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        _state.update { it.copy(confirmPassword = value) }
    }

    fun onSubmit() {
        viewModelScope.launch { signUp() }
    }
    private suspend fun signUp() {
        _state.update { it.copy(isLoading = true, error = null) }
        val result = signUpUseCase(
            name = _state.value.name,
            email = _state.value.email,
            password = _state.value.password,
            confirmPassword = _state.value.confirmPassword
        )
        val newState = if (result.isSuccess) {
            _state.value.copy(isLoading = false, error = null)
        } else {
            _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
        }
        _state.value = newState
    }
}