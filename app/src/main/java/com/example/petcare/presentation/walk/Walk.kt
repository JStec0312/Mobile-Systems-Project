package com.example.petcare.presentation.walk

import android.Manifest // Poprawiony import (był java.util.jar)
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun WalkRoute(
    viewModel: WalkViewModel = hiltViewModel(),
    onNavigateToStats: () -> Unit,
    onStopClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Uprawnienia - Sprawdzamy oba uprawnienia (FINE i COARSE)
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            // Spróbuj żądać FINE_LOCATION, a jeśli odmówią, COARSE_LOCATION będzie fallback
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Domyślna pozycja: Warszawa (jeśli nie mamy lokalizacji)
    val DEFAULT_LOCATION = LatLng(52.2297, 21.0122)
    val initialPosition = state.currentLocation ?: DEFAULT_LOCATION

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 15f)
    }

    LaunchedEffect(state.currentLocation) {
        state.currentLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 17f)
            )
        }
    }

    LaunchedEffect(state.routePoints) {
        if (state.routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(state.routePoints.last(), 17f)
            )
        }
    }

    WalkScreen(
        state = state.copy(isLocationEnabled = hasLocationPermission),
        cameraPositionState = cameraPositionState,
        onStopClick = {
            // WAŻNE: Najpierw logika biznesowa, potem nawigacja
            viewModel.onFinishWalkClick()
            onStopClick()
        },
        onStatsClick = onNavigateToStats
    )
}

@Composable
fun WalkScreen(
    state: WalkState,
    cameraPositionState: CameraPositionState,
    onStopClick: () -> Unit,
    onStatsClick: () -> Unit,
) {
    BaseScreen {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Mapa
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false, // Ukrywamy domyślny przycisk
                        compassEnabled = false
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = state.isLocationEnabled // Niebieska kropka
                    )
                ) {
                    if (state.routePoints.isNotEmpty()) {
                        Polyline(
                            points = state.routePoints,
                            color = MaterialTheme.colorScheme.tertiary,
                            width = 12f,
                            geodesic = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Statystyki i Przyciski
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "TIME",
                        value = state.timerValue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "DISTANCE",
                        value = state.distanceValue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "STEPS",
                        value = state.stepsValue,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onStatsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                ) {
                    Text(
                        text = "ALL WALK STATS",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onStopClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.size(92.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = "STOP",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(75.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                maxLines = 1
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Preview zostawiamy tylko do podglądu w Android Studio (z mockiem),
// ale nie wpływa on na kod produkcyjny.
@Preview
@Composable
fun WalkScreenPreview() {
    PetCareTheme {
        val mockState = WalkState(
            timerValue = "27:34",
            distanceValue = "1.7 km",
            stepsValue = "2313",
            routePoints = listOf()
        )
        val cameraState = rememberCameraPositionState()

        WalkScreen(
            state = mockState,
            cameraPositionState = cameraState,
            onStopClick = {},
            onStatsClick = {}
        )
    }
}