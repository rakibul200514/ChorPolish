package com.rakibul.chorpolish.localgames.models;

public class SpecialNut extends Nut {
    private final NutType type;
    private int countdown;
    private boolean isFrozen;

    public SpecialNut(int color, int id, NutType type) {
        super(color, id);
        this.type = type;
        this.countdown = 5;
        this.isFrozen = (type == NutType.ICE);
    }

    public NutType getType() {
        return type;
    }

    public int getCountdown() {
        return countdown;
    }

    public void decrementCountdown() {
        if (countdown > 0) countdown--;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }
}