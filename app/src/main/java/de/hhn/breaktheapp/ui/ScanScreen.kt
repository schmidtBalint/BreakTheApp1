package de.hhn.breaktheapp.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.hhn.breaktheapp.data.openAppSettings
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(viewModel: ScanViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Umgebungs-App") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Wo bin ich gerade?",
                style = MaterialTheme.typography.headlineSmall
            )
            LocationCard(state)
            LocationButton(viewModel)
        }
    }
}

@Composable
private fun LocationCard(state: LocationState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Standort", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            when (state) {
                LocationState.Idle -> Text("Noch kein Standort abgefragt.")
                LocationState.Loading -> CircularProgressIndicator()
                is LocationState.Success -> {
                    Text("Breite: ${format(state.lat)}")
                    Text("Länge: ${format(state.lon)}")
                    Text("Genauigkeit: ${state.accuracy.toInt()} m")
                }
                is LocationState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    if (state.showSettings) {
                        val context = LocalContext.current
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { openAppSettings(context) }) {
                            Text("Einstellungen öffnen")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationButton(viewModel: ScanViewModel) {
    val activity = LocalContext.current as Activity

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // genau ODER ungefähr reicht
        val granted = result.values.any { it }
        if (granted) {
            viewModel.loadLocation()
        } else {
            // Abgelehnt und kein Rationale mehr: dauerhaft verweigert, das System fragt nicht mehr
            val permanently = !activity.shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            viewModel.onPermissionDenied(permanently)
        }
    }

    Button(
        onClick = {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Standort abrufen")
    }
}

private fun format(value: Double) = String.format(Locale.GERMANY, "%.5f", value)
