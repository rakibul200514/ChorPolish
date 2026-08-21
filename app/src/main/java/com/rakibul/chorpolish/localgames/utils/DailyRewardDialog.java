package com.rakibul.chorpolish.localgames.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.rakibul.chorpolish.R;

public class DailyRewardDialog extends Dialog {

    public interface OnClaimListener {
        void onClaimed(int amount);
    }

    public DailyRewardDialog(@NonNull Context context, int amount, OnClaimListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_daily_reward);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvAmount = findViewById(R.id.tvRewardAmount);
        tvAmount.setText(amount + " COINS");

        findViewById(R.id.btnClaim).setOnClickListener(v -> {
            listener.onClaimed(amount);
            dismiss();
        });
    }
}