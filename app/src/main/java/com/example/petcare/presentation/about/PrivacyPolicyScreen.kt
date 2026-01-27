package com.example.petcare.presentation.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    BaseScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Nagłówek z X (ręczny, bo to ekran z About, nie z Dashboardu)
            // Możesz też użyć rozwiązania z MainContainer jeśli dodasz tam ten ekran
            Spacer(modifier = Modifier.height(30.dp))

            // Tytuł
            Text(
                text = "PRIVACY POLICY",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Treść
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                PolicySection("1. Information We Collect", "We collect information to provide better services to all our users. This includes:\n\n• Account Information: Email address via Firebase Authentication.\n• Pet Data: Names, breeds, medical history, and photos you upload.\n• Location Data: We use your location to track walks using Google Maps API. This data is used solely for the Walk Tracker feature.\n• Usage Data: Information on how you use the app to improve user experience.")

                PolicySection("2. How We Use Information", "We use the information we collect to:\n\n• Provide, maintain, and improve our services.\n• Develop new features (e.g., AI analysis).\n• Provide customer support.\n• Send you technical notices and reminders (like medication alerts).")

                PolicySection("3. Third-Party Services", "We may share information with the following third-party providers for operational purposes:\n\n• Google Firebase (Hosting & Auth)\n• Google Maps Platform (Location Services)\n• AI Providers (for the Vet AI Chat feature)\n\nWe do not sell your personal data to advertisers.")

                PolicySection("4. Data Security", "We strive to use commercially acceptable means to protect your Personal Data, but remember that no method of transmission over the Internet is 100% secure.")

                PolicySection("5. Contact Us", "If you have any questions about this Privacy Policy, please contact us at: contact@petcare.com")

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    PetCareTheme {
        PrivacyPolicyScreen(
            onNavigateBack = {}
        )
    }
}