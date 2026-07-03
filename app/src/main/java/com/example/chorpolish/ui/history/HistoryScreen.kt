package com.example.chorpolish.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chorpolish.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onBack: () -> Unit,
    onClear: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.global_stats_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Clear History")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.leaderboard),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            itemsIndexed(uiState.stats) { index, stat ->
                LeaderboardItem(stat, index + 1)
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            if (uiState.rounds.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.recent_rounds),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                }

                items(uiState.rounds.take(20)) { round ->
                    HistoryItem(round)
                }
            } else {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_game_history), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(stat: PlayerStat, rank: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when(rank) {
                1 -> Color(0xFFFFD700).copy(alpha = 0.1f) // Gold
                2 -> Color(0xFFC0C0C0).copy(alpha = 0.1f) // Silver
                3 -> Color(0xFFCD7F32).copy(alpha = 0.1f) // Bronze
                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (rank <= 3) {
                    Icon(
                        Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = when(rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            else -> Color(0xFFCD7F32)
                        },
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        rank.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                } else {
                    Text(
                        "#$rank",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(stat.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.rounds_played, stat.gamesPlayed), style = MaterialTheme.typography.bodySmall)
            }
            Text(
                stat.totalScore.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun HistoryItem(round: com.example.chorpolish.data.local.entity.GameRound) {
    val isLetterSet = round.chorPlayerName == "LETTER_SET"
    
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    if (isLetterSet) "LETTER SET" else "CHOR POLISH",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isLetterSet) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
                if (!isLetterSet) {
                    Text(
                        if (round.isGuessCorrect) stringResource(R.string.success) else stringResource(R.string.failed),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (round.isGuessCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        "Winner: ${round.mantriPlayerName}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(12.dp))
            
            val scores = listOf(
                round.player1Name to round.player1Score,
                round.player2Name to round.player2Score,
                round.player3Name to round.player3Score,
                round.player4Name to round.player4Score
            )
            
            scores.forEach { (name, score) ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 2.dp), 
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(name, style = MaterialTheme.typography.bodyMedium)
                    Text(stringResource(R.string.pts_label, score), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
