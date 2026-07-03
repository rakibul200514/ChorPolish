package com.example.chorpolish.data.remote

import com.example.chorpolish.data.model.GameRole
import com.example.chorpolish.data.remote.api.ChorPolishApi
import com.example.chorpolish.data.remote.model.*
import com.squareup.moshi.Moshi
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultiplayerRepository @Inject constructor(
    private val api: ChorPolishApi,
    private val socket: Socket,
    private val moshi: Moshi
) {
    fun connect() {
        if (!socket.connected()) {
            socket.connect()
        }
    }

    fun disconnect() {
        socket.disconnect()
    }

    suspend fun createRoom(playerName: String, playerId: String, gameMode: String, maxRounds: Int): Result<RoomResponse> {
        return try {
            val response = api.createRoom(RoomRequest(playerName, playerId, gameMode, maxRounds))
            if (response.isSuccessful && response.body() != null) {
                val room = response.body()!!
                room.roomCode?.let { emitJoin(it, playerId) }
                Result.success(room)
            } else {
                Result.failure(Exception("Failed to create room"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinRoom(playerName: String, playerId: String, roomCode: String): Result<RoomResponse> {
        return try {
            val response = api.joinRoom(JoinRoomRequest(playerName, playerId, roomCode))
            if (response.isSuccessful && response.body() != null) {
                emitJoin(roomCode, playerId)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to join room"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun emitJoin(roomCode: String, playerId: String) {
        val data = JSONObject().apply {
            put("roomCode", roomCode)
            put("playerId", playerId)
        }
        socket.emit("joinRoom", data)
    }

    fun triggerStart(roomCode: String, gameMode: String = "CHOR_POLISH") {
        val data = JSONObject().apply {
            put("roomCode", roomCode)
            put("gameMode", gameMode)
        }
        socket.emit("startGame", data)
    }

    fun roleSeen(roomCode: String, playerId: String) {
        val data = JSONObject().apply {
            put("roomCode", roomCode)
            put("playerId", playerId)
        }
        socket.emit("roleSeen", data)
    }

    fun makeGuess(roomCode: String, guessedPlayerName: String) {
        val data = JSONObject().apply {
            put("roomCode", roomCode)
            put("guessedPlayerName", guessedPlayerName)
        }
        socket.emit("makeGuess", data)
    }

    fun passCard(roomCode: String, fromPlayerId: String, toPlayerId: String, card: String) {
        val data = JSONObject().apply {
            put("roomCode", roomCode)
            put("fromPlayerId", fromPlayerId)
            put("toPlayerId", toPlayerId)
            put("card", card)
        }
        socket.emit("passCard", data)
    }

    fun triggerNextRound(roomCode: String) {
        socket.emit("nextRound", roomCode)
    }

    fun triggerFinishGame(roomCode: String) {
        android.util.Log.d("SocketIO", "Emitting finishGame for room: $roomCode")
        val data = JSONObject().apply {
            put("roomCode", roomCode)
        }
        socket.emit("finishGame", data)
    }

    fun triggerRestartMatch(roomCode: String) {
        socket.emit("restartMatch", roomCode)
    }

    fun sendEmoji(roomCode: String, emoji: String, playerName: String) {
        android.util.Log.d("SocketIO", "Sending emoji: $emoji from $playerName in room $roomCode")
        val data = JSONObject().apply {
            put("roomCode", roomCode)
            put("emoji", emoji)
            put("playerName", playerName)
        }
        socket.emit("sendEmoji", data)
    }

    fun observeRoomEvents(): Flow<MultiplayerEvent> = callbackFlow {
        val adapter = moshi.adapter(RoomResponse::class.java)

        val roomUpdatedListener = Emitter.Listener { args ->
            val json = args[0].toString()
            android.util.Log.d("SocketIO", "roomUpdated received: $json")
            try {
                adapter.fromJson(json)?.let {
                    trySend(MultiplayerEvent.RoomUpdated(it))
                }
            } catch (e: Exception) {
                android.util.Log.e("SocketIO", "Error parsing roomUpdated", e)
            }
        }

        val gameStartedListener = Emitter.Listener { args ->
            val json = args[0].toString()
            android.util.Log.d("SocketIO", "gameStarted received: $json")
            try {
                adapter.fromJson(json)?.let {
                    trySend(MultiplayerEvent.GameStarted(it))
                }
            } catch (e: Exception) {
                android.util.Log.e("SocketIO", "Error parsing gameStarted", e)
            }
        }

        val guessResultListener = Emitter.Listener { args ->
            val json = args[0].toString()
            android.util.Log.d("SocketIO", "guessResult received: $json")
            try {
                adapter.fromJson(json)?.let {
                    trySend(MultiplayerEvent.GuessResult(it))
                }
            } catch (e: Exception) {
                android.util.Log.e("SocketIO", "Error parsing guessResult", e)
            }
        }

        val gameFinishedListener = Emitter.Listener { args ->
            android.util.Log.d("SocketIO", "gameFinished received: ${args[0]}")
            try {
                val data = args[0]
                val json = if (data is JSONObject) data.toString() else data.toString()
                adapter.fromJson(json)?.let {
                    trySend(MultiplayerEvent.GameFinished(it))
                }
            } catch (e: Exception) {
                android.util.Log.e("SocketIO", "Error parsing gameFinished", e)
            }
        }

        val emojiListener = Emitter.Listener { args ->
            try {
                val data = args[0]
                val (emoji, playerName) = when (data) {
                    is JSONObject -> data.getString("emoji") to data.getString("playerName")
                    is Map<*, *> -> data["emoji"].toString() to data["playerName"].toString()
                    else -> {
                        val jsonObj = JSONObject(data.toString())
                        jsonObj.getString("emoji") to jsonObj.getString("playerName")
                    }
                }
                trySend(MultiplayerEvent.EmojiReceived(emoji, playerName))
            } catch (e: Exception) {
                android.util.Log.e("SocketIO", "Error parsing emojiReceived: ${args[0]}", e)
            }
        }

        socket.on("roomUpdated", roomUpdatedListener)
        socket.on("gameStarted", gameStartedListener)
        socket.on("guessResult", guessResultListener)
        socket.on("gameFinished", gameFinishedListener)
        socket.on("emojiReceived", emojiListener)

        socket.on(Socket.EVENT_CONNECT) { android.util.Log.d("SocketIO", "Connected!") }
        socket.on(Socket.EVENT_DISCONNECT) { android.util.Log.d("SocketIO", "Disconnected!") }
        socket.on(Socket.EVENT_CONNECT_ERROR) { args -> android.util.Log.e("SocketIO", "Connect Error: ${args[0]}") }

        awaitClose {
            socket.off("roomUpdated", roomUpdatedListener)
            socket.off("gameStarted", gameStartedListener)
            socket.off("guessResult", guessResultListener)
            socket.off("gameFinished", gameFinishedListener)
            socket.off("emojiReceived", emojiListener)
        }
    }
}

sealed class MultiplayerEvent {
    data class RoomUpdated(val room: RoomResponse) : MultiplayerEvent()
    data class GameStarted(val room: RoomResponse) : MultiplayerEvent()
    data class GuessResult(val room: RoomResponse) : MultiplayerEvent()
    data class GameFinished(val room: RoomResponse) : MultiplayerEvent()
    data class EmojiReceived(val emoji: String, val playerName: String) : MultiplayerEvent()
}
