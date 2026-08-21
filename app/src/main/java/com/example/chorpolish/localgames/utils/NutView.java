package com.rakibul.chorpolish.localgames.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.rakibul.chorpolish.localgames.models.Nut;
import com.rakibul.chorpolish.localgames.models.Tube;

import java.util.ArrayList;
import java.util.List;

public class NutView extends View {

    private List<Tube> tubes;
    private int selectedTubeIndex = -1;
    private Nut animatingNut = null;
    private float animatingNutX, animatingNutY;
    private int fromTubeIdx = -1, toTubeIdx = -1;

    private Paint tubePaint, nutPaint, glowPaint;
    private float tubeWidth, tubeHeight, nutRadius;
    private float spacing;

    public interface OnGameEventListener {
        void onMoveMade();
        void onLevelCompleted();
        void onInvalidMove();
    }

    private OnGameEventListener listener;

    public NutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        tubes = new ArrayList<>();
        tubePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tubePaint.setStyle(Paint.Style.STROKE);
        tubePaint.setStrokeWidth(6f);
        tubePaint.setColor(Color.parseColor("#4DFFFFFF"));

        nutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setTubes(List<Tube> tubes) {
        this.tubes = tubes;
        invalidate();
    }

    public void setOnGameEventListener(OnGameEventListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float availableWidth = w - getPaddingLeft() - getPaddingRight();
        spacing = availableWidth / (tubes.size() + 1);
        tubeWidth = spacing * 0.7f;
        tubeHeight = h * 0.5f;
        nutRadius = tubeWidth * 0.4f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (tubes == null || tubes.isEmpty()) return;

        for (int i = 0; i < tubes.size(); i++) {
            float centerX = spacing * (i + 1);
            float bottomY = getHeight() - getPaddingBottom() - 100;
            float topY = bottomY - tubeHeight;

            drawTube(canvas, centerX, topY, bottomY, i == selectedTubeIndex);
            drawNutsInTube(canvas, tubes.get(i), centerX, bottomY);
        }

        if (animatingNut != null) {
            drawNut(canvas, animatingNutX, animatingNutY, animatingNut.getColor(), true);
        }
    }

    private void drawTube(Canvas canvas, float cx, float topY, float bottomY, boolean isSelected) {
        RectF rect = new RectF(cx - tubeWidth / 2, topY, cx + tubeWidth / 2, bottomY);
        
        // Tube Glass Background (Glassmorphism)
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#1AFFFFFF"));
        canvas.drawRoundRect(rect, tubeWidth / 2, tubeWidth / 2, bgPaint);

        // Tube Stroke
        tubePaint.setColor(isSelected ? Color.WHITE : Color.parseColor("#4DFFFFFF"));
        tubePaint.setStrokeWidth(isSelected ? 8f : 5f);
        
        Path path = new Path();
        path.moveTo(rect.left, rect.top);
        path.lineTo(rect.left, rect.bottom - tubeWidth/2);
        path.arcTo(new RectF(rect.left, rect.bottom - tubeWidth, rect.right, rect.bottom), 180, -180);
        path.lineTo(rect.right, rect.top);
        canvas.drawPath(path, tubePaint);
    }

    private void drawNutsInTube(Canvas canvas, Tube tube, float cx, float bottomY) {
        List<Nut> nuts = new ArrayList<>(tube.getNuts());
        for (int i = 0; i < nuts.size(); i++) {
            // Skip top nut if it's being animated or selected (lifted)
            if (i == nuts.size() - 1 && (animatingNut != null && tube.getId() == fromTubeIdx)) continue;
            
            float cy = bottomY - (i * (nutRadius * 2.2f)) - nutRadius - 20;
            if (tube.getId() == selectedTubeIndex && i == nuts.size() - 1) {
                cy -= 40; // Lift effect
            }
            drawNut(canvas, cx, cy, nuts.get(i).getColor(), false);
        }
    }

