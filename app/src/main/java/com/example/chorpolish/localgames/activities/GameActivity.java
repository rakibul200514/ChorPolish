package com.rakibul.chorpolish.localgames.activities;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rakibul.chorpolish.R;
import com.rakibul.chorpolish.localgames.models.Nut;
import com.rakibul.chorpolish.localgames.models.Tube;
import com.rakibul.chorpolish.localgames.utils.GameConstants;
import com.rakibul.chorpolish.localgames.utils.NutView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private NutView nutView;
    private TextView tvLevel;
    private List<Tube> tubes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        loadLevel();
    }

    private void initViews() {
        nutView = findViewById(R.id.nutView);
        tvLevel = findViewById(R.id.tvLevel);

        findViewById(R.id.btnRestart).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            loadLevel();
        });

        findViewById(R.id.btnUndo).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            Toast.makeText(this, "Undo coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnHome).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            finish();
        });

        nutView.setOnGameEventListener(new NutView.OnGameEventListener() {
            @Override
            public void onMoveMade() {
                // Potential sound effect or haptic feedback
            }

            @Override
            public void onLevelCompleted() {
                Toast.makeText(GameActivity.this, "LEVEL COMPLETED! 🏆", Toast.LENGTH_LONG).show();
                // Logic to transition to next level or show win dialog
            }

            @Override
            public void onInvalidMove() {
                // Shake effect is handled inside NutView, but we can add sound here
            }
        });
    }

    private void loadLevel() {
        tubes = new ArrayList<>();
        
        // Tube 1: Red, Green, Yellow, Blue
        Tube t1 = new Tube(0);
        t1.addNut(new Nut(GameConstants.NUT_COLORS[0], 1)); // Red
        t1.addNut(new Nut(GameConstants.NUT_COLORS[1], 2)); // Green
        t1.addNut(new Nut(GameConstants.NUT_COLORS[2], 3)); // Yellow
        t1.addNut(new Nut(GameConstants.NUT_COLORS[3], 4)); // Blue
        tubes.add(t1);

        // Tube 2: Green, Red, Blue, Yellow
        Tube t2 = new Tube(1);
        t2.addNut(new Nut(GameConstants.NUT_COLORS[1], 5));
        t2.addNut(new Nut(GameConstants.NUT_COLORS[0], 6));
        t2.addNut(new Nut(GameConstants.NUT_COLORS[3], 7));
        t2.addNut(new Nut(GameConstants.NUT_COLORS[2], 8));
        tubes.add(t2);

        // Tube 3: Yellow, Blue, Red, Green
        Tube t3 = new Tube(2);
        t3.addNut(new Nut(GameConstants.NUT_COLORS[2], 9));
        t3.addNut(new Nut(GameConstants.NUT_COLORS[3], 10));
        t3.addNut(new Nut(GameConstants.NUT_COLORS[0], 11));
        t3.addNut(new Nut(GameConstants.NUT_COLORS[1], 12));
        tubes.add(t3);

        // Tube 4: Empty
        tubes.add(new Tube(3));

        // Tube 5: Empty
        tubes.add(new Tube(4));

        nutView.setTubes(tubes);
        tvLevel.setText("LEVEL 1");
    }
}