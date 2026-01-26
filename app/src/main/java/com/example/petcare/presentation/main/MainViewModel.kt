package com.example.petcare.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.logout.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _logoutEvent = MutableSharedFlow<Unit>(replay = 0)
    val logoutEvent = _logoutEvent.asSharedFlow()

    fun onLogout() {
        viewModelScope.launch {
            Timber.d("DEBUG: rozpoczynam wylogowanie")
            logoutUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Timber.d("DEBUG: wylogowanie zakończone")
                        _logoutEvent.emit(Unit)
                    }

                    is Resource.Error -> {
                        Timber.d("DEBUG: wylogowanie zakończone z błędem")
                        _logoutEvent.emit(Unit)
                    }
                }
            }
        }
    }
}
