package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.rakibul.chorpolish.R;

public class GameEffects {

    public static void playButtonClick(View view) {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.button_click);
        view.startAnimation(anim);
    }

    public static void playConfetti(View view) {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_up);
        view.startAnimation(anim);
    }

    public static void vibrate(Context context, long duration) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            v.vibrate(duration);
        }
    }
}