package com.example.chorpolish.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.chorpolish.R
import com.example.chorpolish.data.model.GameRole
import com.example.chorpolish.data.model.Player
import com.example.chorpolish.ui.menu.DecorativeShapes
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    uiState: GameUiState,
    onNextRound: () -> Unit,
    onFinishGame: () -> Unit,
    showButtons: Boolean = true,
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val adManager = remember { com.example.chorpolish.utils.AdManager() }
    val isLastRound = uiState.currentRound >= uiState.maxRounds
    
    if (uiState.gameMode == GameMode.LETTER_SET) {
        LetterSetResultContent(uiState, onNextRound, onFinishGame, showButtons)
        return
    }

    val isCorrect = uiState.isGuessCorrect ?: false
    val scrollState = rememberScrollState()
    
    // Simple Confetti Animation
    val confettiColors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta)
    val particles = remember { 
        List(100) { 
            object {
                var x = Random.nextFloat()
                var y = Random.nextFloat() * -1f
                var speed = 0.01f + Random.nextFloat() * 0.02f
                var color = confettiColors.random()
                var size = 4f + Random.nextFloat() * 8f
            }
        }
    }
    
    if (isCorrect) {
        LaunchedEffect(Unit) {
            while(true) {
                delay(16)
                particles.forEach { p ->
                    p.y += p.speed
                    if (p.y > 1f) {
                        p.y = -0.1f
                        p.x = Random.nextFloat()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (isLastRound) "FINAL RESULTS" else "ROUND ${uiState.currentRound} / ${uiState.maxRounds}", 
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
                    .drawBehind {
                        if (isCorrect && !isLastRound) {
                            particles.forEach { p ->
                                drawCircle(
                                    color = p.color,
                                    radius = p.size,
                                    center = Offset(p.x * size.width, p.y * size.height)
                                )
                            }
                        }
                    }
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Result Banner
            if (isLastRound) {
                FinalCelebration(uiState.players.sortedByDescending { it.sessionScore })
            } else {
                ResultBanner(isCorrect)
            }

            Spacer(Modifier.height(32.dp))

            Text(
                stringResource(R.string.round_scores),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(16.dp))

            // Round Results
            uiState.roundResults.forEach { result ->
                ScoreItem(result)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))

            if (showButtons) {
                if (isLastRound) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                adManager.showInterstitialAd(context as Activity) {
                                    gameViewModel.resetGame()
                                    onNextRound() 
                                }
                            },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Text(
                                "PLAY AGAIN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Button(
                            onClick = onFinishGame,
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(20.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                "DASHBOARD",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onNextRound,
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            stringResource(R.string.next_round),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Extra spacer for emoji selector overlap
            Spacer(Modifier.height(120.dp))
        }
    }
}
}

@Composable
fun ResultBanner(isCorrect: Boolean) {
    val containerColor = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val icon = if (isCorrect) Icons.Rounded.Celebration else Icons.Rounded.Close
    val text = if (isCorrect) stringResource(R.string.guess_correct) else stringResource(R.string.wrong_guess)

    val infiniteTransition = rememberInfiniteTransition(label = "banner_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text.uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun ScoreItem(result: PlayerScore) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 4.dp
                ) {
                    Image(
                        painter = painterResource(id = result.role.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        result.playerName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = CircleShape
                    ) {
                        Text(
                            getRoleNameResult(result.role).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "+${result.score}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = if (result.score > 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
                Text(
                    "PTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun LetterSetResultContent(
    uiState: GameUiState,
    onNextRound: () -> Unit,
    onFinishGame: () -> Unit,
    showButtons: Boolean = true
) {
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
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(48.dp))
            
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Rounded.Celebration,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = Color.White
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                "WINNER!",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                uiState.winner?.name ?: "Someone",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            
            Text(
                "Collected all 4 of a kind!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Display Rankings
            val sortedPlayers = uiState.players.sortedByDescending { it.sessionScore }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sortedPlayers.forEachIndexed { index, player ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (player == uiState.winner) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "#${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = if (player == uiState.winner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                player.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                player.sessionScore.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            if (showButtons) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onFinishGame,
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("FINISH", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onNextRound,
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("REPLAY", fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // Safety spacer for small screens
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
private fun getRoleNameResult(role: GameRole): String {
    return when(role) {
        GameRole.RAJA -> "King"
        GameRole.MANTRI -> "Minister"
        GameRole.SIPAHI -> "Police"
        GameRole.CHOR -> "Thief"
    }
}

@Composable
fun FinalCelebration(players: List<Player>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Rounded.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFFFFD700)
        )
        Text(
            "FINAL RESULTS",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        players.forEachIndexed { index, player ->
            val emoji = when(index) {
                0 -> "🥇"
                1 -> "🥈"
                2 -> "🥉"
                else -> "4️⃣"
            }
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(16.dp))
                    Text(player.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(player.sessionScore.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
