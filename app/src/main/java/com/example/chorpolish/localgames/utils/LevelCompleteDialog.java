package com.rakibul.chorpolish.localgames.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.rakibul.chorpolish.R;

public class LevelCompleteDialog extends Dialog {

    public interface OnDialogActionListener {
        void onNextLevel();
        void onReplay();
        void onHome();
    }

    public LevelCompleteDialog(@NonNull Context context, int level, int reward, OnDialogActionListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_level_complete);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setCancelable(false);

        ((TextView) findViewById(R.id.tvCompletedLevel)).setText(String.valueOf(level));
        ((TextView) findViewById(R.id.tvReward)).setText("+" + reward);

        findViewById(R.id.btnNextLevel).setOnClickListener(v -> {
            dismiss();
            listener.onNextLevel();
        });

        findViewById(R.id.btnReplay).setOnClickListener(v -> {
            dismiss();
            listener.onReplay();
        });

        findViewById(R.id.btnHome).setOnClickListener(v -> {
            dismiss();
            listener.onHome();
        });
    }
}