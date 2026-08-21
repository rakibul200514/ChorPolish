package com.rakibul.chorpolish.localgames.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.rakibul.chorpolish.R;

public class BossLevelDialog extends Dialog {

    public BossLevelDialog(@NonNull Context context, int level, int gems, boolean won) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_boss_level);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        TextView tvTitle = findViewById(R.id.tvBossTitle);
        ImageView ivTrophy = findViewById(R.id.ivBossTrophy);
        TextView tvGems = findViewById(R.id.tvBossGems);
        
        tvTitle.setText(won ? "BOSS DEFEATED!" : "BOSS STAGE");
        tvGems.setText("+" + gems);
        
        if (won) {
            ivTrophy.startAnimation(AnimationUtils.loadAnimation(context, R.anim.floating));
        }

        findViewById(R.id.btnBossAction).setOnClickListener(v -> dismiss());
    }
}