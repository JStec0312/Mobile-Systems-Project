package com.example.petcare.presentation.help

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme

@Composable
fun HelpScreen() {
    val scrollState = rememberScrollState()

    val faqList = listOf(
        "How do I share my pet's profile?" to "To share a pet profile, go to the \"New Pet\" screen and select \"Add pet by ID\". You can find your pet’s unique ID in their profile settings.",
        "Is VetAI a real veterinarian?" to "No. VetAI is an intelligent assistant designed to provide general advice. It does not replace a professional veterinary consultation.",
        "Walk Tracker not working?" to "The Walk Tracker needs access to your location. Please ensure that you have granted Pet Care permission to access your GPS location.",
        "Can I manage multiple pets?" to "Yes! You can easily switch between profiles from the main \"My Pets\" menu. Each pet has its own separate dashboard."
    )

    BaseScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 0.dp)) {
                Text(
                    text = "FAQ",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    faqList.forEach { (question, answer) ->
                        FaqItem(question = question, answer = answer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 0.dp)) {
                Text(
                    text = "Feedback",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeedbackContent()
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isExpanded)
            MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.surface,
        label = "colorAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = question,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun FeedbackContent() {
    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "We value your opinion!",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Found a bug? Or maybe you have an idea for a new feature? Tell us how we can improve PetCare.",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(25.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "petcare@mail.com",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    PetCareTheme {
        HelpScreen()
    }
}