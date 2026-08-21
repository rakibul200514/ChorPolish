package com.rakibul.chorpolish.localgames.utils;

import com.rakibul.chorpolish.localgames.models.Tube;
import java.util.List;

public class HintManager {
    public static UndoManager.MoveRecord findValidMove(List<Tube> tubes) {
        for (int i = 0; i < tubes.size(); i++) {
            Tube fromTube = tubes.get(i);
            if (fromTube.isEmpty()) continue;
            if (fromTube.isCompleted()) continue; // Don't move from completed tubes

            for (int j = 0; j < tubes.size(); j++) {
                if (i == j) continue;
                Tube toTube = tubes.get(j);
                if (MoveValidator.isValidMove(fromTube, toTube)) {
                    // Avoid moving back and forth between two tubes if it doesn't help
                    // (Simple heuristic: don't move to an empty tube if it's the only nut of that color in 'from')
                    return new UndoManager.MoveRecord(i, j);
                }
            }
        }
        return null;
    }
}