package com.example.chorpolish

import android.os.Bundle
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import java.util.*
import com.example.chorpolish.ui.game.GamePhase
import com.example.chorpolish.ui.game.GameViewModel
import com.example.chorpolish.ui.game.PlayerSetupScreen
import com.example.chorpolish.ui.game.RoleRevealScreen
import com.example.chorpolish.ui.game.GuessingScreen
import com.example.chorpolish.ui.game.ResultScreen
import com.example.chorpolish.ui.history.HistoryScreen
import com.example.chorpolish.ui.history.HistoryViewModel
import com.example.chorpolish.ui.game.LetterSetPlayScreen
import com.example.chorpolish.ui.game.LetterSetRevealScreen
import com.example.chorpolish.ui.menu.MainMenuScreen
import com.example.chorpolish.ui.menu.GameModeSelectionScreen
import com.example.chorpolish.ui.game.GameMode
import com.example.chorpolish.ui.settings.SettingsScreen
import com.example.chorpolish.ui.rules.GameRulesScreen
import com.example.chorpolish.ui.multiplayer.*
import com.example.chorpolish.ui.navigation.NavDestination
import com.example.chorpolish.ui.theme.ChorPolishTheme
import com.example.chorpolish.utils.MusicType
import com.example.chorpolish.utils.SoundManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val darkThemeEnabled by mainViewModel.darkThemeEnabled.collectAsState()
            val language by mainViewModel.language.collectAsState()
            val premiumThemesUnlocked by mainViewModel.premiumThemesUnlocked.collectAsState()

            val isDarkTheme = if (premiumThemesUnlocked) (darkThemeEnabled ?: isSystemInDarkTheme()) else false

            // Apply Language
            val locale = Locale.forLanguageTag(language)
            val configuration = LocalConfiguration.current
            configuration.setLocale(locale)
            val context = LocalContext.current
            Locale.setDefault(locale)
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            
            CompositionLocalProvider(
                LocalConfiguration provides configuration
            ) {
                ChorPolishTheme(darkTheme = isDarkTheme) {
                    MainNavigation(soundManager)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.onDestroy()
    }
}

