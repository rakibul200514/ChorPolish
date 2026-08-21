package com.rakibul.chorpolish.localgames.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.rakibul.chorpolish.R;

public class RewardDialog extends Dialog {

    public RewardDialog(@NonNull Context context, String title, int amount) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_daily_reward); // Reuse layout
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ((TextView) findViewById(R.id.tvRewardAmount)).setText("+" + amount + " COINS");
        
        findViewById(R.id.btnClaim).setOnClickListener(v -> dismiss());
    }
}