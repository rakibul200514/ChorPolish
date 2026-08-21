package com.rakibul.chorpolish.localgames.activities;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rakibul.chorpolish.R;
import com.rakibul.chorpolish.localgames.adapters.SettingsAdapter;
import com.rakibul.chorpolish.localgames.utils.GameSaveManager;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private GameSaveManager saveManager;
    private SettingsAdapter adapter;
    private List<SettingsAdapter.SettingItem> settingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_local);

        saveManager = new GameSaveManager(this);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            finish();
        });

        RecyclerView rvSettings = findViewById(R.id.rvSettings);
        settingItems = new ArrayList<>();
        
        settingItems.add(new SettingsAdapter.SettingItem("Music", R.drawable.ic_puzzle, true, saveManager.isMusicOn()));
        settingItems.add(new SettingsAdapter.SettingItem("Sound Effects", R.drawable.ic_puzzle, true, saveManager.isSoundOn()));
        settingItems.add(new SettingsAdapter.SettingItem("Vibration", R.drawable.ic_puzzle, true, saveManager.isVibrationOn()));
        settingItems.add(new SettingsAdapter.SettingItem("Privacy Policy", R.drawable.ic_lock_premium, false, false));
        settingItems.add(new SettingsAdapter.SettingItem("Rate App", R.drawable.ic_trophy, false, false));
        settingItems.add(new SettingsAdapter.SettingItem("Reset Progress", R.drawable.ic_controller, false, false));

        adapter = new SettingsAdapter(settingItems, new SettingsAdapter.OnSettingClickListener() {
            @Override
            public void onSettingClick(int position) {
                handleAction(position);
            }

            @Override
            public void onToggle(int position, boolean checked) {
                handleToggle(position, checked);
            }
        });

        rvSettings.setLayoutManager(new LinearLayoutManager(this));
        rvSettings.setAdapter(adapter);
    }

    private void handleToggle(int position, boolean checked) {
        switch (position) {
            case 0: saveManager.setMusicOn(checked); break;
            case 1: saveManager.setSoundOn(checked); break;
            case 2: saveManager.setVibrationOn(checked); break;
        }
    }

    private void handleAction(int position) {
        switch (position) {
            case 3: Toast.makeText(this, "Opening Privacy Policy...", Toast.LENGTH_SHORT).show(); break;
            case 4: Toast.makeText(this, "Redirecting to Play Store...", Toast.LENGTH_SHORT).show(); break;
            case 5: 
                saveManager.setLatestLevel(1);
                saveManager.unlockLevel(1);
                Toast.makeText(this, "Progress Reset!", Toast.LENGTH_SHORT).show(); 
                break;
        }
    }
}