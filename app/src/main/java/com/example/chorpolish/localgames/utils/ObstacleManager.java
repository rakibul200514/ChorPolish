package com.rakibul.chorpolish.localgames.utils;

import com.rakibul.chorpolish.localgames.models.NutType;
import com.rakibul.chorpolish.localgames.models.SpecialNut;
import com.rakibul.chorpolish.localgames.models.Tube;
import java.util.List;

public class ObstacleManager {

    public static boolean canMoveNut(Tube tube) {
        if (tube.isEmpty()) return false;
        com.rakibul.chorpolish.localgames.models.Nut topNut = tube.peekNut();
        if (topNut instanceof SpecialNut) {
            SpecialNut special = (SpecialNut) topNut;
            if (special.isFrozen()) return false;
            if (special.getType() == NutType.LOCKED) return false;
        }
        return true;
    }

    public static void onMoveMade(List<Tube> tubes) {
        for (Tube tube : tubes) {
            for (com.rakibul.chorpolish.localgames.models.Nut nut : tube.getNuts()) {
                if (nut instanceof SpecialNut) {
                    SpecialNut special = (SpecialNut) nut;
                    if (special.getType() == NutType.BOMB) {
                        special.decrementCountdown();
                        if (special.getCountdown() == 0) {
                            // Explosion logic!
                        }
                    }
                }
            }
        }
    }
}