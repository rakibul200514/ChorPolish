package com.rakibul.chorpolish.data.remote.api

import com.rakibul.chorpolish.data.remote.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChorPolishApi {
    @POST("rooms/create")
    suspend fun createRoom(@Body request: RoomRequest): Response<RoomResponse>

    @POST("rooms/join")
    suspend fun joinRoom(@Body request: JoinRoomRequest): Response<RoomResponse>

    @GET("rooms/{roomCode}")
    suspend fun getRoomStatus(@Path("roomCode") roomCode: String): Response<RoomResponse>

    @GET("leaderboard")
    suspend fun getGlobalLeaderboard(): Response<List<Map<String, Any>>>

    // Referral Endpoints
    @GET("referrals/me")
    suspend fun getReferralInfo(@retrofit2.http.Query("playerId") playerId: String): Response<ReferralInfoResponse>

    @POST("referrals/claim")
    suspend fun claimReferral(@Body request: ClaimReferralRequest): Response<ClaimReferralResponse>
}
