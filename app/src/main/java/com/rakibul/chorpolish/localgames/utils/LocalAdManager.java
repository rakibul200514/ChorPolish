package com.rakibul.chorpolish.localgames.utils;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import androidx.annotation.NonNull;

public class LocalAdManager {
    // TEST IDs - Replace with production IDs for Release
    private static final String INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";

    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private boolean isInterstitialLoading = false;
    private boolean isRewardedLoading = false;

    private static LocalAdManager instance;

    public static synchronized LocalAdManager getInstance() {
        if (instance == null) instance = new LocalAdManager();
        return instance;
    }

    public void loadBanner(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void loadInterstitial(Context context) {
        if (mInterstitialAd != null || isInterstitialLoading) return;

        isInterstitialLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, INTERSTITIAL_ID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                isInterstitialLoading = false;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
                isInterstitialLoading = false;
            }
        });
    }

    public void showInterstitial(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            mInterstitialAd = null;
            loadInterstitial(activity); // Load next
        }
    }

    public void loadRewarded(Context context) {
        if (mRewardedAd != null || isRewardedLoading) return;

        isRewardedLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, REWARDED_ID, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
                isRewardedLoading = false;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mRewardedAd = null;
                isRewardedLoading = false;
            }
        });
    }

    public void showRewarded(Activity activity, OnUserEarnedRewardListener listener) {
        if (mRewardedAd != null) {
            mRewardedAd.show(activity, listener);
            mRewardedAd = null;
            loadRewarded(activity); // Load next
        }
    }
}