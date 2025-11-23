package com.example.petcare.presentation.sign_in


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.auto_login.AutoLoginUseCase
import com.example.petcare.domain.use_case.sign_in.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val autoLoginUseCase: AutoLoginUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    init {
        checkAutoLogin()
    }
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
        signInUseCase(
            email = state.value.email,
            password = state.value.password
        ).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true, error = null) }
                }

                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, error = null, isSuccessful = true) }
                }

                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun checkAutoLogin() {
        _state.update { it.copy(isLoading = true) }

        autoLoginUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccessful = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                }
                is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }

}