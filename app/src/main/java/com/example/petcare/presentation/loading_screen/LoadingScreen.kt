package com.example.petcare.presentation.loading_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.petcare.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        delay(3000L)
        onTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7A6BBC)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(500.dp),
            painter = painterResource(id = R.drawable.petcare_logo_white),
            contentDescription = "Logo aplikacji",
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen(onTimeout = {})
}


