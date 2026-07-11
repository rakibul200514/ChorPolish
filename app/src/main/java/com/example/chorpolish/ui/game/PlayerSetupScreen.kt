package com.rakibul.chorpolish.ui.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rakibul.chorpolish.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSetupScreen(
    uiState: GameUiState,
    onUpdateName: (Int, String) -> Unit,
    onStartGame: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    val isInputValid = uiState.players.filter { !it.isRobot }.all { it.name.isNotBlank() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.player_setup_title), 
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    ) 
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                color = Color.White.copy(alpha = 0.08f), // Darker glass effect
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    var isStartLoading by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            if (!isStartLoading) {
                                isStartLoading = true
                                onStartGame()
                            }
                        },
                        enabled = isInputValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (isStartLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.start_game), style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val maxWidth = maxWidth
                val contentWidth = if (maxWidth > 600.dp) 500.dp else maxWidth
                
                Column(
                    modifier = Modifier.width(contentWidth).align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Chor Polish Logo",
                        modifier = Modifier.size(100.dp).padding(bottom = 12.dp)
                    )

                    Text(
                        "Enter Your Name",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    uiState.players.filter { !it.isRobot }.forEachIndexed { index, player ->
                        OutlinedTextField(
                            value = player.name,
                            onValueChange = { onUpdateName(index, it) },
                            label = { Text("Your Name", color = Color.White.copy(alpha = 0.7f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = Color.White
                                ) 
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                cursorColor = Color.White
                            ),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                    
                    Text(
                        "You will play against 3 Robot players",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    if (!isInputValid && uiState.players.any { it.name.isNotBlank() }) {
                        Text(
                            stringResource(R.string.names_must_be_unique),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    // On small screens, bottom bar might cover content. 
                    // Add extra space at the bottom of the scrollable column.
                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}
