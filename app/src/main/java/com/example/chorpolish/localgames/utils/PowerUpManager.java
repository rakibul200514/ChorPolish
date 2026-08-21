package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;

public class PowerUpManager {
    private final GameSaveManager saveManager;

    public PowerUpManager(Context context) {
        this.saveManager = new GameSaveManager(context);
    }

    public boolean useUndo() {
        return usePowerUp("undo", 50);
    }

    public boolean useHint() {
        return usePowerUp("hint", 100);
    }

    public boolean useShuffle() {
        return usePowerUp("shuffle", 150);
    }

    public boolean useHammer() {
        return usePowerUp("hammer", 200);
    }

    private boolean usePowerUp(String name, int cost) {
        if (saveManager.spendCoins(cost)) {
            // Power-up logic handled in GameActivity
            return true;
        }
        return false;
    }
}