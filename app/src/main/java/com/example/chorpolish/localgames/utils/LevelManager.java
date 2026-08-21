package com.rakibul.chorpolish.localgames.utils;

import com.rakibul.chorpolish.localgames.models.LevelData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelManager {
    private static LevelManager instance;
    private final List<LevelData> levels;

    private LevelManager() {
        levels = new ArrayList<>();
        generate100Levels();
    }

    public static LevelManager getInstance() {
        if (instance == null) instance = new LevelManager();
        return instance;
    }

    public LevelData getLevel(int levelNumber) {
        if (levelNumber < 1 || levelNumber > levels.size()) return levels.get(0);
        return levels.get(levelNumber - 1);
    }

    private void generate100Levels() {
        for (int i = 1; i <= 100; i++) {
            String difficulty;
            int numColors;
            int totalTubes;

            if (i <= 20) {
                difficulty = "Easy";
                numColors = (i < 10) ? 3 : 4;
                totalTubes = numColors + 1;
            } else if (i <= 50) {
                difficulty = "Medium";
                numColors = 5;
                totalTubes = numColors + 2;
            } else if (i <= 80) {
                difficulty = "Hard";
                numColors = 6;
                totalTubes = numColors + 2;
            } else {
                difficulty = "Expert";
                numColors = 8;
                totalTubes = numColors + 2;
            }

            levels.add(generateSolvableLevel(i, difficulty, numColors, totalTubes));
        }
    }

    private LevelData generateSolvableLevel(int levelNum, String diff, int numColors, int totalTubes) {
        List<Integer> allNuts = new ArrayList<>();
        for (int c = 0; c < numColors; c++) {
            for (int j = 0; j < 4; j++) {
                // Map color index to GameConstants.NUT_COLORS
                allNuts.add(GameConstants.NUT_COLORS[c % GameConstants.NUT_COLORS.length]);
            }
        }
        
        // Shuffle to create puzzle
        Random random = new Random(levelNum * 12345L); // Deterministic for each level
        Collections.shuffle(allNuts, random);

        List<int[]> tubes = new ArrayList<>();
        for (int i = 0; i < numColors; i++) {
            int[] tube = new int[4];
            for (int j = 0; j < 4; j++) {
                tube[j] = allNuts.get(i * 4 + j);
            }
            tubes.add(tube);
        }

        // Add empty tubes
        for (int i = 0; i < (totalTubes - numColors); i++) {
            tubes.add(new int[0]);
        }

        return new LevelData(levelNum, diff, totalTubes, tubes);
    }
}