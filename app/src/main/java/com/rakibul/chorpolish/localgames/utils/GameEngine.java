package com.rakibul.chorpolish.localgames.utils;

import com.rakibul.chorpolish.localgames.models.Nut;
import com.rakibul.chorpolish.localgames.models.Tube;
import java.util.List;

public class GameEngine {
    private final List<Tube> tubes;
    private final UndoManager undoManager;
    private int movesCount = 0;

    public GameEngine(List<Tube> tubes) {
        this.tubes = tubes;
        this.undoManager = new UndoManager();
    }

    public boolean performMove(int fromIndex, int toIndex) {
        Tube fromTube = tubes.get(fromIndex);
        Tube toTube = tubes.get(toIndex);

        if (MoveValidator.isValidMove(fromTube, toTube)) {
            Nut nut = fromTube.popNut();
            toTube.addNut(nut);
            undoManager.pushMove(fromIndex, toIndex);
            movesCount++;
            return true;
        }
        return false;
    }

    public UndoManager.MoveRecord undoLastMove() {
        UndoManager.MoveRecord lastMove = undoManager.popMove();
        if (lastMove != null) {
            Tube fromOriginal = tubes.get(lastMove.toIndex);
            Tube toOriginal = tubes.get(lastMove.fromIndex);
            Nut nut = fromOriginal.popNut();
            toOriginal.addNut(nut);
            movesCount--;
            return lastMove;
        }
        return null;
    }

    public UndoManager.MoveRecord getHint() {
        return HintManager.findValidMove(tubes);
    }

    public boolean isGameOver() {
        return WinChecker.isLevelComplete(tubes);
    }

    public int getMovesCount() {
        return movesCount;
    }

    public void reset() {
        movesCount = 0;
        undoManager.clear();
    }
}