package com.rakibul.chorpolish.ui.game

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterSetPlayScreen(
    uiState: GameUiState,
    onPassCard: (Int, Int, String) -> Unit
) {
    val currentPlayer = uiState.players[uiState.currentPlayerTurn]
    val nextPlayerIndex = (uiState.currentPlayerTurn + 1) % 4
    val nextPlayer = uiState.players[nextPlayerIndex]
    
    var selectedCard by remember(uiState.currentPlayerTurn) { mutableStateOf<String?>(null) }
    var isConfirmed by remember(uiState.currentPlayerTurn) { mutableStateOf(false) }
    var showPassOverlay by remember(uiState.currentPlayerTurn) { mutableStateOf(!currentPlayer.isRobot) }

    val playerCards = uiState.playerCards[currentPlayer.id] ?: emptyList()
    val humanPlayerId = uiState.players.find { !it.isRobot }?.id ?: 0
    val humanCards = uiState.playerCards[humanPlayerId] ?: emptyList()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "LETTER SET (ABCD)", 
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        if (showPassOverlay && !currentPlayer.isRobot) {
            PassPhoneOverlay(
                playerName = currentPlayer.name,
                onDismiss = { showPassOverlay = false }
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentPlayer.isRobot) {
                    Text(
                        "ROBOT'S TURN",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        currentPlayer.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Show exactly what letter is being passed
                    val cardBeingPassed = uiState.lastPassedCard ?: "a card"
                    
                    Text(
                        "${currentPlayer.name} passed letter '$cardBeingPassed' to ${nextPlayer.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Visual representation of the passed card
                    LetterCardSmall(cardBeingPassed)
                    
                    Spacer(Modifier.height(48.dp))
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        "YOUR TURN",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        currentPlayer.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(32.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(playerCards) { card ->
                            LetterCard(
                                letter = card,
                                isSelected = selectedCard == card,
                                onClick = { if (!isConfirmed) selectedCard = card }
                            )
                        }
                    }

                    if (selectedCard != null) {
                        Button(
                            onClick = {
                                onPassCard(currentPlayer.id, nextPlayer.id, selectedCard!!)
                                isConfirmed = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Rounded.Sync, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text("PASS CARD TO ${nextPlayer.name.uppercase()}", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            "Select a card to pass to ${nextPlayer.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                // Show Human's cards at the bottom even when it's Robot's turn
                if (currentPlayer.isRobot) {
                    Text("Your current cards:", color = Color.White.copy(alpha = 0.6f))
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        humanCards.forEach { card ->
                            Surface(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(card, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LetterCard(
    letter: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when(letter) {
        "A" -> Color(0xFFFF5252)
        "B" -> Color(0xFF448AFF)
        "C" -> Color(0xFF4CAF50)
        "D" -> Color(0xFFFFC107)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .border(
                width = if (isSelected) 4.dp else 0.dp,
                color = if (isSelected) color else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                letter,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
            if (isSelected) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    tint = color
                )
            }
        }
    }
}

@Composable
fun PassPhoneOverlay(
    playerName: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "PASS PHONE TO",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 2.sp
            )
            Text(
                playerName,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("I AM $playerName", fontWeight = FontWeight.Bold)
            }
        }
    }
}
