package com.rakibul.chorpolish.localgames.utils;

import com.example.chorpolish.localgames.models.NutType;
import com.example.chorpolish.localgames.models.SpecialNut;
import com.example.chorpolish.localgames.models.Tube;
import java.util.ArrayList;
import java.util.List;

public class BossLevelHelper {

    public static boolean isBossLevel(int level) {
        return level > 0 && level % 10 == 0;
    }

    public static List<Tube> generateBossLevel(int level) {
        List<Tube> tubes = new ArrayList<>();
        // Boss levels have 10-12 tubes and special nuts
        int numTubes = 10;
        for (int i = 0; i < numTubes; i++) {
            Tube tube = new Tube(i);
            if (i < 8) {
                // Add some special nuts for challenge
                tube.addNut(new SpecialNut(GameConstants.NUT_COLORS[i % GameConstants.NUT_COLORS.length], i, 
                    i % 3 == 0 ? NutType.FIRE : NutType.NORMAL));
            }
            tubes.add(tube);
        }
        return tubes;
    }
}