package com.example.chorpolish.localgames.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chorpolish.R;
import com.example.chorpolish.localgames.adapters.LocalGamesAdapter;
import com.example.chorpolish.localgames.models.LocalGameModel;
import java.util.ArrayList;
import java.util.List;

public class LocalGamesActivity extends AppCompatActivity {

    private RecyclerView rvLocalGames;
    private LocalGamesAdapter adapter;
    private List<LocalGameModel> gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_games);

        initViews();
        setupRecyclerView();
    }

    private void initViews() {
        rvLocalGames = findViewById(R.id.rvLocalGames);
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
            onBackPressed();
        });
    }

    private void setupRecyclerView() {
        gameList = new ArrayList<>();
        gameList.add(new LocalGameModel(
                "🧩 Nut Sort Puzzle",
                "Sort all colorful nuts.",
                "Easy",
                "Available",
                R.drawable.ic_puzzle,
                false
        ));
        gameList.add(new LocalGameModel(
                "🎮 More Games",
                "New games coming soon!",
                "???",
                "Locked",
                R.drawable.ic_controller,
                true
        ));

        adapter = new LocalGamesAdapter(gameList, game -> {
            if (game.getTitle().contains("Nut Sort")) {
                startActivity(new Intent(this, NutSortMenuActivity.class));
            }
        });

        rvLocalGames.setLayoutManager(new LinearLayoutManager(this));
        rvLocalGames.setAdapter(adapter);
    }
}