const express = require('express');
const router = express.Router();
const Room = require('../models/Room');

// Create Room
router.post('/create', async (req, res) => {
    try {
        const { playerName, playerId, gameMode, maxRounds } = req.body;
        console.log(`Creating room for ${playerName} (${playerId}) in mode ${gameMode} with ${maxRounds} rounds`);
        const roomCode = Math.floor(100000 + Math.random() * 900000).toString();
        const room = new Room({
            roomCode,
            players: [{ name: playerName, playerId, score: 0 }],
            host: playerName,
            gameMode: gameMode || 'CHOR_POLISH',
            maxRounds: maxRounds || 8
        });
        await room.save();
        console.log(`Room created: ${roomCode}`);
        res.json(room);
    } catch (err) {
        console.error('Create room error:', err);
        res.status(500).json({ message: err.message });
    }
});

// Join Room
router.post('/join', async (req, res) => {
    try {
        const { playerName, playerId, roomCode } = req.body;
        console.log(`Join attempt: ${playerName} (${playerId}) for room ${roomCode}`);
        const room = await Room.findOne({ roomCode });
        if (room) {
            const existingPlayer = room.players.find(p => p.playerId === playerId);
            if (existingPlayer) {
                console.log(`Player ${playerName} rejoining room ${roomCode}`);
                res.json(room);
            } else if (room.players.length < 4) {
                room.players.push({ name: playerName, playerId, score: 0 });
                await room.save();
                console.log(`Player ${playerName} joined room ${roomCode}`);
                res.json(room);
            } else {
                console.log(`Room ${roomCode} is full`);
                res.status(400).json({ message: 'Room full' });
            }
        } else {
            console.log(`Room not found: ${roomCode}`);
            res.status(400).json({ message: 'Room not found' });
        }
    } catch (err) {
        console.error('Join room error:', err);
        res.status(500).json({ message: err.message });
    }
});

// Get Active Rooms (Admin)
router.get('/active', async (req, res) => {
    const rooms = await Room.find({});
    res.json(rooms);
});

module.exports = router;
