package com.rakibul.chorpolish.ui.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakibul.chorpolish.data.local.SessionManager
import com.rakibul.chorpolish.data.remote.api.ChorPolishApi
import com.rakibul.chorpolish.data.remote.model.ClaimReferralRequest
import com.rakibul.chorpolish.data.remote.model.RemoteReferral
import com.rakibul.chorpolish.data.rewards.RewardManager
import com.rakibul.chorpolish.data.rewards.RewardSource
import com.rakibul.chorpolish.data.rewards.RewardTransaction
import com.rakibul.chorpolish.data.rewards.RewardType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RewardsUiState(
    val currentPoints: Int = 0,
    val totalPointsEarned: Int = 0,
    val history: List<RewardTransaction> = emptyList(),
    val pointsBySource: Map<String, Int> = emptyMap(),
    val referralCode: String = "",
    val referralHistory: List<RemoteReferral> = emptyList(),
    val isReferred: Boolean = false,
    val isLoading: Boolean = false,
    val isReferralLoading: Boolean = false,
    val referralError: String? = null,
    val error: String? = null,
    val claimSuccess: String? = null
)

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val rewardManager: RewardManager,
    private val api: ChorPolishApi,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    init {
        loadRewards()
        fetchReferralInfo()
    }

    fun loadRewards() {
        val points = rewardManager.getCurrentPoints()
        val history = rewardManager.getRewardHistory()
        
        val totalEarned = history.sumOf { it.points }
        val bySource = history.groupBy { it.getSource().name }
            .mapValues { entry -> entry.value.sumOf { it.points } }

        _uiState.update {
            it.copy(
                currentPoints = points,
                totalPointsEarned = totalEarned,
                history = history,
                pointsBySource = bySource
            )
        }
    }

    fun fetchReferralInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isReferralLoading = true, referralError = null) }
            try {
                var pId = sessionManager.playerId.first()
                if (pId.isEmpty()) {
                    pId = java.util.UUID.randomUUID().toString()
                    sessionManager.savePlayerId(pId)
                }

                val response = api.getReferralInfo(pId)
                if (response.isSuccessful) {
                    val info = response.body()
                    _uiState.update {
                        it.copy(
                            referralCode = info?.referralCode ?: "",
                            referralHistory = info?.history ?: emptyList(),
                            isReferred = info?.referredBy != null,
                            isReferralLoading = false,
                            referralError = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isReferralLoading = false, 
                            referralError = "Unable to load referral code" 
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isReferralLoading = false, 
                        referralError = if (e is java.io.IOException) "Internet connection required" else "Unable to load referral code"
                    ) 
                }
            }
        }
    }

    fun claimReferral(code: String) {
        if (code.isBlank()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, claimSuccess = null) }
            try {
                val pId = sessionManager.playerId.first()
                val response = api.claimReferral(ClaimReferralRequest(pId, code))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val result = response.body()!!
                    
                    // Award points locally
                    rewardManager.addPoints(
                        result.rewardPoints ?: 50,
                        result.transactionId ?: "REFERRAL_${System.currentTimeMillis()}",
                        RewardType.REFERRAL,
                        RewardSource.REFERRAL,
                        "Referral Program",
                        null
                    )
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            claimSuccess = "Referral code applied! You earned ${result.rewardPoints} points.",
                            isReferred = true 
                        )
                    }
                    loadRewards()
                    fetchReferralInfo()
                } else {
                    val errorMsg = response.body()?.error ?: "Invalid referral code or already used."
                    _uiState.update { it.copy(isLoading = false, error = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Connection error. Please try again.") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, claimSuccess = null) }
    }
}
