package com.rakibul.chorpolish.localgames.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MoveAnimation {
    
    public interface AnimationListener {
        void onUpdate(float x, float y);
        void onEnd();
    }

    public static void startArcMove(float startX, float startY, float endX, float endY, long duration, AnimationListener listener) {
        float peakY = Math.min(startY, endY) - 250;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();
            float currentX = startX + (endX - startX) * t;
            float currentY;
            if (t < 0.5) {
                currentY = startY + (peakY - startY) * (t * 2);
            } else {
                currentY = peakY + (endY - peakY) * ((t - 0.5f) * 2);
            }
            listener.onUpdate(currentX, currentY);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onEnd();
            }
        });
        animator.start();
    }
}