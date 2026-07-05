package com.rakibul.chorpolish.ui.menu

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.rakibul.chorpolish.R
import com.rakibul.chorpolish.ui.theme.GameSecondary
import com.rakibul.chorpolish.ui.components.BannerAd

@Composable
fun MainMenuScreen(
    onLocalGame: () -> Unit,
    onMultiplayer: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_bounce")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    Scaffold(
        bottomBar = {
            BannerAd(modifier = Modifier.padding(bottom = 8.dp))
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Video Banner
            VideoBanner(
                videoFileName = "menu_banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1604f / 720f)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .border(
                        BorderStroke(
                            2.dp,
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            )

            Spacer(Modifier.height(32.dp))

            // Logo and Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Chor Polish Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(logoScale)
                )
                Text(
                    stringResource(R.string.app_name).uppercase(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    ),
                    color = Color.White
                )
                Text(
                    "THE ULTIMATE MYSTERY GAME",
                    style = MaterialTheme.typography.labelMedium,
                    color = GameSecondary,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))

            // Menu Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    text = "LOCAL GAME",
                    icon = Icons.Rounded.Group,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onLocalGame
                )

                MenuButton(
                    text = "MULTIPLAYER",
                    icon = Icons.Rounded.Public,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = onMultiplayer
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SmallMenuButton(
                        text = "History",
                        icon = Icons.Rounded.History,
                        modifier = Modifier.weight(1f),
                        onClick = onHistory
                    )
                    SmallMenuButton(
                        text = "Settings",
                        icon = Icons.Rounded.Settings,
                        modifier = Modifier.weight(1f),
                        onClick = onSettings
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bottom Version Info
            Text(
                stringResource(R.string.version_label),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoBanner(
    videoFileName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            val resourceId = context.resources.getIdentifier(videoFileName, "raw", context.packageName)
            if (resourceId != 0) {
                val videoUri = android.net.Uri.parse("android.resource://${context.packageName}/$resourceId")
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = modifier
    )
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Text(
                text,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

@Composable
fun SmallMenuButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White
        )
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White)
    }
}

@Composable
fun DecorativeShapes() {
    Box(modifier = Modifier.fillMaxSize()) {
        val infiniteTransition = rememberInfiniteTransition(label = "shapes")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Icon(
            imageVector = Icons.Rounded.Casino,
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-100).dp, y = (-50).dp)
                .rotate(rotation)
                .scale(0.8f),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        )

        Icon(
            imageVector = Icons.Rounded.VerifiedUser,
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .rotate(-rotation)
                .scale(0.8f),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
        )
    }
}
