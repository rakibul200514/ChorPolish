package com.rakibul.chorpolish.localgames.utils;

import com.rakibul.chorpolish.localgames.models.LevelData;
import com.rakibul.chorpolish.localgames.models.Tube;
import java.util.ArrayList;
import java.util.List;

public class AdvancedLevelManager {

    public static LevelData getLevel(int levelNumber) {
        if (BossLevelHelper.isBossLevel(levelNumber)) {
            List<Tube> bossTubes = BossLevelHelper.generateBossLevel(levelNumber);
            return new LevelData(levelNumber, "BOSS", bossTubes.size(), new ArrayList<>());
        }
        return LevelManager.getInstance().getLevel(levelNumber);
    }
}