@Composable
fun MainNavigation(soundManager: SoundManager) {
    val context = LocalContext.current
    val adManager = remember { com.example.chorpolish.utils.AdManager() }
    val gameViewModel: GameViewModel = viewModel()
    
    LaunchedEffect(Unit) {
        (context as? Activity)?.let { adManager.loadInterstitialAd(it) }
    }
    val historyViewModel: HistoryViewModel = viewModel()
    val multiplayerViewModel: MultiplayerViewModel = viewModel()
    val settingsViewModel: com.example.chorpolish.ui.settings.SettingsViewModel = hiltViewModel()
    
    val uiState by gameViewModel.uiState.collectAsState()
    val historyUiState by historyViewModel.uiState.collectAsState()
    val multiplayerUiState by multiplayerViewModel.uiState.collectAsState()
    
    val backStack = rememberNavBackStack(NavDestination.MainMenu)

    LaunchedEffect(backStack.last()) {
        val currentDestination = backStack.last()
        when (currentDestination) {
            is NavDestination.MainMenu, is NavDestination.PlayerSetup -> soundManager.playBackgroundMusic(MusicType.MAIN_MENU)
            is NavDestination.MultiplayerLobby -> soundManager.playBackgroundMusic(MusicType.LOBBY)
            is NavDestination.RoleReveal, is NavDestination.Guessing, is NavDestination.Result -> soundManager.playBackgroundMusic(MusicType.GAMEPLAY)
            is NavDestination.History -> soundManager.playBackgroundMusic(MusicType.RESULT)
            else -> { /* Keep current music */ }
        }
    }

    LaunchedEffect(uiState.showRules) {
        if (uiState.showRules) {
            if (backStack.last() !is NavDestination.GameRules) {
                backStack.add(NavDestination.GameRules)
            }
        }
    }

    LaunchedEffect(Unit) {
        multiplayerViewModel.navigationEvent.collect { destination ->
            if (backStack.last() != destination) {
                backStack.add(destination)
            }
        }
    }

    val isMultiplayer = multiplayerUiState.roomCode != null

    // Offline Phase Transitions
    LaunchedEffect(uiState.phase) {
        if (isMultiplayer) return@LaunchedEffect
        
        when (uiState.phase) {
            GamePhase.ROLE_REVEAL -> {
                if (backStack.last() !is NavDestination.RoleReveal) {
                    backStack.add(NavDestination.RoleReveal)
                }
            }
            GamePhase.GUESSING -> {
                if (backStack.last() !is NavDestination.Guessing) {
                    backStack.add(NavDestination.Guessing)
                }
            }
            GamePhase.RESULT -> {
                if (backStack.last() !is NavDestination.Result) {
                    backStack.add(NavDestination.Result)
                }
            }
            GamePhase.PLAYING -> {
                if (backStack.last() !is NavDestination.LetterSetPlay) {
                    backStack.add(NavDestination.LetterSetPlay)
                }
            }
            GamePhase.SETUP -> {
                // Keep current stack or reset
            }
        }
    }

    LaunchedEffect(multiplayerUiState.phase) {
        if (!isMultiplayer) return@LaunchedEffect
        
        when (multiplayerUiState.phase) {
            "REVEAL" -> {
                if (backStack.last() !is NavDestination.RoleReveal) {
                    backStack.add(NavDestination.RoleReveal)
                }
            }
            "GUESSING" -> {
                if (backStack.last() !is NavDestination.Guessing) {
                    backStack.add(NavDestination.Guessing)
                }
            }
            "RESULT" -> {
                if (backStack.last() !is NavDestination.Result) {
                    backStack.add(NavDestination.Result)
                }
            }
            "PLAYING" -> {
                if (backStack.last() !is NavDestination.LetterSetPlay) {
                    backStack.add(NavDestination.LetterSetPlay)
                }
            }
            "FINISHED" -> {
                if (backStack.last() !is NavDestination.History) {
                    backStack.add(NavDestination.History)
                }
            }
            "LOBBY" -> {
                 if (backStack.size > 1 && backStack.last() != NavDestination.MultiplayerLobby && backStack.last() != NavDestination.PlayerSetup) {
                    backStack.add(NavDestination.MultiplayerLobby)
                 }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                NavDisplay(
                    backStack = backStack,
                    onBack = { 
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                        }
                    },
                    entryProvider = entryProvider {
                        entry<NavDestination.MainMenu> {
                            MainMenuScreen(
                                onLocalGame = { backStack.add(NavDestination.GameModeSelection(isMultiplayer = false)) },
                                onMultiplayer = { backStack.add(NavDestination.GameModeSelection(isMultiplayer = true)) },
                                onHistory = { backStack.add(NavDestination.History) },
                                onSettings = { backStack.add(NavDestination.Settings) }
                            )
                        }
                        entry<NavDestination.GameModeSelection> { destination ->
                            GameModeSelectionScreen(
                                isMultiplayer = destination.isMultiplayer,
                                onModeSelected = { mode ->
                                    if (mode == GameMode.CHOR_POLISH) {
                                        backStack.add(NavDestination.MatchTypeSelection(destination.isMultiplayer, mode.name))
                                    } else {
                                        // Letter Set skips match type selection and uses default rounds
                                        gameViewModel.setMaxRounds(8) // Default for Letter Set
                                        gameViewModel.setGameMode(mode)
                                        if (destination.isMultiplayer) {
                                            backStack.add(NavDestination.MultiplayerEntry)
                                        } else {
                                            backStack.add(NavDestination.PlayerSetup)
                                        }
                                    }
                                },
                                onBack = { backStack.removeAt(backStack.size - 1) }
                            )
                        }
                        entry<NavDestination.MatchTypeSelection> { destination ->
                            val unlocked by settingsViewModel.longMatchUnlocked.collectAsState()

                            com.example.chorpolish.ui.menu.MatchTypeSelectionScreen(
                                isMultiplayer = destination.isMultiplayer,
                                gameMode = destination.gameMode,
                                isLongMatchUnlocked = unlocked,
                                onMatchSelected = { rounds ->
                                    gameViewModel.setMaxRounds(rounds)
                                    gameViewModel.setGameMode(GameMode.valueOf(destination.gameMode))
                                    if (destination.isMultiplayer) {
                                        backStack.add(NavDestination.MultiplayerEntry)
                                    } else {
                                        backStack.add(NavDestination.PlayerSetup)
                                    }
                                },
                                onBack = { backStack.removeAt(backStack.size - 1) }
                            )
                        }
                        entry<NavDestination.PlayerSetup> {
                            PlayerSetupScreen(
                                uiState = uiState,
                                onUpdateName = { index, name -> gameViewModel.updatePlayerName(index, name) },
                                onStartGame = {
                                    gameViewModel.startGame()
                                },
                                onNavigateToSettings = {
                                    backStack.add(NavDestination.Settings)
                                }
                            )
                        }
                        entry<NavDestination.MultiplayerEntry> {
                            MultiplayerEntryScreen(
                                error = multiplayerUiState.error,
                                onJoinRoom = { name, code ->
                                    multiplayerViewModel.joinRoom(name, code)
                                },
                                onCreateRoom = { name ->
                                    multiplayerViewModel.createRoom(name, uiState.gameMode.name, uiState.maxRounds)
                                },
                                onBack = {
                                    backStack.removeAt(backStack.size - 1)
                                }
                            )
                        }
                        entry<NavDestination.MultiplayerLobby> {
                        MultiplayerLobbyScreen(
                            roomCode = multiplayerUiState.roomCode ?: "------",
                            players = multiplayerUiState.players,
                            myName = multiplayerUiState.myName,
                            isHost = multiplayerUiState.isHost,
                            onStartGame = { mode ->
                                multiplayerViewModel.triggerStart(mode)
                            },
                            onLeaveRoom = {
                                backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }
                        entry<NavDestination.RoleReveal> {
                            if (isMultiplayer) {
                                MultiplayerRoleRevealScreen(multiplayerViewModel, multiplayerUiState)
                            } else {
                                if (uiState.gameMode == GameMode.LETTER_SET) {
                                    LetterSetRevealScreen(
                                        uiState = uiState,
                                        onPlayerSawCards = { playerId ->
                                            gameViewModel.playerSawRole(playerId)
                                        },
                                        onRevealCards = {
                                            gameViewModel.playSoundEffect(com.example.chorpolish.utils.SoundEffect.CARD_FLIP)
                                        }
                                    )
                                } else {
                                    RoleRevealScreen(
                                        uiState = uiState,
                                        onPlayerSawRole = { playerId ->
                                            gameViewModel.playerSawRole(playerId)
                                        },
                                        onRevealCard = { role ->
                                            gameViewModel.playRoleAnnouncement(role)
                                            gameViewModel.playSoundEffect(com.example.chorpolish.utils.SoundEffect.CARD_FLIP)
                                        },
                                        onCountdownTick = {
                                            gameViewModel.playSoundEffect(com.example.chorpolish.utils.SoundEffect.COUNTDOWN_TICK)
                                        }
                                    )
                                }
                            }
                        }
                        entry<NavDestination.LetterSetPlay> {
                            if (isMultiplayer) {
                                MultiplayerLetterSetPlayScreen(multiplayerViewModel, multiplayerUiState)
                            } else {
                                LetterSetPlayScreen(
                                    uiState = uiState,
                                    onPassCard = { from, to, card ->
                                        gameViewModel.passCard(from, to, card)
                                    }
                                )
                            }
                        }
                        entry<NavDestination.Guessing> {
                            if (isMultiplayer) {
                                MultiplayerGuessingScreen(multiplayerViewModel, multiplayerUiState)
                            } else {
                                GuessingScreen(
                                    uiState = uiState,
                                    onMakeGuess = { player ->
                                        gameViewModel.makeGuess(player)
                                        backStack.add(NavDestination.Result)
                                    }
                                )
                            }
                        }
                        entry<NavDestination.Result> {
                            if (isMultiplayer) {
                                MultiplayerResultScreen(
                                    viewModel = multiplayerViewModel, 
                                    state = multiplayerUiState, 
                                    onFinish = { 
                                        adManager.showInterstitialAd(context as Activity) {
                                            // Clear all screens from backstack and go to MainMenu
                                            while (backStack.size > 0) {
                                                backStack.removeAt(backStack.size - 1)
                                            }
                                            backStack.add(NavDestination.MainMenu)
                                            backStack.add(NavDestination.History)
                                        }
                                    }
                                )
                            } else {
                                ResultScreen(
                                    uiState = uiState,
                                    onNextRound = {
                                        // Pop the Result screen before starting next round
                                        if (backStack.size > 1) {
                                            backStack.removeAt(backStack.size - 1)
                                        }
                                        if (uiState.currentRound > uiState.maxRounds) {
                                            // This was a "PLAY AGAIN" case after final results
                                            adManager.showInterstitialAd(context as Activity) {
                                                gameViewModel.startNewRound()
                                            }
                                        } else {
                                            gameViewModel.startNewRound()
                                        }
                                    },
                                    onFinishGame = {
                                        adManager.showInterstitialAd(context as Activity) {
                                            gameViewModel.resetGame()
                                            // Reset backstack to MainMenu then add History
                                            while (backStack.size > 0) {
                                                backStack.removeAt(backStack.size - 1)
                                            }
                                            backStack.add(NavDestination.MainMenu)
                                            backStack.add(NavDestination.History)
                                        }
                                    }
                                )
                            }
                        }
                        entry<NavDestination.History> {
                            HistoryScreen(
                                uiState = historyUiState,
                                onBack = {
                                    backStack.removeAt(backStack.size - 1)
                                },
                                onClear = {
                                    historyViewModel.clearHistory()
                                }
                            )
                        }
                        entry<NavDestination.Settings> {
                            SettingsScreen(
                                onBack = {
                                    backStack.removeAt(backStack.size - 1)
                                }
                            )
                        }
                        entry<NavDestination.GameRules> {
                            GameRulesScreen(
                                onDismiss = {
                                    gameViewModel.dismissRules()
                                    backStack.removeAt(backStack.size - 1)
                                }
                            )
                        }
                    }
                )

                if (multiplayerUiState.isReconnecting) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(Modifier.height(16.dp))
                            Text(stringResource(R.string.reconnecting_label), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
