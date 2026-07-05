package com.rakibul.chorpolish.ui.multiplayer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.rakibul.chorpolish.R

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.rakibul.chorpolish.ui.menu.DecorativeShapes
import com.rakibul.chorpolish.ui.menu.VideoBanner
import com.rakibul.chorpolish.utils.QrUtils
import androidx.compose.ui.graphics.asImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerLobbyScreen(
    roomCode: String,
    players: List<String>,
    myName: String,
    isHost: Boolean,
    initialGameMode: String,
    onStartGame: (String, Int) -> Unit,
    onLeaveRoom: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedMode by remember(initialGameMode) { mutableStateOf(initialGameMode) }
    var selectedRounds by remember { mutableIntStateOf(8) }
    var showCustomRoundsDialog by remember { mutableStateOf(false) }
    var customRoundsInput by remember { mutableStateOf("10") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.multiplayer_setup_title),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onLeaveRoom) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.cancel))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Video Banner added to Lobby
            VideoBanner(
                    videoFileName = "lobby_banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1604f / 720f)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .border(
                            BorderStroke(
                                2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                            RoundedCornerShape(24.dp)
                        )
                )

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.room_code_label).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 2.sp
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = if (roomCode == "000000" || roomCode.isBlank()) "------" else roomCode,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 8.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (roomCode != "000000" && roomCode.isNotBlank()) {
                                IconButton(
                                    onClick = { 
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Room Code", roomCode)
                                        clipboard.setPrimaryClip(clip)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.ContentCopy, 
                                        contentDescription = stringResource(R.string.copy_label),
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            stringResource(R.string.waiting_for_players, players.size),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { players.size / 4f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )

                        Spacer(Modifier.height(24.dp))

                        // QR Code Section
                        if (roomCode != "000000" && roomCode.isNotBlank()) {
                            val qrBitmap = remember(roomCode) { QrUtils.generateQrCode(roomCode, 300) }
                            qrBitmap?.let {
                                Card(
                                    modifier = Modifier.size(160.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = "Room QR Code",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "SCAN TO JOIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    "PLAYERS",
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                players.forEach { player ->
                    PlayerLobbyItem(name = player, isMe = player == myName)
                    Spacer(Modifier.height(12.dp))
                }
                
                repeat(4 - players.size) {
                    PlayerLobbyItem(name = "Waiting...", isMe = false, isPlaceholder = true)
                    Spacer(Modifier.height(12.dp))
                }

                if (isHost) {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        "CHOOSE GAME MODE",
                        modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameModeChip(
                            text = "CHOR POLISH",
                            isSelected = selectedMode == "CHOR_POLISH",
                            onClick = { selectedMode = "CHOR_POLISH" },
                            modifier = Modifier.weight(1f)
                        )
                        GameModeChip(
                            text = "LETTER SET (ABCD)",
                            isSelected = selectedMode == "LETTER_SET",
                            onClick = { selectedMode = "LETTER_SET" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(
                        "CHOOSE MATCH LENGTH",
                        modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoundOptionChip("5 RDS", 5, selectedRounds == 5) { selectedRounds = 5 }
                        RoundOptionChip("8 RDS", 8, selectedRounds == 8) { selectedRounds = 8 }
                        RoundOptionChip("16 RDS", 16, selectedRounds == 16) { selectedRounds = 16 }
                        RoundOptionChip(
                            if (selectedRounds != 5 && selectedRounds != 8 && selectedRounds != 16) "$selectedRounds RDS" else "CUSTOM",
                            if (selectedRounds != 5 && selectedRounds != 8 && selectedRounds != 16) selectedRounds else -1,
                            selectedRounds != 5 && selectedRounds != 8 && selectedRounds != 16
                        ) { showCustomRoundsDialog = true }
                    }
                }

                Spacer(Modifier.height(32.dp))

                if (isHost) {
                    Button(
                        onClick = { onStartGame(selectedMode, selectedRounds) },
                        enabled = players.size == 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            stringResource(R.string.start_game),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                            Spacer(Modifier.width(16.dp))
                            Text(
                                stringResource(R.string.waiting_for_host),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(60.dp))
        }
    }

    if (showCustomRoundsDialog) {
        AlertDialog(
            onDismissRequest = { showCustomRoundsDialog = false },
            title = { Text("Custom Match Rounds") },
            text = {
                OutlinedTextField(
                    value = customRoundsInput,
                    onValueChange = { if (it.length <= 2) customRoundsInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Enter number of rounds") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val r = customRoundsInput.toIntOrNull() ?: 10
                    selectedRounds = r.coerceIn(2, 50)
                    showCustomRoundsDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomRoundsDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun RoundOptionChip(
    text: String,
    value: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GameModeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        tonalElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlayerLobbyItem(name: String, isMe: Boolean, isPlaceholder: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaVal by if (isPlaceholder) {
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Card(
        modifier = Modifier.fillMaxWidth().graphicsLayer { this.alpha = alphaVal },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isPlaceholder) 0.3f else 0.8f)
        ),
        border = if (isMe) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaceholder) 0.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isPlaceholder) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) 
                        else MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = if (isPlaceholder) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (isMe) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isPlaceholder) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isMe) {
                    Text(
                        "YOU",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }
            }
            if (!isPlaceholder && !isMe) {
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Rounded.PlayArrow, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
