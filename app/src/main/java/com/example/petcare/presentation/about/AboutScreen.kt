package com.example.petcare.presentation.about

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcare.R
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme

@Composable
fun AboutScreen(
    onPrivacyPolicyClick: () -> Unit // Parametr przekazany
) {
    val scrollState = rememberScrollState()

    BaseScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(id = R.drawable.petcare_logo_purple),
                contentDescription = "Pet Care Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(1.dp))

            SectionHeader(title = "Our mission")

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val missionText = buildAnnotatedString {
                    append("At ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("PetCare")
                    }
                    append(", we believe that a healthy pet is a happy pet. We created this application to simplify the daily lives of pet parents by combining organization with smart technology.")
                }

                Text(
                    text = missionText,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionHeader(title = "Meet our Team")

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "We are a team of passionate developers and pet lovers dedicated to bridging the gap between technology and pet care. Our goal was to build a tool that we would love to use with our own pets.",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TeamMemberItem(name = "Kinga Jasitczak", email = "kinga@petcare.com")
                    Spacer(modifier = Modifier.height(10.dp))
                    TeamMemberItem(name = "Julia Rogalska", email = "julia@petcare.com")
                    Spacer(modifier = Modifier.height(10.dp))
                    TeamMemberItem(name = "Jakub Stec", email = "jakub@petcare.com")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPrivacyPolicyClick, // Użycie parametru
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "PRIVACY POLICY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "v 1.0.0",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 8.dp)) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun TeamMemberItem(name: String, email: String) {
    var isExpanded by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isExpanded)
            MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.surface,
        label = "colorAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { isExpanded = !isExpanded }
            .animateContentSize()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = email,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    PetCareTheme {
        AboutScreen(
            onPrivacyPolicyClick = {}
        )
    }
}