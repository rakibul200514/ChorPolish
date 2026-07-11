package com.rakibul.chorpolish.ui.multiplayer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.rakibul.chorpolish.data.model.GameRole
import com.rakibul.chorpolish.data.model.Player
import com.rakibul.chorpolish.ui.game.*
import com.rakibul.chorpolish.ui.components.BannerAd
import kotlinx.coroutines.delay
import com.rakibul.chorpolish.R
import com.rakibul.chorpolish.utils.SoundEffect
import com.airbnb.lottie.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerRoleRevealScreen(
    viewModel: MultiplayerViewModel,
    state: MultiplayerUiState
) {
    if (state.gameMode == "LETTER_SET") {
        MultiplayerLetterSetRevealContent(viewModel, state)
        return
    }
    
    var isRevealed by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(3) }
    var hasSeenRole by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(isRevealed) {
        if (isRevealed) {
            state.myRole?.let { viewModel.playRoleAnnouncement(it) }
            viewModel.playSoundEffect(com.rakibul.chorpolish.utils.SoundEffect.CARD_FLIP)
            countdown = 3
            while (countdown > 0) {
                delay(1000)
                countdown--
                if (countdown > 0) viewModel.playSoundEffect(com.rakibul.chorpolish.utils.SoundEffect.COUNTDOWN_TICK)
            }
            isRevealed = false
            hasSeenRole = true
            viewModel.roleSeen()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "YOUR SECRET ROLE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(32.dp))

        if (!isRevealed && !hasSeenRole) {
            // Using the unified RoleCard from RoleRevealScreen
            RoleCard(
                playerName = state.myName,
                role = state.myRole,
                isRevealed = false,
                countdown = 3,
                onReveal = { isRevealed = true }
            )
        } else if (isRevealed) {
            RoleCard(
                playerName = state.myName,
                role = state.myRole,
                isRevealed = true,
                countdown = countdown,
                onReveal = {}
            )
        } else {
            Text(
                "Waiting for others...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun RoleCardLarge(role: GameRole) {
    Card(
        modifier = Modifier.size(width = 260.dp, height = 400.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(32.dp))
            Text(
                "YOU ARE THE",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                role.roleName.uppercase(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MultiplayerGuessingScreen(
    viewModel: MultiplayerViewModel,
    state: MultiplayerUiState
) {
    // Robust check for Police role
    val isPolice = (state.myPlayerId == state.sipahiPlayerId && state.myPlayerId.isNotEmpty()) || 
                   (state.myName == state.sipahiPlayer && state.myName.isNotEmpty()) ||
                   (state.myRole == GameRole.SIPAHI)

    // Log for debugging if nobody is identified as police locally
    LaunchedEffect(state.phase, state.sipahiPlayer, state.sipahiPlayerId) {
        if (state.phase == "GUESSING" && !isPolice) {
            android.util.Log.d("Multiplayer", "Waiting for police: ${state.sipahiPlayer} (ID: ${state.sipahiPlayerId}). I am: ${state.myName} (ID: ${state.myPlayerId})")
        }
    }

    val sipahiIsDisconnected = state.remotePlayers.find { it.name == state.sipahiPlayer }?.isDisconnected ?: false
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "POLICE GUESSING",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(Modifier.height(24.dp))

            if (isPolice && !sipahiIsDisconnected) {
                Text(
                    "Choose who is the THIEF",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))
                
                state.guessOptions.forEach { playerName ->
                    Card(
                        onClick = {
                            // Optimized: Immediate UI feedback
                            viewModel.makeGuess(playerName)
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            playerName,
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                val statusText = if (sipahiIsDisconnected) {
                    "Police is disconnected! Robot is choosing..."
                } else if (state.sipahiPlayer != null) {
                    "Police (${state.sipahiPlayer}) is guessing..."
                } else {
                    "Waiting for Police to be assigned..."
                }
                
                Text(
                    statusText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
        }
        
        ReactionOverlay(state.activeReactions)
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
            EmojiSelector(premiumUnlocked = state.premiumEmojisUnlocked) { viewModel.sendEmoji(it) }
        }
    }
}

@Composable
fun MultiplayerResultScreen(
    viewModel: MultiplayerViewModel,
    state: MultiplayerUiState,
    onFinish: () -> Unit
) {
    if (state.gameMode == "LETTER_SET") {
        MultiplayerLetterSetResultContent(viewModel, state, onFinish)
        return
    }
    
    val isCorrect = state.isGuessCorrect ?: false
    val winnerColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF5252)
    val isFinished = state.phase == "FINISHED"

    // Calculate Rank based on TOTAL scores
    val sortedScores = state.totalScores.sortedByDescending { it.second }
    val myRank = (sortedScores.indexOfFirst { it.first == state.myName } + 1).coerceAtLeast(1)

    var showPersonalResult by remember { mutableStateOf(true) }

    LaunchedEffect(isFinished) {
        if (isFinished) {
            showPersonalResult = true
            if (myRank == 1) {
                viewModel.playSoundEffect(SoundEffect.WINNER_SOUND)
            }
            delay(4000)
            showPersonalResult = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isFinished && showPersonalResult) {
            PersonalCelebrationScreen(rank = myRank, myName = state.myName)
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    val context = LocalContext.current
                    val adManager = remember { com.rakibul.chorpolish.utils.AdManager() }
                    ResultScreen(
                        uiState = GameUiState(
                            isGuessCorrect = state.isGuessCorrect,
                            roundResults = state.roundScoresWithRoles.map { (name, score, role) ->
                                PlayerScore(name, role ?: GameRole.CHOR, score)
                            },
                            currentRound = state.currentRound,
                            maxRounds = state.maxRounds
                        ),
                        onNextRound = {
                            if (state.isHost) {
                                if (state.currentRound >= state.maxRounds) {
                                    adManager.showInterstitialAd(context as Activity) {
                                        viewModel.triggerNextRound()
                                    }
                                } else {
                                    viewModel.triggerNextRound()
                                }
                            }
                        },
                        onFinishGame = {
                            if (state.isHost) {
                                adManager.showInterstitialAd(context as Activity) {
                                    viewModel.triggerFinishGame()
                                    onFinish()
                                }
                            }
                        },
                        showButtons = state.isHost
                    )
                }

                // Show personal rank clearly if game is finished
                if (isFinished) {
                    Surface(
                        tonalElevation = 12.dp,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "YOUR FINAL RANK: ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "#$myRank",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        ReactionOverlay(state.activeReactions)
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
            EmojiSelector(premiumUnlocked = state.premiumEmojisUnlocked) { viewModel.sendEmoji(it) }
        }
    }
}

@Composable
fun PersonalCelebrationScreen(rank: Int, myName: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://assets9.lottiefiles.com/packages/lf20_u4yrau.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    val infiniteTransition = rememberInfiniteTransition(label = "crown_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (rank == 1) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = when (rank) {
                    1 -> "🥇 Rank #1"
                    2 -> "🥈 Rank #2"
                    3 -> "🥉 Rank #3"
                    else -> "4️⃣ Rank #4"
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            when (rank) {
                1 -> {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(glowScale)
                                .graphicsLayer { alpha = 0.3f },
                            shape = CircleShape,
                            color = Color(0xFFFFD700)
                        ) {}
                        Text("👑", fontSize = 80.sp)
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("🎉 Congratulations!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("👑 You are the King!", style = MaterialTheme.typography.titleLarge)
                    Text("🏆 You ruled the game!", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                }
                2 -> {
                    Text("🥈", fontSize = 80.sp)
                    Spacer(Modifier.height(24.dp))
                    Text("🥈 Great Job!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("⭐ Excellent performance!", style = MaterialTheme.typography.titleLarge)
                    Text("Keep playing to become the King.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                }
                3 -> {
                    Text("🥉", fontSize = 80.sp)
                    Spacer(Modifier.height(24.dp))
                    Text("🥉 Good Effort!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("👏 Nice game!", style = MaterialTheme.typography.titleLarge)
                    Text("You'll reach the top next time.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                }
                else -> {
                    Text("😅", fontSize = 80.sp)
                    Spacer(Modifier.height(24.dp))
                    Text("Better Luck Next Time!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("💪 Don't give up.", style = MaterialTheme.typography.titleLarge)
                    Text("Your comeback starts in the next match!", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun MultiplayerLetterSetRevealContent(
    viewModel: MultiplayerViewModel,
    state: MultiplayerUiState
) {
    var hasSeen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.playSoundEffect(com.rakibul.chorpolish.utils.SoundEffect.CARD_FLIP)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Your Initial Cards",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(Modifier.height(32.dp))

            val cards = state.playerCards[state.myPlayerId] ?: emptyList()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                cards.forEach { card ->
                    LetterCardSmall(card)
                }
            }

            Spacer(Modifier.height(48.dp))

            if (!hasSeen) {
                Button(
                    onClick = {
                        hasSeen = true
                        viewModel.roleSeen()
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("I'VE SEEN THEM", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    "Waiting for others to see their cards...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        
        ReactionOverlay(state.activeReactions)
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
            EmojiSelector(premiumUnlocked = state.premiumEmojisUnlocked) { viewModel.sendEmoji(it) }
        }
    }
}

@Composable
fun MultiplayerLetterSetResultContent(
    viewModel: MultiplayerViewModel,
    state: MultiplayerUiState,
    onFinish: () -> Unit
) {
    val isWinner = state.winner == state.myName
    val isLastRound = state.currentRound >= state.maxRounds
    
    // Calculate Rank based on total scores
    val sortedScores = state.totalScores.sortedByDescending { it.second }
    val myEntry = state.totalScores.find { it.first == state.myName }
    val myRank = if (myEntry != null) {
        sortedScores.indexOfFirst { it.first == state.myName } + 1
    } else 4

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLastRound) {
                FinalCelebrationMultiplayer(sortedScores)
            } else {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = if (isWinner) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        if (isWinner) Icons.Rounded.Star else Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                
                Text(
                    if (isWinner) "YOU WON THIS ROUND!" else "ROUND OVER",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = if (isWinner) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary
                )
                
                Text(
                    "${state.winner} is the winner!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    "ROUND ${state.currentRound} / ${state.maxRounds}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    "YOUR CURRENT RANK: #$myRank",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(48.dp))

            if (state.isHost) {
                val context = LocalContext.current
                val adManager = remember { com.rakibul.chorpolish.utils.AdManager() }
                Button(
                    onClick = { 
                        // Optimized: immediate UI feedback
                        if (isLastRound) {
                            adManager.showInterstitialAd(context as Activity) {
                                viewModel.restartMatch()
                            }
                        } else {
                            viewModel.triggerNextRound()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(if (isLastRound) "PLAY AGAIN" else "PLAY NEXT ROUND", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { 
                        adManager.showInterstitialAd(context as Activity) {
                            viewModel.triggerFinishGame() 
                            onFinish()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(if (isLastRound) "DASHBOARD" else "FINISH EARLY", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(if (isLastRound) "Game over. Waiting for host..." else "Waiting for host...", style = MaterialTheme.typography.bodyLarge)
            }
        }
        
        ReactionOverlay(state.activeReactions)
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
            EmojiSelector(premiumUnlocked = state.premiumEmojisUnlocked) { viewModel.sendEmoji(it) }
        }
    }
}

@Composable
fun FinalCelebrationMultiplayer(scores: List<Pair<String, Int>>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BannerAd(modifier = Modifier.padding(bottom = 16.dp))
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
        scores.forEachIndexed { index, pair ->
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
                    Text(pair.first, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("${pair.second} pts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun EmojiSelector(
    premiumUnlocked: Boolean = false,
    onEmojiSelected: (String) -> Unit
) {
    val basicEmojis = listOf("🤝", "💯", "🤖", "👶", "👻", "🥱", "🦁", "⭐")
    val premiumEmojis = listOf("👑", "😂", "🔥", "💎", "🎉", "😈", "🤯", "🏆")
    
    val allEmojis = basicEmojis + premiumEmojis
    
    Card(
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(allEmojis) { emoji ->
                val isPremium = premiumEmojis.contains(emoji)
                val canUse = !isPremium || premiumUnlocked
                
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        emoji,
                        modifier = Modifier
                            .scale(if (canUse) 1f else 0.8f)
                            .clickable(enabled = canUse) { onEmojiSelected(emoji) }
                            .padding(8.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (!canUse) {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = "Locked",
                            modifier = Modifier.size(16.dp).align(Alignment.BottomEnd),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReactionOverlay(reactions: List<EmojiReaction>) {
    Box(modifier = Modifier.fillMaxSize()) {
        reactions.forEach { reaction ->
            key(reaction.id) {
                EmojiReactionItem(reaction)
            }
        }
    }
}

@Composable
fun EmojiReactionItem(reaction: EmojiReaction) {
    val randomX = remember { (10..70).random() }
    val randomY = remember { (15..60).random() }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .offset(x = (randomX * 4).dp, y = (randomY * 8).dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    reaction.playerName, 
                    color = Color.White, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.width(6.dp))
                Text(reaction.emoji, fontSize = 24.sp)
            }
        }
    }
}
