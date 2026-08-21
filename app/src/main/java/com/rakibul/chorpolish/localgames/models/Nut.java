package com.rakibul.chorpolish.localgames.models;

public class Nut {
    private final int color;
    private final int id;

    public Nut(int color, int id) {
        this.color = color;
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public int getId() {
        return id;
    }
}