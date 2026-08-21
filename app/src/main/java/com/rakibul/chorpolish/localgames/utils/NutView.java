package com.rakibul.chorpolish.localgames.utils;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rakibul.chorpolish.localgames.models.Nut;
import com.rakibul.chorpolish.localgames.models.Tube;

import java.util.ArrayList;
import java.util.List;

public class NutView extends View {

    private List<Tube> tubes;
    private GameEngine gameEngine;
    private int selectedTubeIndex = -1;
    private Nut animatingNut = null;
    private float animatingNutX, animatingNutY;
    private int fromTubeIdx = -1;
    private int hintFrom = -1, hintTo = -1;

    private Paint tubePaint, nutPaint, glowPaint, hintPaint;
    private float tubeWidth, tubeHeight, nutRadius;
    private float spacing;

    public interface OnGameEventListener {
        void onMoveMade(int count);
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
        
        hintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hintPaint.setStyle(Paint.Style.STROKE);
        hintPaint.setStrokeWidth(8f);
        hintPaint.setColor(Color.YELLOW);
    }

    public void setTubes(List<Tube> tubes) {
        this.tubes = tubes;
        this.gameEngine = new GameEngine(tubes);
        this.selectedTubeIndex = -1;
        this.animatingNut = null;
        this.hintFrom = -1;
        this.hintTo = -1;
        invalidate();
    }

    public void setOnGameEventListener(OnGameEventListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (tubes == null || tubes.isEmpty()) return;
        
        float availableWidth = w - getPaddingLeft() - getPaddingRight();
        spacing = availableWidth / (tubes.size() + 1);
        tubeWidth = Math.min(spacing * 0.75f, 180f);
        tubeHeight = h * 0.55f;
        nutRadius = tubeWidth * 0.4f;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (tubes == null || tubes.isEmpty()) return;

        for (int i = 0; i < tubes.size(); i++) {
            float centerX = spacing * (i + 1);
            float bottomY = getHeight() - getPaddingBottom() - 150;
            float topY = bottomY - tubeHeight;

            boolean isHinted = (i == hintFrom || i == hintTo);
            drawTube(canvas, centerX, topY, bottomY, i == selectedTubeIndex, isHinted);
            drawNutsInTube(canvas, tubes.get(i), centerX, bottomY);
        }

        if (animatingNut != null) {
            drawNut(canvas, animatingNutX, animatingNutY, animatingNut.getColor(), true);
        }
    }

    private void drawTube(Canvas canvas, float cx, float topY, float bottomY, boolean isSelected, boolean isHinted) {
        RectF rect = new RectF(cx - tubeWidth / 2, topY, cx + tubeWidth / 2, bottomY);
        
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#1AFFFFFF"));
        canvas.drawRoundRect(rect, tubeWidth / 2, tubeWidth / 2, bgPaint);

        if (isHinted) {
            canvas.drawRoundRect(rect, tubeWidth / 2, tubeWidth / 2, hintPaint);
        }

        tubePaint.setColor(isSelected ? Color.WHITE : Color.parseColor("#4DFFFFFF"));
        tubePaint.setStrokeWidth(isSelected ? 10f : 6f);
        
        Path path = new Path();
        path.moveTo(rect.left, rect.top);
        path.lineTo(rect.left, rect.bottom - tubeWidth/2);
        path.arcTo(new RectF(rect.left, rect.bottom - tubeWidth, rect.right, rect.bottom), 180, -180);
        path.lineTo(rect.right, rect.top);
        canvas.drawPath(path, tubePaint);
    }

    private void drawNutsInTube(Canvas canvas, Tube tube, float cx, float bottomY) {
        List<Nut> nutsInTube = new ArrayList<>(tube.getNuts());
        for (int i = 0; i < nutsInTube.size(); i++) {
            if (i == nutsInTube.size() - 1 && (animatingNut != null && tube.getId() == fromTubeIdx)) continue;
            
            float cy = bottomY - (i * (nutRadius * 2.3f)) - nutRadius - 30;
            if (i == nutsInTube.size() - 1 && tubes.indexOf(tube) == selectedTubeIndex) {
                cy -= 80;
            }
            drawNut(canvas, cx, cy, nutsInTube.get(i).getColor(), false);
        }
    }

