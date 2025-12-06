package com.example.petcare.presentation.walk

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
import androidx.compose.material3.Surface
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
import com.google.android.gms.location.LocationServices
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

    // 1. Klient do pobrania pozycji startowej (tylko dla UI mapy, żeby nie startować w Warszawie)
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // 2. Stan uprawnień
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    // 3. Konfiguracja kamery
    val defaultLocation = LatLng(52.2297, 21.0122) // Domyślnie Wwa (fallback)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    // 4. Launcher uprawnień - pytamy na starcie
    LaunchedEffect(Unit) {
        if(!hasLocationPermission) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 5. Logic Start: Gdy mamy uprawnienia -> Startujemy serwis ORAZ ustawiamy kamerę na usera
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            // A. Powiadom ViewModel żeby odpalił serwis
            viewModel.onPermissionGranted()

            // B. Pobierz ostatnią lokalizację i przesuń mapę (naprawa problemu Warszawy)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        // Przesuwamy kamerę bez animacji na start
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 17f)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    // 6. Śledzenie trasy (gdy już idziesz i przybywają nowe punkty z serwisu)
    LaunchedEffect(state.routePoints) {
        if(state.routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(state.routePoints.last(), 17f)
            )
        }
    }

    // Wywołanie ekranu
    WalkScreen(
        state = state.copy(isLocationEnabled = hasLocationPermission),
        cameraPositionState = cameraPositionState,
        onStopClick = {
            viewModel.onStopClick() // Stop logiczny
            onStopClick() // Nawigacja
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
    // Zastąpiono BaseScreen zwykłym Surface
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
                        myLocationButtonEnabled = false,
                        compassEnabled = false
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = state.isLocationEnabled
                    )
                ) {
                    if(state.routePoints.isNotEmpty()) {
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


@Preview
@Composable
fun WalkScreenPreview() {
    // Zastąpiono PetCareTheme standardowym MaterialTheme
    MaterialTheme {
        val warsaw = LatLng(52.2297, 21.0122)
        val cameraState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(warsaw, 15f)
        }
        val mockRoute = listOf(
            LatLng(52.2297, 21.0122),
            LatLng(52.2300, 21.0130),
            LatLng(52.2310, 21.0140)
        )
        val mockState = WalkState(
            timerValue = "27:34",
            distanceValue = "1.7 km",
            stepsValue = "2313",
            routePoints = mockRoute
        )

        WalkScreen(
            state = mockState,
            cameraPositionState = cameraState,
            onStopClick = {},
            onStatsClick = {}
        )
    }
}