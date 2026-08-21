package com.rakibul.chorpolish.localgames.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rakibul.chorpolish.R;
import com.rakibul.chorpolish.localgames.adapters.LevelAdapter;
import com.rakibul.chorpolish.localgames.utils.GameSaveManager;
import com.rakibul.chorpolish.localgames.utils.LocalAdManager;

public class LevelSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        initViews();
        LocalAdManager.getInstance().loadBanner(findViewById(R.id.adView));
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            finish();
        });

        RecyclerView rvLevels = findViewById(R.id.rvLevels);
        GameSaveManager saveManager = new GameSaveManager(this);
        int unlockedLevel = saveManager.getUnlockedLevel();

        LevelAdapter adapter = new LevelAdapter(100, unlockedLevel, level -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("LEVEL_NUMBER", level);
            startActivity(intent);
            finish();
        });

        rvLevels.setLayoutManager(new GridLayoutManager(this, 4));
        rvLevels.setAdapter(adapter);
        
        if (unlockedLevel > 8) {
            rvLevels.scrollToPosition(unlockedLevel - 1);
        }
    }
}