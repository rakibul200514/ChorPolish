package com.rakibul.chorpolish.data.remote.model

import com.rakibul.chorpolish.data.model.GameRole
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomRequest(
    @Json(name = "playerName") val playerName: String,
    @Json(name = "playerId") val playerId: String,
    @Json(name = "gameMode") val gameMode: String? = "CHOR_POLISH",
    @Json(name = "maxRounds") val maxRounds: Int? = 8
)

@JsonClass(generateAdapter = true)
data class JoinRoomRequest(
    @Json(name = "playerName") val playerName: String,
    @Json(name = "playerId") val playerId: String,
    @Json(name = "roomCode") val roomCode: String
)

@JsonClass(generateAdapter = true)
data class RemotePlayer(
    @Json(name = "name") val name: String? = null,
    @Json(name = "playerId") val playerId: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "score") val score: Int? = 0,
    @Json(name = "hasSeenRole") val hasSeenRole: Boolean? = false,
    @Json(name = "isDisconnected") val isDisconnected: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class RoomResponse(
    @Json(name = "roomCode") val roomCode: String? = null,
    @Json(name = "players") val players: List<RemotePlayer>? = emptyList(),
    @Json(name = "host") val host: String? = null,
    @Json(name = "phase") val phase: String? = "LOBBY",
    @Json(name = "gameMode") val gameMode: String? = "CHOR_POLISH",
    @Json(name = "isStarted") val isStarted: Boolean? = false,
    @Json(name = "currentRound") val currentRound: Int? = 1,
    @Json(name = "maxRounds") val maxRounds: Int? = 8,
    @Json(name = "mantriPlayer") val mantriPlayer: String? = null,
    @Json(name = "rajaPlayer") val rajaPlayer: String? = null,
    @Json(name = "chorPlayer") val chorPlayer: String? = null,
    @Json(name = "sipahiPlayer") val sipahiPlayer: String? = null,
    @Json(name = "sipahiPlayerId") val sipahiPlayerId: String? = null,
    @Json(name = "guessOptions") val guessOptions: List<String>? = emptyList(),
    @Json(name = "selectedChorGuess") val selectedChorGuess: String? = null,
    @Json(name = "isGuessCorrect") val isGuessCorrect: Boolean? = null,
    @Json(name = "playerCards") val playerCards: Map<String, List<String>>? = emptyMap(),
    @Json(name = "winner") val winner: String? = null,
    @Json(name = "currentPlayerTurn") val currentPlayerTurn: Int? = 0
)

@JsonClass(generateAdapter = true)
data class JoinEmit(
    @Json(name = "roomCode") val roomCode: String,
    @Json(name = "playerId") val playerId: String
)

@JsonClass(generateAdapter = true)
data class RoleSeenEmit(
    @Json(name = "roomCode") val roomCode: String,
    @Json(name = "playerId") val playerId: String
)

@JsonClass(generateAdapter = true)
data class GuessEmit(
    @Json(name = "roomCode") val roomCode: String,
    @Json(name = "guessedPlayerName") val guessedPlayerName: String
)

@JsonClass(generateAdapter = true)
data class OnlinePlayerRemote(
    @Json(name = "playerId") val playerId: String,
    @Json(name = "name") val name: String,
    @Json(name = "isAvailable") val isAvailable: Boolean = true
)
