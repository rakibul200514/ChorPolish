package com.rakibul.chorpolish.localgames.utils;

import com.rakibul.chorpolish.localgames.models.Nut;
import com.rakibul.chorpolish.localgames.models.Tube;

public class MoveValidator {
    public static boolean isValidMove(Tube fromTube, Tube toTube) {
        if (fromTube == null || toTube == null) return false;
        if (fromTube.isEmpty()) return false;
        if (toTube.isFull()) return false;

        Nut nutToMove = fromTube.peekNut();
        if (toTube.isEmpty()) return true;

        Nut targetNut = toTube.peekNut();
        return nutToMove.getColor() == targetNut.getColor();
    }
}