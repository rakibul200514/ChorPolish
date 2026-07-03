package com.example.chorpolish.ui.multiplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chorpolish.ui.game.LetterCard
import com.example.chorpolish.ui.menu.DecorativeShapes

@Composable
fun MultiplayerLetterSetPlayScreen(
    viewModel: MultiplayerViewModel,
    state: MultiplayerUiState
) {
    val myCards = state.playerCards[state.myPlayerId] ?: emptyList()
    val isMyTurn = state.playerIds.getOrNull(state.currentPlayerTurn) == state.myPlayerId
    val nextPlayerIndex = (state.currentPlayerTurn + 1) % 4
    val nextPlayerName = state.players.getOrNull(nextPlayerIndex) ?: "Next Player"
    val nextPlayerId = state.playerIds.getOrNull(nextPlayerIndex) ?: ""
    
    var selectedCard by remember(state.currentPlayerTurn) { mutableStateOf<String?>(null) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isMyTurn) "YOUR TURN" else "WAITING FOR ${state.players.getOrNull(state.currentPlayerTurn)?.uppercase() ?: "..."}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 2.sp
            )
            
            Spacer(Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(myCards) { card ->
                    LetterCard(
                        letter = card,
                        isSelected = selectedCard == card,
                        onClick = { if (isMyTurn) selectedCard = card }
                    )
                }
            }

            if (isMyTurn) {
                if (selectedCard != null) {
                    Button(
                        onClick = {
                            viewModel.passCard(nextPlayerId, selectedCard!!) 
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Rounded.Sync, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("PASS TO $nextPlayerName", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        "Select a card to pass to $nextPlayerName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    "${state.players.getOrNull(state.currentPlayerTurn) ?: "Player"} is choosing a card...",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(Modifier.height(80.dp))
        }

        ReactionOverlay(state.activeReactions)
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
            EmojiSelector(premiumUnlocked = state.premiumEmojisUnlocked) { viewModel.sendEmoji(it) }
        }
    }
}
