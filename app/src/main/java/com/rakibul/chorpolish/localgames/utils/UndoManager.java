package com.rakibul.chorpolish.localgames.utils;

import java.util.Stack;

public class UndoManager {
    private final Stack<MoveRecord> moveHistory;

    public UndoManager() {
        this.moveHistory = new Stack<>();
    }

    public void pushMove(int fromIndex, int toIndex) {
        moveHistory.push(new MoveRecord(fromIndex, toIndex));
    }

    public MoveRecord popMove() {
        if (moveHistory.isEmpty()) return null;
        return moveHistory.pop();
    }

    public void clear() {
        moveHistory.clear();
    }

    public static class MoveRecord {
        public final int fromIndex;
        public final int toIndex;

        public MoveRecord(int fromIndex, int toIndex) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }
    }
}