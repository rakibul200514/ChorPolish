package com.rakibul.chorpolish.ui.rewards

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakibul.chorpolish.data.rewards.RewardConfig
import com.rakibul.chorpolish.data.rewards.RewardSource
import com.rakibul.chorpolish.data.rewards.RewardTransaction
import com.rakibul.chorpolish.ui.theme.GameSecondary
import com.rakibul.chorpolish.ui.theme.SurfaceGlass
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    onBack: () -> Unit,
    viewModel: RewardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Refresh data whenever the screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadRewards()
        viewModel.fetchReferralInfo()
    }

    // Success/Error dialogs
    if (uiState.error != null || uiState.claimSuccess != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = { Text(if (uiState.error != null) "Error" else "Success") },
            text = { Text(uiState.error ?: uiState.claimSuccess ?: "") },
            confirmButton = {
                Button(onClick = { viewModel.clearMessages() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "ORBIXA REWARDS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                PointsDisplay(uiState.currentPoints)
                Spacer(Modifier.height(32.dp))
            }

            item {
                StatsSection(uiState)
                Spacer(Modifier.height(32.dp))
            }

            item {
                InviteFriendsCard(
                    referralCode = uiState.referralCode,
                    isReferralLoading = uiState.isReferralLoading,
                    referralError = uiState.referralError,
                    onRetry = { viewModel.fetchReferralInfo() },
                    onCopy = {
                        if (uiState.referralCode.isNotEmpty()) {
                            clipboardManager.setText(AnnotatedString(uiState.referralCode))
                        }
                    },
                    onShare = {
                        if (uiState.referralCode.isNotEmpty()) {
                            val referralLink = android.net.Uri.parse("https://play.google.com/store/apps/details")
                                .buildUpon()
                                .appendQueryParameter("id", "com.rakibul.chorpolish")
                                .appendQueryParameter("referrer", "referral_code=${uiState.referralCode}")
                                .build()
                                .toString()

                            val shareMessage = "🎮 Join me on Orbixa!\n\n" +
                                    "Play fun mini games and puzzles.\n\n" +
                                    "Use my referral code: ${uiState.referralCode}\n\n" +
                                    "Download Orbixa:\n$referralLink"

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Invite"))
                        }
                    }
                )
                Spacer(Modifier.height(32.dp))
            }

            if (!uiState.isReferred) {
                item {
                    ClaimReferralSection(
                        isLoading = uiState.isLoading,
                        onClaim = { code -> viewModel.claimReferral(code) }
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }

            item {
                Text(
                    "EARN POINTS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = GameSecondary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                EarnPointsSection()
                Spacer(Modifier.height(32.dp))
            }

            if (uiState.referralHistory.isNotEmpty()) {
                item {
                    Text(
                        "REFERRAL HISTORY",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = GameSecondary,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    uiState.referralHistory.forEach { ref ->
                        ReferralHistoryItem(ref)
                        Spacer(Modifier.height(12.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            item {
                Text(
                    "REWARD HISTORY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = GameSecondary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            }

            if (uiState.history.isEmpty()) {
                item {
                    EmptyHistoryState()
                }
            } else {
                items(uiState.history) { transaction ->
                    RewardHistoryItem(transaction)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun InviteFriendsCard(
    referralCode: String,
    isReferralLoading: Boolean,
    referralError: String?,
    onRetry: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.People, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(12.dp))
            Text("Invite Friends", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Earn 50 points for every verified referral", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            
            Spacer(Modifier.height(24.dp))
            
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isReferralLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GameSecondary, strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("LOADING...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = GameSecondary, letterSpacing = 2.sp)
                    } else if (referralError != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(referralError, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                            TextButton(onClick = onRetry) {
                                Text("RETRY", color = GameSecondary, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(referralCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = GameSecondary, letterSpacing = 2.sp)
                        Spacer(Modifier.width(16.dp))
                        IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.ContentCopy, "Copy", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = referralCode.isNotEmpty(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Rounded.Share, null)
                Spacer(Modifier.width(12.dp))
                Text("SHARE INVITE", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ClaimReferralSection(isLoading: Boolean, onClaim: (String) -> Unit) {
    var code by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Have a Referral Code?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter code (e.g. ORB123)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = { onClaim(code) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading && code.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("CLAIM REWARD", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReferralHistoryItem(referral: com.rakibul.chorpolish.data.remote.model.RemoteReferral) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.PersonAdd, null, tint = GameSecondary)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Friend Joined", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text(referral.status, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
            }
            Text("+50", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = GameSecondary)
        }
    }
}

@Composable
fun PointsDisplay(points: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Stars,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = GameSecondary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Your Points",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                String.format("%,d", points),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                ),
                color = Color.White
            )
            Text(
                "Orbixa Points",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GameSecondary
            )
        }
    }
}

@Composable
fun StatsSection(uiState: RewardsUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = "Total Earned",
            value = uiState.totalPointsEarned.toString(),
            icon = Icons.Rounded.TrendingUp,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Games Played",
            value = uiState.history.size.toString(),
            icon = Icons.Rounded.Gamepad,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = Color.White)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun EarnPointsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EarnCard("Nut Slot", "+${RewardConfig.POINTS_LEVEL_COMPLETION} per level", Icons.Rounded.Extension)
        EarnCard("Letter Set", "+${RewardConfig.POINTS_LEVEL_COMPLETION} per level", Icons.Rounded.TextFormat)
        EarnCard("Chor Polish", "+${RewardConfig.POINTS_LEVEL_COMPLETION} per round", Icons.Rounded.Groups)
        EarnCard("Arrow Escape", "+${RewardConfig.POINTS_LEVEL_COMPLETION} per level", Icons.Rounded.Navigation)
    }
}

@Composable
fun EarnCard(title: String, subtitle: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = GameSecondary)
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun RewardHistoryItem(transaction: RewardTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when(transaction.getSource()) {
                RewardSource.NUT_SLOT -> Icons.Rounded.Extension
                RewardSource.LETTER_SET -> Icons.Rounded.TextFormat
                RewardSource.CHOR_POLISH -> Icons.Rounded.Groups
                RewardSource.ARROW_ESCAPE -> Icons.Rounded.Navigation
                else -> Icons.Rounded.Star
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.White)
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.getGameName() ?: "System Reward",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                val description = if (transaction.getLevelNumber() != null) "Level ${transaction.getLevelNumber()} Completed" else "Round Completed"
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
                
                val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                Text(
                    sdf.format(Date(transaction.getTimestamp())),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            Text(
                "+${transaction.getPoints()}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = GameSecondary
            )
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Rounded.History,
            null,
            modifier = Modifier.size(64.dp).alpha(0.2f),
            tint = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No rewards yet",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.5f)
        )
        Text(
            "Complete games to start earning Orbixa Points.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.3f),
            textAlign = TextAlign.Center
        )
    }
}
