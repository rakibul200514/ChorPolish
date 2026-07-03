package com.example.chorpolish.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
 import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chorpolish.R
import com.example.chorpolish.data.model.GameRole
import com.example.chorpolish.ui.menu.DecorativeShapes
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleRevealScreen(
    uiState: GameUiState,
    onPlayerSawRole: (Int) -> Unit,
    onRevealCard: (GameRole) -> Unit = {},
    onCountdownTick: () -> Unit = {}
) {
    val currentPlayerIndex = uiState.players.indexOfFirst { !it.hasSeenRole }
    val currentPlayer = uiState.players.getOrNull(currentPlayerIndex)

    var isRevealed by remember(currentPlayerIndex) { mutableStateOf(false) }
    var countdown by remember(currentPlayerIndex) { mutableIntStateOf(3) }
    var showNextButton by remember(currentPlayerIndex) { mutableStateOf(false) }

    LaunchedEffect(isRevealed) {
        if (isRevealed) {
            countdown = 3
            while (countdown > 0) {
                delay(1000)
                countdown--
                if (countdown > 0) onCountdownTick()
            }
            isRevealed = false
            showNextButton = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.secret_reveal_title), 
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        )
                    )
                )
        ) {
            DecorativeShapes()

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (currentPlayer != null) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        stringResource(R.string.pass_phone_to),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        currentPlayer.name,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val maxWidth = maxWidth
                        val contentWidth = if (maxWidth > 600.dp) 400.dp else maxWidth
                        
                        Box(modifier = Modifier.width(contentWidth).align(Alignment.Center)) {
                            RoleCard(
                                playerName = currentPlayer.name,
                                role = currentPlayer.currentRole,
                                isRevealed = isRevealed,
                                countdown = countdown,
                                onReveal = { 
                                    if (!showNextButton && !isRevealed) {
                                        isRevealed = true
                                        currentPlayer.currentRole?.let { onRevealCard(it) }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(48.dp))

                    if (showNextButton) {
                        Button(
                            onClick = { 
                                onPlayerSawRole(currentPlayer.id)
                                showNextButton = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(20.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                stringResource(R.string.i_have_seen_my_role),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(12.dp))
                            Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(24.dp))
                        }
                    } else if (!isRevealed) {
                        Text(
                            stringResource(R.string.tap_instruction),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Extra space at bottom to ensure scrolling past the Banner Ad
                    Spacer(Modifier.height(100.dp))
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text("Loading next phase...", modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getRoleName(role: GameRole?): String {
    return when(role) {
        GameRole.RAJA -> "King"
        GameRole.MANTRI -> "Minister"
        GameRole.SIPAHI -> "Police"
        GameRole.CHOR -> "Thief"
        null -> "???"
    }
}

@Composable
fun RoleCard(
    playerName: String,
    role: GameRole?,
    isRevealed: Boolean,
    countdown: Int,
    onReveal: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(8.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16f * density
            }
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(enabled = !isRevealed, onClick = onReveal),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRevealed) MaterialTheme.colorScheme.surface 
                             else MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {}
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        stringResource(R.string.tap_to_reveal),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (role != null) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = role.imageRes),
                                contentDescription = null,
                                modifier = Modifier.size(180.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                    
                    Text(
                        stringResource(R.string.you_are).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        getRoleName(role),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(32.dp))
                    
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { countdown / 3f },
                            modifier = Modifier.size(80.dp),
                            strokeWidth = 6.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                        Text(
                            countdown.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
