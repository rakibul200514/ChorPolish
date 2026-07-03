package com.example.chorpolish.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import com.example.chorpolish.R
import com.example.chorpolish.data.local.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    private var mediaPlayer: MediaPlayer? = null
    private var currentMusicType: MusicType? = null
    
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    init {
        tts = TextToSpeech(context, this)
        
        // Watch for background music setting changes
        scope.launch {
            sessionManager.backgroundMusicEnabled.collect { enabled ->
                if (!enabled) {
                    stopBackgroundMusic()
                } else if (mediaPlayer == null && currentMusicType != null) {
                    // Resume playing if music was turned ON and we have a type set
                    playBackgroundMusic(currentMusicType!!)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsInitialized = true
        }
    }

    fun speakRoleAnnouncement(roleName: String) {
        scope.launch {
            if (sessionManager.characterVoiceEnabled.first()) {
                if (isTtsInitialized) {
                    tts?.speak("You are the $roleName", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    fun playSoundEffect(effect: SoundEffect) {
        scope.launch {
            if (sessionManager.soundEffectsEnabled.first()) {
                when (effect) {
                    SoundEffect.BUTTON_CLICK -> {
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)
                    }
                    SoundEffect.COUNTDOWN_TICK -> {
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 50)
                    }
                    SoundEffect.CARD_FLIP -> {
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                    }
                    SoundEffect.GUESS_CORRECT -> {
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                        if (isTtsInitialized) {
                            tts?.speak("Correct Guess!", TextToSpeech.QUEUE_ADD, null, null)
                        }
                    }
                    SoundEffect.GUESS_WRONG -> {
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
                        if (isTtsInitialized) {
                            tts?.speak("Wrong Guess!", TextToSpeech.QUEUE_ADD, null, null)
                        }
                    }
                    SoundEffect.WINNER_SOUND -> {
                        launch {
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 200)
                            delay(250)
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_5, 200)
                            delay(250)
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_9, 400)
                        }
                    }
                    else -> {
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 100)
                    }
                }
            }
        }
    }

    fun playBackgroundMusic(musicType: MusicType) {
        scope.launch {
            // Update current type even if disabled, so we know what to play when turned ON
            if (currentMusicType == musicType && mediaPlayer?.isPlaying == true) return@launch
            currentMusicType = musicType
            
            if (!sessionManager.backgroundMusicEnabled.first()) return@launch
            
            stopBackgroundMusic()
            
            val resId = when(musicType) {
                MusicType.MAIN_MENU -> R.raw.main_menu_music
                MusicType.LOBBY -> R.raw.lobby_music
                MusicType.GAMEPLAY -> R.raw.gameplay_music
                MusicType.RESULT -> R.raw.result_music
            }
            
            try {
                mediaPlayer = MediaPlayer.create(context, resId).apply {
                    isLooping = true
                    setVolume(0.6f, 0.6f) // Set to 60% volume for background
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopBackgroundMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        soundPool.release()
        toneGenerator.release()
        stopBackgroundMusic()
    }
}

enum class SoundEffect {
    BUTTON_CLICK,
    CARD_FLIP,
    ROOM_CREATED,
    PLAYER_JOINED,
    ROLE_REVEAL,
    COUNTDOWN_TICK,
    GUESS_CORRECT,
    GUESS_WRONG,
    SCORE_UPDATE,
    WINNER_SOUND,
    CONFETTI
}

enum class MusicType {
    MAIN_MENU,
    LOBBY,
    GAMEPLAY,
    RESULT
}
