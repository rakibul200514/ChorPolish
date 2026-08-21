package com.rakibul.chorpolish.localgames.models;

import com.rakibul.chorpolish.localgames.utils.GameConstants;
import java.util.Stack;

public class Tube {
    private Stack<Nut> nuts;
    private int id;

    public Tube(int id) {
        this.id = id;
        this.nuts = new Stack<>();
    }

    public boolean isFull() {
        return nuts.size() >= GameConstants.MAX_NUTS_PER_TUBE;
    }

    public boolean isEmpty() {
        return nuts.isEmpty();
    }

    public void addNut(Nut nut) {
        if (!isFull()) {
            nuts.push(nut);
        }
    }

    public Nut popNut() {
        if (!isEmpty()) {
            return nuts.pop();
        }
        return null;
    }

    public Nut peekNut() {
        if (!isEmpty()) {
            return nuts.peek();
        }
        return null;
    }

    public int size() {
        return nuts.size();
    }

    public int getId() {
        return id;
    }

    public Stack<Nut> getNuts() {
        return nuts;
    }

    public boolean isCompleted() {
        if (nuts.size() == 0) return true;
        if (nuts.size() != GameConstants.MAX_NUTS_PER_TUBE) return false;
        
        int firstColor = nuts.get(0).getColor();
        for (Nut nut : nuts) {
            if (nut.getColor() != firstColor) return false;
        }
        return true;
    }
}