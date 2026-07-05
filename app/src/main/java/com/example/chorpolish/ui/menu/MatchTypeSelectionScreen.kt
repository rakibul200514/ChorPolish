package com.rakibul.chorpolish.ui.menu

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rakibul.chorpolish.ui.game.GameMode
import com.rakibul.chorpolish.utils.AdManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchTypeSelectionScreen(
    isMultiplayer: Boolean,
    gameMode: String,
    isLongMatchUnlocked: Boolean, // Legacy permanent unlock (not used now for temporary)
    onMatchSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val adManager = remember { AdManager() }
    val scrollState = rememberScrollState()
    
    // Track temporary unlocks for this session only
    var tempLongUnlocked by remember { mutableStateOf(false) }
    var tempCustomUnlocked by remember { mutableStateOf(false) }

    var customRounds by remember { mutableStateOf("10") }
    var showCustomDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        (context as? Activity)?.let { adManager.loadRewardAd(it) }
    }

    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Custom Rounds") },
            text = {
                OutlinedTextField(
                    value = customRounds,
                    onValueChange = { if (it.all { char -> char.isDigit() }) customRounds = it },
                    label = { Text("Enter number of rounds") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val rounds = customRounds.toIntOrNull() ?: 10
                    onMatchSelected(rounds)
                    showCustomDialog = false
                }) {
                    Text("START")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("RECOMMENDED MODES", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MatchTypeCard(
                title = "🟢 Quick Match",
                subtitle = "5 Rounds",
                color = Color(0xFF4CAF50),
                onClick = { onMatchSelected(5) }
            )

            MatchTypeCard(
                title = "🔵 Classic Match ⭐",
                subtitle = "8 Rounds",
                color = Color(0xFF2196F3),
                isDefault = true,
                onClick = { onMatchSelected(8) }
            )

            MatchTypeCard(
                title = "🟣 Long Match",
                subtitle = "16 Rounds",
                color = Color(0xFF9C27B0),
                isLocked = !tempLongUnlocked,
                onClick = { 
                    if (tempLongUnlocked) {
                        onMatchSelected(16)
                    } else {
                        (context as? Activity)?.let { activity ->
                            adManager.showRewardAd(activity) {
                                tempLongUnlocked = true
                            }
                        }
                    }
                }
            )

            MatchTypeCard(
                title = "🟠 Custom Match",
                subtitle = "Your Choice",
                color = Color(0xFFFF9800),
                isLocked = !tempCustomUnlocked,
                onClick = { 
                    if (tempCustomUnlocked) {
                        showCustomDialog = true
                    } else {
                        (context as? Activity)?.let { activity ->
                            adManager.showRewardAd(activity) {
                                tempCustomUnlocked = true
                            }
                        }
                    }
                }
            )
            
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun MatchTypeCard(
    title: String,
    subtitle: String,
    color: Color,
    isDefault: Boolean = false,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = if (isDefault) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
                    if (isDefault) {
                        Spacer(Modifier.width(8.dp))
                        Text("(Default)", style = MaterialTheme.typography.labelSmall, color = color)
                    }
                }
                Text(subtitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            
            if (isLocked) {
                Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = color)
            } else if (isDefault) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = "Recommended", tint = color)
            }
        }
    }
}
