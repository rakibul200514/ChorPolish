const mongoose = require('mongoose');

const roomSchema = new mongoose.Schema({
    roomCode: { type: String, required: true, unique: true },
    players: [{
        name: String,
        playerId: String,
        role: String,
        score: { type: Number, default: 0 },
        hasSeenRole: { type: Boolean, default: false },
        socketId: String,
        isDisconnected: { type: Boolean, default: false },
        disconnectedAt: Date
    }],
    host: String,
    gameMode: { type: String, enum: ['CHOR_POLISH', 'LETTER_SET'], default: 'CHOR_POLISH' },
    phase: { type: String, enum: ['LOBBY', 'REVEAL', 'GUESSING', 'RESULT', 'FINISHED', 'PLAYING'], default: 'LOBBY' },
    isStarted: { type: Boolean, default: false },
    currentRound: { type: Number, default: 1 },
    maxRounds: { type: Number, default: 8 },
    mantriPlayer: String,
    rajaPlayer: String,
    chorPlayer: String,
    sipahiPlayer: String,
    sipahiPlayerId: String, // Added
    guessOptions: [String],
    selectedChorGuess: String,
    isGuessCorrect: Boolean,
    playerCards: { type: Map, of: [String] },
    winner: String,
    currentPlayerTurn: { type: Number, default: 0 },
    createdAt: { type: Date, default: Date.now, expires: 7200 } // TTL 2 hours
});

module.exports = mongoose.model('Room', roomSchema);
