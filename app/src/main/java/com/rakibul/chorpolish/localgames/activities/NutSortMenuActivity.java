package com.rakibul.chorpolish.localgames.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.rakibul.chorpolish.R;
import com.rakibul.chorpolish.localgames.utils.DailyRewardDialog;
import com.rakibul.chorpolish.localgames.utils.DailyRewardManager;
import com.rakibul.chorpolish.localgames.utils.GameSaveManager;
import com.rakibul.chorpolish.localgames.utils.LocalAdManager;

public class NutSortMenuActivity extends AppCompatActivity {

    private DailyRewardManager dailyRewardManager;
    private GameSaveManager saveManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nut_sort_menu);

        dailyRewardManager = new DailyRewardManager(this);
        saveManager = new GameSaveManager(this);

        setupButtons();
        applyEntranceAnimations();
        checkDailyReward();
        
        LocalAdManager.getInstance().loadInterstitial(this);
    }

    private void checkDailyReward() {
        if (dailyRewardManager.canClaimReward()) {
            int amount = dailyRewardManager.getNextRewardAmount();
            DailyRewardDialog dialog = new DailyRewardDialog(this, amount, reward -> {
                saveManager.addCoins(reward);
                dailyRewardManager.claimReward();
            });
            dialog.show();
        }
    }

    private void setupButtons() {
        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            startActivity(new Intent(this, GameActivity.class));
        });

        findViewById(R.id.btnLevels).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            startActivity(new Intent(this, LevelSelectActivity.class));
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            startActivity(new Intent(this, SettingsActivity.class));
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            finish();
        });
    }

    private void applyEntranceAnimations() {
        Animation fadeUp = AnimationUtils.loadAnimation(this, R.anim.fade_up);
        findViewById(R.id.tvGameTitle).startAnimation(fadeUp);
        findViewById(R.id.buttonContainer).startAnimation(fadeUp);
        
        findViewById(R.id.tvGameTitle).postDelayed(() -> {
             findViewById(R.id.tvGameTitle).startAnimation(AnimationUtils.loadAnimation(this, R.anim.floating));
        }, 800);
    }
}