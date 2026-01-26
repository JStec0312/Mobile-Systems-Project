package com.example.petcare.presentation.ai_chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.R
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme

@Composable
fun AIChatRoute(
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AIChatScreen(
        state = state,
        onMessageChange = viewModel::onMessageChange,
        onSendMessage = viewModel::onSendMessage
    )
}

@Composable
fun AIChatScreen(
    state: AIChatState,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    val listState = rememberLazyListState()

    // Automatyczne przewijanie do dołu
    LaunchedEffect(state.messages.size, state.isAiTyping) {
        if (state.messages.isNotEmpty()) {
            val targetIndex = state.messages.size + (if (state.isAiTyping) 1 else 0) - 1
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    BaseScreen() {
        Box(modifier = Modifier.fillMaxSize()) {
            // TŁO (Łapki)
            Image(
                painter = painterResource(id = R.drawable.paw_prints),
                contentDescription = "",
                modifier = Modifier
                    .scale(scaleX = -1f, scaleY = -1f)
                    .align(Alignment.TopStart)
                    .offset(x = 120.dp, y = 150.dp)
                    .size(500.dp)
                    .alpha(0.5f)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Odstęp od góry
                Spacer(modifier = Modifier.height(60.dp))

                // --- TREŚĆ CZATU ---
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Nagłówek daty "Today"
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Today",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Lista wiadomości
                    items(state.messages) { message ->
                        ChatBubble(message = message)
                    }

                    // Wskaźnik pisania
                    if (state.isAiTyping) {
                        item {
                            TypingIndicator()
                        }
                    }

                    // Ewentualny błąd
                    if (state.error != null && !state.isAiTyping) {
                        item {
                            Text(
                                text = state.error,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 50.dp)
                            )
                        }
                    }
                }

                // --- POLE TEKSTOWE ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Gallery",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = state.inputText,
                        onValueChange = onMessageChange,
                        placeholder = { Text("Ask me anything...", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onSendMessage,
                        enabled = state.inputText.isNotBlank() && !state.isAiTyping
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (state.inputText.isNotBlank()) MaterialTheme.colorScheme.secondary else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.secondary else Color.White
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.secondary

    // ZMIANA: Wyrównanie całego wiersza do góry (Alignment.Top)
    // Dzięki temu dymek zawsze zaczyna się na wysokości avatara
    val verticalAlignment = Alignment.Top
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.align(alignment),
            verticalAlignment = verticalAlignment // Zastosowanie wyrównania do góry
        ) {
            // Awatar AI (tylko po lewej stronie)
            if (!isUser) {
                // ZMIANA: Surface zamiast Boxa dla łatwiejszego cienia i tła
                Surface(
                    shape = CircleShape,
                    color = Color.White, // ZMIANA: Białe tło kółka
                    shadowElevation = 1.dp, // Opcjonalny delikatny cień
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // ZMIANA: Icon zamiast Image pozwala na łatwe użycie tint (koloru)
                        Icon(
                            painter = painterResource(id = R.drawable.paw),
                            contentDescription = "AI Avatar",
                            tint = MaterialTheme.colorScheme.secondary, // ZMIANA: Fioletowa łapka
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Kolumna zawierająca Dymek + Czas
            Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                Surface(
                    color = bubbleColor,
                    shape = shape,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(12.dp),
                        color = textColor,
                        fontSize = 15.sp
                    )
                }

                // Godzina jest teraz w kolumnie, więc naturalnie spada POD dymek
                Text(
                    text = message.timestamp,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    // ZMIANA: Wyrównanie do góry (Top), żeby pasowało do zwykłych wiadomości
    Row(verticalAlignment = Alignment.Top) {

        // ZMIANA: Ten sam styl avatara co w ChatBubble (Białe tło, Fioletowa ikona)
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.paw),
                    contentDescription = "AI Avatar",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Dymek z kropkami
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}

// --- PODGLĄD ---

@Preview(showBackground = true)
@Composable
fun AIChatPreview() {
    val dummyMessages = listOf(
        ChatMessage(
            text = "Hello! I'm VetAI, your veterinary assistant \uD83D\uDC3E",
            isUser = false,
            timestamp = "12:15"
        ),
        ChatMessage(
            text = "How can I help you and your pet today?",
            isUser = false,
            timestamp = "12:16"
        ),
        ChatMessage(
            text = "Hi, my dog ate chocolate!",
            isUser = true,
            timestamp = "12:18"
        ),
        ChatMessage(
            text = "Please go to the vet immediately! Chocolate is toxic.",
            isUser = false,
            timestamp = "12:19"
        )
    )

    PetCareTheme {
        AIChatScreen(
            state = AIChatState(
                messages = dummyMessages,
                inputText = "Okay, I'm going!",
                isAiTyping = false
            ),
            onMessageChange = {},
            onSendMessage = {}
        )
    }
}