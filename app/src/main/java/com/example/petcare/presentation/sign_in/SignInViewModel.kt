package com.example.petcare.presentation.sign_in


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.petcare.domain.use_case.sign_in.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun onSubmit() {
        viewModelScope.launch { signIn() }
    }
    private suspend fun signIn() {
        _state.update { it.copy(isLoading = true, error = null) }
        val result = signInUseCase(
            email = _state.value.email,
            password = _state.value.password,
        )
        val newState = if (result.isSuccess) {
            _state.value.copy(isLoading = false, error = null)
        } else {
            _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
        }
        _state.value = newStategit
    }
}