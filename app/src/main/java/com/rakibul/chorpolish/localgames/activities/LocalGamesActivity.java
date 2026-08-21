package com.rakibul.chorpolish.localgames.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rakibul.chorpolish.R;
import com.rakibul.chorpolish.localgames.adapters.LocalGamesAdapter;
import com.rakibul.chorpolish.localgames.models.LocalGameModel;
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
            finish();
        });
    }

    private void setupRecyclerView() {
        gameList = new ArrayList<>();
        gameList.add(new LocalGameModel(
                getString(R.string.nut_sort_title),
                getString(R.string.nut_sort_subtitle),
                getString(R.string.easy),
                getString(R.string.available),
                R.drawable.ic_puzzle,
                false
        ));
        gameList.add(new LocalGameModel(
                getString(R.string.more_games),
                getString(R.string.coming_soon),
                "???",
                "Locked",
                R.drawable.ic_controller,
                true
        ));

        adapter = new LocalGamesAdapter(gameList, game -> {
            if (game.getTitle().equals(getString(R.string.nut_sort_title))) {
                startActivity(new Intent(this, NutSortMenuActivity.class));
            }
        });

        rvLocalGames.setLayoutManager(new LinearLayoutManager(this));
        rvLocalGames.setAdapter(adapter);
    }
}