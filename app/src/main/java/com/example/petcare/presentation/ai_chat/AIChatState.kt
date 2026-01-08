package com.example.petcare.presentation.ai_chat

import java.util.UUID

// Model pojedynczej wiadomości (tylko dla UI)
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean, // true = Ty (fioletowy dymek), false = AI (biały dymek + łapka)
    val timestamp: String
)

// Stan całego ekranu
data class AIChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isAiTyping: Boolean = false,
    val error: String? = null
)