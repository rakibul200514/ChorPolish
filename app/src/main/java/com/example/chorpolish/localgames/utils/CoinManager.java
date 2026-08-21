package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;

public class CoinManager {
    private final GameSaveManager saveManager;

    public CoinManager(Context context) {
        this.saveManager = new GameSaveManager(context);
    }

    public int getBalance() {
        return saveManager.getCoins();
    }

    public void addCoins(int amount) {
        saveManager.addCoins(amount);
    }

    public boolean spendCoins(int amount) {
        return saveManager.spendCoins(amount);
    }
}