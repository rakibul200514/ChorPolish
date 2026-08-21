package com.rakibul.chorpolish.data.remote

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.rakibul.chorpolish.data.local.SessionManager
import com.rakibul.chorpolish.data.remote.api.ChorPolishApi
import com.rakibul.chorpolish.data.remote.model.ClaimReferralRequest
import com.rakibul.chorpolish.data.rewards.RewardManager
import com.rakibul.chorpolish.data.rewards.RewardSource
import com.rakibul.chorpolish.data.rewards.RewardType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReferralTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ChorPolishApi,
    private val sessionManager: SessionManager,
    private val rewardManager: RewardManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startTracking() {
        scope.launch {
            if (sessionManager.referralProcessed.first()) return@launch

            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            try {
                                val response = referrerClient.installReferrer
                                val referrerUrl = response.installReferrer
                                handleReferrer(referrerUrl)
                                referrerClient.endConnection()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        else -> {
                            // Handle other error codes if necessary
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next app launch
                }
            })
        }
    }

    private fun handleReferrer(referrerUrl: String?) {
        if (referrerUrl == null) return
        
        scope.launch {
            try {
                // The referrer might be URL encoded
                val decodedUrl = try {
                    URLDecoder.decode(referrerUrl, "UTF-8")
                } catch (e: Exception) {
                    referrerUrl
                }
                
                val params = decodedUrl.split("&")
                var referralCode: String? = null
                
                for (param in params) {
                    if (param.startsWith("referral_code=")) {
                        referralCode = param.substringAfter("referral_code=")
                        break
                    } else if (param.startsWith("referrer=")) {
                        // Sometimes it's nested: referrer=referral_code%3DORB...
                        val nested = param.substringAfter("referrer=")
                        if (nested.contains("referral_code=")) {
                            referralCode = nested.substringAfter("referral_code=")
                        }
                    }
                }

                if (referralCode != null) {
                    processReferral(referralCode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun processReferral(code: String) {
        try {
            var pId = sessionManager.playerId.first()
            if (pId.isEmpty()) {
                // Generate a player ID if it doesn't exist yet (e.g. fresh install)
                pId = java.util.UUID.randomUUID().toString()
                sessionManager.savePlayerId(pId)
            }

            val response = api.claimReferral(ClaimReferralRequest(pId, code))
            if (response.isSuccessful && response.body()?.success == true) {
                val result = response.body()!!
                
                // Award points locally
                rewardManager.addPoints(
                    result.rewardPoints ?: 50,
                    result.transactionId ?: "REFERRAL_INSTALL_${System.currentTimeMillis()}",
                    RewardType.REFERRAL,
                    RewardSource.REFERRAL,
                    "Referral Program",
                    null
                )
                
                sessionManager.setReferralProcessed(true)
            } else {
                val error = response.body()?.error
                // If already referred, mark as processed so we don't keep trying
                if (error?.contains("already", ignoreCase = true) == true) {
                    sessionManager.setReferralProcessed(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
