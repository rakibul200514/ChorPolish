package com.rakibul.chorpolish.ui.multiplayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rakibul.chorpolish.R

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.rounded.QrCodeScanner
import com.rakibul.chorpolish.ui.theme.GameSecondary

import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerEntryScreen(
    error: String?,
    onJoinRoom: (String, String) -> Unit,
    onCreateRoom: (String) -> Unit,
    onScanQr: () -> Unit,
    onBack: () -> Unit,
    viewModel: MultiplayerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    
    // Load saved name once and register online
    LaunchedEffect(Unit) {
        if (state.myName.isNotEmpty()) {
            name = state.myName
        }
        // Force refresh online list when entering this screen
        viewModel.refreshOnlinePlayers()
        viewModel.registerOnline()
    }
    var roomCode by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Sync with scanned code
    LaunchedEffect(state.scannedRoomCode) {
        state.scannedRoomCode?.let { code ->
            roomCode = code
            viewModel.setScannedRoomCode(null) // Clear it
            
            // Auto-join ONLY if name is already provided
            if (name.isNotBlank()) {
                onJoinRoom(name, code)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.multiplayer_setup_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.cancel))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(32.dp))

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    viewModel.updateMyName(it)
                },
                label = { Text(stringResource(R.string.your_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                singleLine = true
            )

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.join_existing_room), fontWeight = FontWeight.Bold)
                        IconButton(onClick = onScanQr) {
                            Icon(Icons.Rounded.QrCodeScanner, contentDescription = "Scan QR", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = roomCode,
                        onValueChange = { if (it.length <= 6) roomCode = it },
                        label = { Text(stringResource(R.string.six_digit_code)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Rounded.VpnKey, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onJoinRoom(name, roomCode) },
                        enabled = name.isNotBlank() && roomCode.length == 6,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.join_room))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            
            Text(stringResource(R.string.or_label), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    // Optimized: Prevent multiple clicks
                    onCreateRoom(name)
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.create_new_room))
            }
        }
    }
}
