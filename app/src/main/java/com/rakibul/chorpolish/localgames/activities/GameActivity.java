package com.rakibul.chorpolish.localgames.activities;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rakibul.chorpolish.R;
import com.rakibul.chorpolish.localgames.models.LevelData;
import com.rakibul.chorpolish.localgames.models.Nut;
import com.rakibul.chorpolish.localgames.models.Tube;
import com.rakibul.chorpolish.localgames.utils.GameSaveManager;
import com.rakibul.chorpolish.localgames.utils.LevelCompleteDialog;
import com.rakibul.chorpolish.localgames.utils.LevelManager;
import com.rakibul.chorpolish.localgames.utils.NutView;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private NutView nutView;
    private TextView tvLevel, tvCoins;
    private GameSaveManager saveManager;
    private int currentLevelNum;
    private LevelData currentLevelData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        saveManager = new GameSaveManager(this);
        currentLevelNum = getIntent().getIntExtra("LEVEL_NUMBER", saveManager.getLatestLevel());

        initViews();
        loadLevel(currentLevelNum);
    }

    private void initViews() {
        nutView = findViewById(R.id.nutView);
        tvLevel = findViewById(R.id.tvLevel);
        tvCoins = findViewById(R.id.tvCoins);
        updateCoinsDisplay();

        findViewById(R.id.btnRestart).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            loadLevel(currentLevelNum);
        });

        findViewById(R.id.btnUndo).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            nutView.undo();
        });

        findViewById(R.id.btnHome).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            finish();
        });
        
        // Use btnPause as Hint for now as per objective (though XML has it as Pause)
        findViewById(R.id.btnPause).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            nutView.showHint();
        });

        nutView.setOnGameEventListener(new NutView.OnGameEventListener() {
            @Override
            public void onMoveMade(int count) {
                // Can update a move counter UI if added
            }

            @Override
            public void onLevelCompleted() {
                handleLevelWin();
            }

            @Override
            public void onInvalidMove() {
                Toast.makeText(GameActivity.this, "Invalid Move!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCoinsDisplay() {
        tvCoins.setText(String.valueOf(saveManager.getCoins()));
    }

    private void loadLevel(int levelNum) {
        currentLevelNum = levelNum;
        saveManager.setLatestLevel(levelNum);
        currentLevelData = LevelManager.getInstance().getLevel(levelNum);
        
        tvLevel.setText("LEVEL " + levelNum);
        
        List<Tube> tubes = new ArrayList<>();
        List<int[]> configs = currentLevelData.getTubeConfigurations();
        
        for (int i = 0; i < configs.size(); i++) {
            Tube tube = new Tube(i);
            int[] colors = configs.get(i);
            for (int color : colors) {
                tube.addNut(new Nut(color, i * 10 + color));
            }
            tubes.add(tube);
        }
        
        nutView.setTubes(tubes);
    }

    private void handleLevelWin() {
        int reward = currentLevelData.getRewardCoins();
        saveManager.addCoins(reward);
        saveManager.unlockLevel(currentLevelNum + 1);
        updateCoinsDisplay();

        LevelCompleteDialog dialog = new LevelCompleteDialog(this, currentLevelNum, reward, new LevelCompleteDialog.OnDialogActionListener() {
            @Override
            public void onNextLevel() {
                loadLevel(currentLevelNum + 1);
            }

            @Override
            public void onReplay() {
                loadLevel(currentLevelNum);
            }

            @Override
            public void onHome() {
                finish();
            }
        });
        dialog.show();
    }
}