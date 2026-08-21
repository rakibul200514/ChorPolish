package com.rakibul.chorpolish.localgames.utils;

import com.rakibul.chorpolish.localgames.models.Tube;
import java.util.List;

public class WinChecker {
    public static boolean isLevelComplete(List<Tube> tubes) {
        for (Tube tube : tubes) {
            if (!tube.isEmpty()) {
                if (!tube.isCompleted()) {
                    return false;
                }
            }
        }
        
        // Ensure at least some tubes are completed and not just all empty
        int completedCount = 0;
        for (Tube tube : tubes) {
            if (tube.isCompleted() && !tube.isEmpty()) {
                completedCount++;
            }
        }
        
        return completedCount > 0;
    }
}