    private void drawNut(Canvas canvas, float cx, float cy, int color, boolean isAnimating) {
        nutPaint.setShader(new LinearGradient(cx - nutRadius, cy - nutRadius, cx + nutRadius, cy + nutRadius,
                color, darkenColor(color), Shader.TileMode.CLAMP));
        
        canvas.drawCircle(cx, cy, nutRadius, nutPaint);

        Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setColor(Color.parseColor("#99FFFFFF"));
        canvas.drawCircle(cx - nutRadius * 0.35f, cy - nutRadius * 0.35f, nutRadius * 0.3f, highlightPaint);

        if (isAnimating) {
            glowPaint.setColor(color);
            glowPaint.setAlpha(150);
            glowPaint.setStyle(Paint.Style.STROKE);
            glowPaint.setStrokeWidth(12f);
            canvas.drawCircle(cx, cy, nutRadius + 8, glowPaint);
        }
    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.5f;
        return Color.HSVToColor(hsv);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
            if (animatingNut != null) return true;

            float x = event.getX();
            int clickedTubeIdx = -1;
            for (int i = 0; i < tubes.size(); i++) {
                float centerX = spacing * (i + 1);
                if (Math.abs(x - centerX) < tubeWidth / 1.2f) {
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
        hintFrom = -1;
        hintTo = -1;

        if (selectedTubeIndex == -1) {
            if (!tubes.get(index).isEmpty()) {
                selectedTubeIndex = index;
                invalidate();
            }
        } else if (selectedTubeIndex == index) {
            selectedTubeIndex = -1;
            invalidate();
        } else {
            attemptMove(selectedTubeIndex, index);
        }
    }

    private void attemptMove(int from, int to) {
        if (gameEngine.performMove(from, to)) {
            // Success move - Nut was already popped and added in GameEngine,
            // but for animation we need to manually adjust for a moment.
            // Actually, performMove pops/adds immediately. To animate,
            // we should ideally pop, animate, then add.
            // Let's adjust GameEngine or use it differently.
            
            // Revert GameEngine state for animation
            Nut nut = tubes.get(to).popNut();
            tubes.get(from).addNut(nut);
            
            animateMove(from, to, nut, false);
            selectedTubeIndex = -1;
        } else {
            if (listener != null) listener.onInvalidMove();
            selectedTubeIndex = -1;
            invalidate();
        }
    }

    private void animateMove(int from, int to, Nut nut, boolean isUndo) {
        animatingNut = nut;
        fromTubeIdx = tubes.get(from).getId();

        float startX = spacing * (from + 1);
        float startY = getHeight() - getPaddingBottom() - 150 - (tubes.get(from).size() * (nutRadius * 2.3f)) - nutRadius - 110;
        
        // Temporarily pop from source for animation
        tubes.get(from).popNut();
        
        float endX = spacing * (to + 1);
        float endY = getHeight() - getPaddingBottom() - 150 - (tubes.get(to).size() * (nutRadius * 2.3f)) - nutRadius - 30;

        MoveAnimation.startArcMove(startX, startY, endX, endY, 350, new MoveAnimation.AnimationListener() {
            @Override
            public void onUpdate(float x, float y) {
                animatingNutX = x;
                animatingNutY = y;
                invalidate();
            }

            @Override
            public void onEnd() {
                tubes.get(to).addNut(animatingNut);
                animatingNut = null;
                invalidate();
                if (!isUndo) {
                    if (gameEngine.isGameOver()) {
                        if (listener != null) listener.onLevelCompleted();
                    }
                    if (listener != null) listener.onMoveMade(gameEngine.getMovesCount());
                }
            }
        });
    }

    public void undo() {
        if (animatingNut != null) return;
        UndoManager.MoveRecord last = gameEngine.undoLastMove();
        if (last != null) {
            // Undo pops from 'to' and adds to 'from' in GameEngine.
            // Revert for animation.
            Nut nut = tubes.get(last.fromIndex).popNut();
            tubes.get(last.toIndex).addNut(nut);
            
            animateMove(last.toIndex, last.fromIndex, nut, true);
            if (listener != null) listener.onMoveMade(gameEngine.getMovesCount());
        }
    }

    public void showHint() {
        UndoManager.MoveRecord hint = gameEngine.getHint();
        if (hint != null) {
            hintFrom = hint.fromIndex;
            hintTo = hint.toIndex;
            invalidate();
        }
    }
}