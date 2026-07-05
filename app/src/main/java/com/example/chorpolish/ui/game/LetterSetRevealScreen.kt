package com.rakibul.chorpolish.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rakibul.chorpolish.ui.menu.DecorativeShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterSetRevealScreen(
    uiState: GameUiState,
    onPlayerSawCards: (Int) -> Unit,
    onRevealCards: () -> Unit = {}
) {
    val currentPlayerIndex = uiState.players.indexOfFirst { !it.hasSeenRole }
    val currentPlayer = uiState.players.getOrNull(currentPlayerIndex)
    
    var showCards by remember(currentPlayerIndex) { mutableStateOf(false) }
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
            if (currentPlayer != null) {
                Spacer(Modifier.height(48.dp))
                
                Text(
                    "PASS PHONE TO",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 2.sp
                )
                Text(
                    currentPlayer.name,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(32.dp))

                if (!showCards) {
                    Button(
                        onClick = { 
                            showCards = true
                            onRevealCards()
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("SHOW MY CARDS", fontWeight = FontWeight.Bold)
                    }
                } else {
                    val cards = uiState.playerCards[currentPlayer.id] ?: emptyList()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cards.forEach { card ->
                            LetterCardSmall(card)
                        }
                    }

                    Spacer(Modifier.height(48.dp))

                    Button(
                        onClick = { onPlayerSawCards(currentPlayer.id) },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("I'VE SEEN THEM")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.ArrowForward, contentDescription = null)
                    }
                }
                
                // Extra space at bottom to ensure scrolling past the Banner Ad
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun LetterCardSmall(letter: String) {
    val color = when(letter) {
        "A" -> Color(0xFFFF5252)
        "B" -> Color(0xFF448AFF)
        "C" -> Color(0xFF4CAF50)
        "D" -> Color(0xFFFFC107)
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier.size(70.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(2.dp, color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(letter, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = color)
        }
    }
}
