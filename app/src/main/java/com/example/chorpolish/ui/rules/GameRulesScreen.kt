package com.rakibul.chorpolish.ui.rules

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rakibul.chorpolish.R

@Composable
fun GameRulesScreen(
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    var currentStep by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = when(currentStep) {
                        0 -> R.drawable.role_raja
                        1 -> R.drawable.logo // Reverted to logo
                        else -> R.drawable.role_mantri
                    }),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay Gradient/Fade
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                startY = 300f
                            )
                        )
                )
                
                Text(
                    text = when(currentStep) {
                        0 -> "Chor Polish Rules"
                        1 -> "Letter Set Rules"
                        else -> "How to Use the App"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when(currentStep) {
                    0 -> {
                        RuleItem(
                            icon = Icons.Rounded.Groups,
                            title = "Players",
                            description = "This game is played between 4 players."
                        )
                        RuleItem(
                            icon = Icons.Rounded.Gavel,
                            title = "Roles",
                            description = "Roles are: King (1000 pts), Minister (800 pts), Police (500 pts), and Thief (0 pts)."
                        )
                        RuleItem(
                            icon = Icons.Rounded.Check,
                            title = "The Guess",
                            description = "Police must identify the Thief. Correct guess gives points to Police, otherwise Thief wins them."
                        )
                        RuleItem(
                            icon = Icons.Rounded.EmojiEvents,
                            title = "Winning",
                            description = "Points accumulate each round. The player with the highest total score wins the game!"
                        )
                    }
                    1 -> {
                        RuleItem(
                            icon = Icons.Rounded.Description,
                            title = "The Deck",
                            description = "16 cards total (4 of each: A, B, C, D) distributed among 4 players."
                        )
                        RuleItem(
                            icon = Icons.Rounded.Sync,
                            title = "Gameplay",
                            description = "Players pass one card at a time to the next player clockwise."
                        )
                        RuleItem(
                            icon = Icons.Rounded.Star,
                            title = "Objective",
                            description = "Be the first to collect all 4 cards of the same letter (e.g., AAAA)."
                        )
                        RuleItem(
                            icon = Icons.Rounded.EmojiEvents,
                            title = "Winning",
                            description = "The first person to complete their set wins the round and earns 100 points!"
                        )
                    }
                    2 -> {
                        RuleItem(
                            icon = Icons.Rounded.Smartphone,
                            title = "Local Game",
                            description = "Play with friends on a single device by passing the phone when prompted."
                        )
                        RuleItem(
                            icon = Icons.Rounded.Public,
                            title = "Multiplayer",
                            description = "Create or join a room using a 6-digit code. You can also invite online players directly!"
                        )
                        RuleItem(
                            icon = Icons.Rounded.Info,
                            title = "History & Stats",
                            description = "Check the History section to see your past games, global stats, and leaderboard."
                        )
                        RuleItem(
                            icon = Icons.Rounded.Info,
                            title = "Settings",
                            description = "Customize your experience with music, sound effects, voice announcements, and themes."
                        )
                    }
                }
            }

            // Bottom Buttons
            Column(modifier = Modifier.padding(24.dp)) {
                Button(
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                            // Reset scroll for next ruleset
                            // scrollState.scrollTo(0) // Need a scope for this, skipping for now
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(if (currentStep < 2) "NEXT RULE" else "GET STARTED", fontWeight = FontWeight.Bold)
                }
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SKIP ALL", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun RuleItem(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