    private void drawNut(Canvas canvas, float cx, float cy, int color, boolean isAnimating) {
        // Main Nut Body
        nutPaint.setShader(new LinearGradient(cx - nutRadius, cy - nutRadius, cx + nutRadius, cy + nutRadius,
                color, darkenColor(color), Shader.TileMode.CLAMP));
        
        canvas.drawCircle(cx, cy, nutRadius, nutPaint);

        // Highlight (3D Effect)
        Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setColor(Color.parseColor("#66FFFFFF"));
        canvas.drawCircle(cx - nutRadius * 0.3f, cy - nutRadius * 0.3f, nutRadius * 0.4f, highlightPaint);

        if (isAnimating || isSelectedNut(cx, cy)) {
            glowPaint.setColor(color);
            glowPaint.setAlpha(100);
            glowPaint.setStyle(Paint.Style.STROKE);
            glowPaint.setStrokeWidth(10f);
            canvas.drawCircle(cx, cy, nutRadius + 5, glowPaint);
        }
    }

    private boolean isSelectedNut(float cx, float cy) {
        // Placeholder for selection logic
        return false;
    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.7f;
        return Color.HSVToColor(hsv);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            int clickedTubeIdx = -1;
            for (int i = 0; i < tubes.size(); i++) {
                float centerX = spacing * (i + 1);
                if (Math.abs(x - centerX) < tubeWidth / 2) {
                    clickedTubeIdx = i;
                    break;
                }
            }

            if (clickedTubeIdx != -1) {
                handleTubeClick(clickedTubeIdx);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleTubeClick(int index) {
        if (selectedTubeIndex == -1) {
            if (!tubes.get(index).isEmpty()) {
                selectedTubeIndex = index;
                invalidate();
            }
        } else if (selectedTubeIndex == index) {
            selectedTubeIndex = -1;
            invalidate();
        } else {
            // Attempt to move
            moveNut(selectedTubeIndex, index);
        }
    }

    private void moveNut(int from, int to) {
        Tube fromTube = tubes.get(from);
        Tube toTube = tubes.get(to);

        Nut nut = fromTube.peekNut();
        if (toTube.isFull() || (!toTube.isEmpty() && toTube.peekNut().getColor() != nut.getColor())) {
            shakeTube(to);
            if (listener != null) listener.onInvalidMove();
            selectedTubeIndex = -1;
            invalidate();
            return;
        }

        animateNutMove(from, to, fromTube.popNut());
        selectedTubeIndex = -1;
    }

    private void animateNutMove(int from, int to, Nut nut) {
        animatingNut = nut;
        fromTubeIdx = from;
        toTubeIdx = to;

        float startX = spacing * (from + 1);
        float startY = getHeight() - getPaddingBottom() - 100 - (tubes.get(from).size() * (nutRadius * 2.2f)) - nutRadius - 60;
        float endX = spacing * (to + 1);
        float endY = getHeight() - getPaddingBottom() - 100 - (tubes.get(to).size() * (nutRadius * 2.2f)) - nutRadius - 20;
        float peakY = Math.min(startY, endY) - 150;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(GameConstants.ANIM_DURATION_MOVE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();
            animatingNutX = startX + (endX - startX) * t;
            // Parabolic path
            if (t < 0.5) {
                animatingNutY = startY + (peakY - startY) * (t * 2);
            } else {
                animatingNutY = peakY + (endY - peakY) * ((t - 0.5f) * 2);
            }
            invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tubes.get(to).addNut(animatingNut);
                animatingNut = null;
                checkWinCondition();
                if (listener != null) listener.onMoveMade();
                invalidate();
            }
        });
        animator.start();
    }

    private void shakeTube(int index) {
        // Simplified shake logic
        invalidate();
    }

    private void checkWinCondition() {
        boolean allCompleted = true;
        for (Tube tube : tubes) {
            if (!tube.isCompleted()) {
                allCompleted = false;
                break;
            }
        }
        if (allCompleted && listener != null) {
            listener.onLevelCompleted();
        }
    }
}