package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;

public class PowerUpManager {
    private final GameSaveManager saveManager;

    public PowerUpManager(Context context) {
        this.saveManager = new GameSaveManager(context);
    }

    public boolean useUndo() {
        return usePowerUp("undo", 10);
    }

    public boolean useHint() {
        return usePowerUp("hint", 20);
    }

    public boolean useShuffle() {
        return usePowerUp("shuffle", 50);
    }

    public boolean useHammer() {
        return usePowerUp("hammer", 100);
    }

    private boolean usePowerUp(String name, int cost) {
        return saveManager.spendCoins(cost);
    }
}