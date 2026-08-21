package com.rakibul.chorpolish.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralInfoResponse(
    @Json(name = "playerId") val playerId: String,
    @Json(name = "referralCode") val referralCode: String,
    @Json(name = "referredBy") val referredBy: String?,
    @Json(name = "history") val history: List<RemoteReferral>
)

@JsonClass(generateAdapter = true)
data class RemoteReferral(
    @Json(name = "status") val status: String,
    @Json(name = "createdAt") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class ClaimReferralRequest(
    @Json(name = "playerId") val playerId: String,
    @Json(name = "referralCode") val referralCode: String
)

@JsonClass(generateAdapter = true)
data class ClaimReferralResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String?,
    @Json(name = "rewardPoints") val rewardPoints: Int?,
    @Json(name = "transactionId") val transactionId: String?,
    @Json(name = "error") val error: String?
)
