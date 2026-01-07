package com.example.petcare.presentation.ai_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.ask_vet_ai.AskVetAiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val askVetAiUseCase: AskVetAiUseCase // <-- Twój UseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AIChatState())
    val state = _state.asStateFlow()

    init {
        // Dodajemy wiadomość powitalną na start
        addMessage(
            text = "Hello! I'm VetAI. How can I help your pet today? \uD83D\uDC3E",
            isUser = false
        )
    }

    fun onMessageChange(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun onSendMessage() {
        val userText = _state.value.inputText
        if (userText.isBlank()) return

        // 1. Dodaj wiadomość użytkownika do listy i wyczyść pole
        addMessage(text = userText, isUser = true)
        _state.update { it.copy(inputText = "") }

        // 2. Wywołaj UseCase backendowy
        viewModelScope.launch {
            askVetAiUseCase(question = userText).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isAiTyping = true, error = null) }
                    }
                    is Resource.Success -> {
                        // Mamy odpowiedź z OpenAI!
                        _state.update { it.copy(isAiTyping = false) }
                        result.data?.let { answer ->
                            addMessage(text = answer, isUser = false)
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isAiTyping = false, error = result.message) }
                        // Opcjonalnie: Wyświetl błąd jako wiadomość w czacie
                        addMessage(text = "Error: ${result.message}", isUser = false)
                    }
                }
            }
        }
    }

    // Funkcja pomocnicza do dodawania wiadomości do listy
    private fun addMessage(text: String, isUser: Boolean) {
        val timestamp = getCurrentTime()
        val newMessage = ChatMessage(text = text, isUser = isUser, timestamp = timestamp)

        _state.update { currentState ->
            currentState.copy(
                messages = currentState.messages + newMessage
            )
        }
    }

    private fun getCurrentTime(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "${now.hour}:${now.minute.toString().padStart(2, '0')}"
    }
}