package com.rakibul.chorpolish.ui.multiplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.rakibul.chorpolish.ui.game.LetterCard
import com.rakibul.chorpolish.ui.menu.DecorativeShapes

@Composable
fun MultiplayerLetterSetPlayScreen(
    viewModel: MultiplayerViewModel,
    state: MultiplayerUiState
) {
    val myCards = state.playerCards[state.myPlayerId] ?: emptyList()
    val activePlayerName = state.players.getOrNull(state.currentPlayerTurn) ?: "..."
    val activePlayer = state.remotePlayers.find { it.name == activePlayerName }
    val isMyTurn = state.playerIds.getOrNull(state.currentPlayerTurn) == state.myPlayerId
    val isRobotTurn = activePlayer?.isDisconnected == true

    val nextPlayerIndex = (state.currentPlayerTurn + 1) % 4
    val nextPlayerName = state.players.getOrNull(nextPlayerIndex) ?: "Next Player"
    val nextPlayerId = state.playerIds.getOrNull(nextPlayerIndex) ?: ""
    
    var selectedIndex by remember(state.currentPlayerTurn) { mutableIntStateOf(-1) }

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
                if (isMyTurn && !isRobotTurn) "YOUR TURN" else "WAITING FOR ${activePlayerName.uppercase()} ${if(isRobotTurn) "(ROBOT)" else ""}",
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
                itemsIndexed(myCards) { index, card ->
                    LetterCard(
                        letter = card,
                        isSelected = selectedIndex == index,
                        onClick = { if (isMyTurn && !isRobotTurn) selectedIndex = index }
                    )
                }
            }

            if (isMyTurn && !isRobotTurn) {
                if (selectedIndex != -1) {
                    Button(
                        onClick = {
                            viewModel.passCard(nextPlayerId, myCards[selectedIndex]) 
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Rounded.Sync, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("PASS '${myCards[selectedIndex]}' TO $nextPlayerName", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        "Select one card to pass to $nextPlayerName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    if (isRobotTurn) "$activePlayerName is disconnected! Robot is choosing..." 
                    else "$activePlayerName is choosing a card...",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                if (isRobotTurn) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
            }
            
            Spacer(Modifier.height(80.dp))
        }

        ReactionOverlay(state.activeReactions)
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
            EmojiSelector(premiumUnlocked = state.premiumEmojisUnlocked) { viewModel.sendEmoji(it) }
        }
    }
}
