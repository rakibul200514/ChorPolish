package com.example.chorpolish.localgames.activities;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chorpolish.R;

public class NutSortMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nut_sort_menu);

        setupButtons();
        applyEntranceAnimations();
    }

    private void setupButtons() {
        View.OnClickListener clickListener = v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            // Actions for Play, Levels, Settings can be added here
        };

        findViewById(R.id.btnPlay).setOnClickListener(clickListener);
        findViewById(R.id.btnLevels).setOnClickListener(clickListener);
        findViewById(R.id.btnSettings).setOnClickListener(clickListener);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            onBackPressed();
        });
    }

    private void applyEntranceAnimations() {
        Animation fadeUp = AnimationUtils.loadAnimation(this, R.anim.fade_up);
        findViewById(R.id.tvGameTitle).startAnimation(fadeUp);
        findViewById(R.id.buttonContainer).startAnimation(fadeUp);
        
        // Apply floating animation to title
        findViewById(R.id.tvGameTitle).startAnimation(AnimationUtils.loadAnimation(this, R.anim.floating));
    }
}