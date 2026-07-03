package com.example.chorpolish.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.example.chorpolish.utils.AdManager
import com.example.chorpolish.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        (context as? Activity)?.let { activity ->
            viewModel.adManager.loadRewardAd(activity)
        }
    }
    
    val soundEffectsEnabled by viewModel.soundEffectsEnabled.collectAsState()
    val backgroundMusicEnabled by viewModel.backgroundMusicEnabled.collectAsState()
    val characterVoiceEnabled by viewModel.characterVoiceEnabled.collectAsState()
    val darkThemeEnabled by viewModel.darkThemeEnabled.collectAsState()
    val language by viewModel.language.collectAsState()
    val premiumThemesUnlocked by viewModel.premiumThemesUnlocked.collectAsState()
    val premiumEmojisUnlocked by viewModel.premiumEmojisUnlocked.collectAsState()
    val longMatchUnlocked by viewModel.longMatchUnlocked.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.language)) },
            text = {
                Column {
                    LanguageOption("English", "en", language) { viewModel.setLanguage(it); showLanguageDialog = false }
                    LanguageOption("Hindi (हिन्दी)", "hi", language) { viewModel.setLanguage(it); showLanguageDialog = false }
                    LanguageOption("Bangla (বাংলা)", "bn", language) { viewModel.setLanguage(it); showLanguageDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsItem(
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                title = stringResource(R.string.sound_effects),
                subtitle = stringResource(R.string.sound_effects_desc),
                trailing = { 
                    Switch(
                        checked = soundEffectsEnabled, 
                        onCheckedChange = { viewModel.setSoundEffectsEnabled(it) }
                    ) 
                }
            )

            SettingsItem(
                icon = Icons.Rounded.MusicNote,
                title = stringResource(R.string.background_music),
                subtitle = stringResource(R.string.background_music_desc),
                trailing = { 
                    Switch(
                        checked = backgroundMusicEnabled, 
                        onCheckedChange = { viewModel.setBackgroundMusicEnabled(it) }
                    ) 
                }
            )

            SettingsItem(
                icon = Icons.Rounded.RecordVoiceOver,
                title = stringResource(R.string.character_voice),
                subtitle = stringResource(R.string.character_voice_desc),
                trailing = { 
                    Switch(
                        checked = characterVoiceEnabled, 
                        onCheckedChange = { viewModel.setCharacterVoiceEnabled(it) }
                    ) 
                }
            )
            
            SettingsItem(
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.language),
                subtitle = when(language) {
                    "hi" -> "Hindi (हिन्दी)"
                    "bn" -> "Bangla (বাংলা)"
                    else -> "English"
                },
                onClick = { showLanguageDialog = true },
                trailing = { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null) }
            )
            
            SettingsItem(
                icon = Icons.Rounded.DarkMode,
                title = stringResource(R.string.dark_theme),
                subtitle = if (premiumThemesUnlocked) stringResource(R.string.dark_theme_desc) else "👑 Premium Feature (Watch Ad to Unlock)",
                trailing = { 
                    if (premiumThemesUnlocked) {
                        Switch(
                            checked = darkThemeEnabled ?: false, 
                            onCheckedChange = { viewModel.setDarkThemeEnabled(it) }
                        )
                    } else {
                        Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            )

            // Premium Features
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            SettingsItem(
                icon = Icons.Rounded.WorkspacePremium,
                title = "Unlock Premium Themes",
                subtitle = if (premiumThemesUnlocked) "👑 Premium Themes Unlocked" else "Watch Ad to Unlock Dark Mode & Themes",
                trailing = {
                    if (!premiumThemesUnlocked) {
                        Button(onClick = {
                            (context as? Activity)?.let { activity ->
                                viewModel.adManager.showRewardAd(activity) {
                                    viewModel.unlockPremiumThemes()
                                }
                            }
                        }) {
                            Text("UNLOCK 👑")
                        }
                    } else {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Unlocked", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )

            SettingsItem(
                icon = Icons.Rounded.AddReaction,
                title = "Unlock Premium Emoji Pack",
                subtitle = if (premiumEmojisUnlocked) "😊 Premium Emojis Unlocked" else "Unlock: 👑 😂 🔥 💎 🎉 😈 🤯 🏆",
                trailing = {
                    if (!premiumEmojisUnlocked) {
                        Button(onClick = {
                            (context as? Activity)?.let { activity ->
                                viewModel.adManager.showRewardAd(activity) {
                                    viewModel.unlockPremiumEmojis()
                                }
                            }
                        }) {
                            Text("UNLOCK 😊")
                        }
                    } else {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Unlocked", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
            
            Spacer(Modifier.height(100.dp))
            
            Text(
                stringResource(R.string.version_label),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    trailing: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing()
        }
    }
}

@Composable
fun LanguageOption(
    name: String,
    code: String,
    currentCode: String,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(code) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, style = MaterialTheme.typography.bodyLarge)
        if (code == currentCode) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
