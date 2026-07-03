package com.example.chorpolish.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import javax.inject.Inject
import javax.inject.Singleton

import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Singleton
class AdManager @Inject constructor() {
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var isInterstitialLoading = false
    private val TAG = "AdManager"
    
    // Testing Ad Unit IDs
    private val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    private val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    private val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    fun loadRewardAd(activity: Activity) {
        if (rewardedAd != null || isAdLoading) return
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, REWARDED_AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                isAdLoading = false
                rewardedAd = null
                Log.e(TAG, "Rewarded ad failed: ${adError.message}")
            }
            override fun onAdLoaded(ad: RewardedAd) {
                isAdLoading = false
                rewardedAd = ad
                Log.d(TAG, "Rewarded ad loaded.")
            }
        })
    }

    fun loadInterstitialAd(activity: Activity) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, INTERSTITIAL_AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                isInterstitialLoading = false
                interstitialAd = null
                Log.e(TAG, "Interstitial ad failed: ${adError.message}")
            }
            override fun onAdLoaded(ad: InterstitialAd) {
                isInterstitialLoading = false
                interstitialAd = ad
                Log.d(TAG, "Interstitial ad loaded.")
            }
        })
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onAdDismissed()
                    loadInterstitialAd(activity)
                }
                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            onAdDismissed()
            loadInterstitialAd(activity)
        }
    }

    fun showRewardAd(activity: Activity, onRewardEarned: () -> Unit) {
        if (rewardedAd != null) {
            rewardedAd?.show(activity) {
                rewardedAd = null
                onRewardEarned()
                loadRewardAd(activity)
            }
        } else {
            Toast.makeText(activity, "Ad not ready yet.", Toast.LENGTH_SHORT).show()
            loadRewardAd(activity)
        }
    }

    fun createBannerAd(activity: Activity): AdView {
        return AdView(activity).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BANNER_AD_UNIT_ID
            loadAd(AdRequest.Builder().build())
        }
    }
}
