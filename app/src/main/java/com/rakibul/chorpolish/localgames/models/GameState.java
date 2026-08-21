package com.rakibul.chorpolish.localgames.models;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final List<List<Integer>> tubeStates;

    public GameState(List<Tube> tubes) {
        this.tubeStates = new ArrayList<>();
        for (Tube tube : tubes) {
            List<Integer> nuts = new ArrayList<>();
            for (Nut nut : tube.getNuts()) {
                nuts.add(nut.getColor());
            }
            this.tubeStates.add(nuts);
        }
    }

    public List<List<Integer>> getTubeStates() {
        return tubeStates;
    }
}