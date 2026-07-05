const Room = require('./models/Room');

module.exports = (io, socket) => {
    // Helper to check if a player needs Robot help
    const checkAndTriggerRobotAction = async (roomCode) => {
        const room = await Room.findOne({ roomCode });
        if (!room || room.phase === 'LOBBY' || room.phase === 'FINISHED' || room.phase === 'RESULT') return;

        // 1. Role Reveal Phase: Auto-see for disconnected players
        if (room.phase === 'REVEAL') {
            const disconnectedPlayers = room.players.filter(p => p.isDisconnected && !p.hasSeenRole);
            if (disconnectedPlayers.length > 0) {
                console.log(`Auto-revealing for disconnected players in room ${roomCode}`);
                disconnectedPlayers.forEach(p => p.hasSeenRole = true);

                const allSeen = room.players.every(p => p.hasSeenRole);
                if (allSeen) {
                    if (room.gameMode === 'CHOR_POLISH') {
                        room.phase = 'GUESSING';
                        room.guessOptions = room.players
                            .filter(p => p.role !== 'POLICE')
                            .map(p => p.name)
                            .sort(() => Math.random() - 0.5);
                    } else {
                        room.phase = 'PLAYING';
                    }
                }
                await room.save();
                io.to(roomCode).emit('roomUpdated', room);
            }
        }

        // 2. Guessing Phase: Robot Sipahi
        if (room.phase === 'GUESSING') {
            const sipahi = room.players.find(p => p.role === 'POLICE');
            if (sipahi && sipahi.isDisconnected) {
                console.log(`Robot taking over Guessing for disconnected Sipahi ${sipahi.name} in room ${roomCode}`);
                setTimeout(async () => {
                    const freshRoom = await Room.findOne({ roomCode });
                    if (!freshRoom || freshRoom.phase !== 'GUESSING') return;

                    const suspects = freshRoom.guessOptions;
                    const randomGuess = suspects[Math.floor(Math.random() * suspects.length)];

                    const isCorrect = randomGuess === freshRoom.chorPlayer;
                    freshRoom.isGuessCorrect = isCorrect;
                    freshRoom.selectedChorGuess = randomGuess;
                    freshRoom.phase = 'RESULT';

                    freshRoom.players.forEach(p => {
                        let roundScore = 0;
                        if (p.role === 'KING') roundScore = 1000;
                        else if (p.role === 'MINISTER') roundScore = 800;
                        else if (p.role === 'POLICE') roundScore = isCorrect ? 500 : 0;
                        else if (p.role === 'THIEF') roundScore = isCorrect ? 0 : 500;
                        p.score += roundScore;
                    });

                    await freshRoom.save();
                    io.to(roomCode).emit('guessResult', freshRoom);
                }, 3000);
            }
        }

        // 3. Letter Set Playing Phase: Robot Turn
        if (room.phase === 'PLAYING' && room.gameMode === 'LETTER_SET') {
            const activePlayer = room.players[room.currentPlayerTurn];
            if (activePlayer && activePlayer.isDisconnected) {
                console.log(`Robot taking over Turn for disconnected player ${activePlayer.name} in room ${roomCode}`);
                setTimeout(async () => {
                    const freshRoom = await Room.findOne({ roomCode });
                    if (!freshRoom || freshRoom.phase !== 'PLAYING' || freshRoom.currentPlayerTurn !== room.currentPlayerTurn) return;

                    const fromPlayerId = activePlayer.playerId;
                    const fromCards = freshRoom.playerCards.get(fromPlayerId);

                    if (fromCards && fromCards.length > 0) {
                        // Robot logic: pass a random card (or least frequent)
                        const card = fromCards[Math.floor(Math.random() * fromCards.length)];

                        const nextPlayerIndex = (freshRoom.currentPlayerTurn + 1) % 4;
                        const nextPlayer = freshRoom.players[nextPlayerIndex];
                        const toPlayerId = nextPlayer.playerId;
                        const toCards = freshRoom.playerCards.get(toPlayerId);

                        const cardIndex = fromCards.indexOf(card);
                        fromCards.splice(cardIndex, 1);
                        toCards.push(card);

                        freshRoom.playerCards.set(fromPlayerId, fromCards);
                        freshRoom.playerCards.set(toPlayerId, toCards);

                        if (toCards.length === 4 && new Set(toCards).size === 1) {
                            freshRoom.winner = nextPlayer.name;
                            freshRoom.phase = 'RESULT';
                            freshRoom.players.forEach(p => {
                                if (p.playerId === toPlayerId) p.score += 100;
                            });
                        } else {
                            freshRoom.currentPlayerTurn = (freshRoom.currentPlayerTurn + 1) % 4;
                        }

                        await freshRoom.save();
                        io.to(roomCode).emit('roomUpdated', freshRoom);
                        // Trigger next robot check
                        checkAndTriggerRobotAction(roomCode);
                    }
                }, 3000);
            }
        }
    };

    // Log every event
    socket.onAny((eventName, ...args) => {
        console.log(`Socket ${socket.id} sent event: ${eventName}`, args);
    });

    socket.on('joinRoom', async (data) => {
        try {
            const { roomCode, playerId } = typeof data === 'string' ? JSON.parse(data) : data;
            console.log(`Socket ${socket.id} attempting to join room ${roomCode} for player ${playerId}`);

            socket.join(roomCode);
            const room = await Room.findOne({ roomCode });
            if (room) {
                const player = room.players.find(p => p.playerId === playerId);
                if (player) {
                    player.socketId = socket.id;
                    player.isDisconnected = false;
                    player.disconnectedAt = null;
                    await room.save();
                    console.log(`Player ${player.name} (${playerId}) successfully joined socket room`);

                    // Broadcast updated room state to everyone in the room
                    io.to(roomCode).emit('roomUpdated', room);
                } else {
                    console.warn(`Player ${playerId} not found in room ${roomCode} data`);
                }
            } else {
                console.warn(`Room ${roomCode} not found for joinRoom event`);
            }
        } catch (err) {
            console.error('joinRoom socket error:', err);
        }
    });

    socket.on('startGame', async (data) => {
        try {
            const { roomCode, gameMode, maxRounds } = typeof data === 'string' ? JSON.parse(data) : data;
            console.log(`Starting game for room ${roomCode} in mode ${gameMode} with ${maxRounds} rounds`);
            const room = await Room.findOne({ roomCode });
            if (room && room.players.length === 4) {
                room.gameMode = gameMode || 'CHOR_POLISH';
                room.maxRounds = maxRounds || 8;

                if (room.gameMode === 'CHOR_POLISH') {
                    const roles = ['KING', 'MINISTER', 'THIEF', 'POLICE'];
                    const shuffledRoles = [...roles].sort(() => Math.random() - 0.5);
                    room.players.forEach((player, index) => {
                        player.role = shuffledRoles[index];
                        player.hasSeenRole = false;
                        if (player.role === 'KING') room.rajaPlayer = player.name;
                        if (player.role === 'MINISTER') room.mantriPlayer = player.name;
                        if (player.role === 'THIEF') room.chorPlayer = player.name;
                        if (player.role === 'POLICE') {
                            room.sipahiPlayer = player.name;
                            room.sipahiPlayerId = player.playerId;
                        }
                    });
                    room.phase = 'REVEAL';
                } else if (room.gameMode === 'LETTER_SET') {
                    const deck = [];
                    ['A', 'B', 'C', 'D'].forEach(l => {
                        for(let i=0; i<4; i++) deck.push(l);
                    });

                    let validDistribution = false;
                    const playerCards = {};

                    while (!validDistribution) {
                        deck.sort(() => Math.random() - 0.5);
                        validDistribution = true;

                        for (let i = 0; i < 4; i++) {
                            const hand = deck.slice(i * 4, (i + 1) * 4);
                            // If any player has 4 of the same card, it's invalid
                            if (hand.every(card => card === hand[0])) {
                                validDistribution = false;
                                break;
                            }
                        }
                    }

                    room.players.forEach((player, index) => {
                        playerCards[player.playerId] = deck.slice(index * 4, (index + 1) * 4);
                        player.hasSeenRole = false; // Reuse this for "seen cards"
                    });
                    room.playerCards = playerCards;
                    room.currentPlayerTurn = 0;
                    room.phase = 'REVEAL';
                }

                room.isStarted = true;
                await room.save();
                io.to(roomCode).emit('gameStarted', room);

                // Initial check for robots
                checkAndTriggerRobotAction(roomCode);
            }
        } catch (err) {
            console.error('startGame socket error:', err);
        }
    });

    socket.on('roleSeen', async (data) => {
        try {
            const { roomCode, playerId } = typeof data === 'string' ? JSON.parse(data) : data;
            const room = await Room.findOne({ roomCode });
            if (room) {
                const player = room.players.find(p => p.playerId === playerId);
                if (player) {
                    player.hasSeenRole = true;
                    console.log(`Player ${player.name} in room ${roomCode} has seen their role`);
                    const allSeen = room.players.every(p => p.hasSeenRole);
                    if (allSeen) {
                        if (room.gameMode === 'CHOR_POLISH') {
                            console.log(`All players in room ${roomCode} have seen their roles. Moving to GUESSING.`);
                            room.phase = 'GUESSING';
                            room.guessOptions = room.players
                                .filter(p => p.role !== 'POLICE')
                                .map(p => p.name)
                                .sort(() => Math.random() - 0.5);
                        } else {
                            console.log(`All players in room ${roomCode} have seen their cards. Moving to PLAYING.`);
                            room.phase = 'PLAYING';
                        }
                    }
                    await room.save();
                    io.to(roomCode).emit('roomUpdated', room);

                    // Check if phase change triggered a robot turn
                    checkAndTriggerRobotAction(roomCode);
                }
            }
        } catch (err) {
            console.error('roleSeen socket error:', err);
        }
    });

    socket.on('makeGuess', async (data) => {
        try {
            const { roomCode, guessedPlayerName } = typeof data === 'string' ? JSON.parse(data) : data;
            const room = await Room.findOne({ roomCode });
            if (room && room.phase === 'GUESSING') {
                const isCorrect = guessedPlayerName === room.chorPlayer;
                room.isGuessCorrect = isCorrect;
                room.selectedChorGuess = guessedPlayerName;
                room.phase = 'RESULT';

                console.log(`Guess made in room ${roomCode}: ${guessedPlayerName}. Correct: ${isCorrect}`);

                // Scoring Logic (Police finds Thief)
                room.players.forEach(p => {
                    let roundScore = 0;
                    if (p.role === 'KING') roundScore = 1000;
                    else if (p.role === 'MINISTER') roundScore = 800;
                    else if (p.role === 'POLICE') roundScore = isCorrect ? 500 : 0;
                    else if (p.role === 'THIEF') roundScore = isCorrect ? 0 : 500;
                    p.score += roundScore;
                });

                await room.save();
                io.to(roomCode).emit('guessResult', room);
            }
        } catch (err) {
            console.error('makeGuess socket error:', err);
        }
    });

    socket.on('passCard', async (data) => {
        try {
            const { roomCode, fromPlayerId, toPlayerId, card } = typeof data === 'string' ? JSON.parse(data) : data;
            const room = await Room.findOne({ roomCode });
            if (room && room.phase === 'PLAYING') {
                const fromCards = room.playerCards.get(fromPlayerId);
                const toCards = room.playerCards.get(toPlayerId);

                const cardIndex = fromCards.indexOf(card);
                if (cardIndex > -1) {
                    fromCards.splice(cardIndex, 1);
                    toCards.push(card);

                    room.playerCards.set(fromPlayerId, fromCards);
                    room.playerCards.set(toPlayerId, toCards);

                    // Check for winner
                    if (toCards.length === 4 && new Set(toCards).size === 1) {
                        const winner = room.players.find(p => p.playerId === toPlayerId);
                        room.winner = winner.name;
                        room.phase = 'RESULT';

                        // Scoring for Letter Set
                        room.players.forEach(p => {
                            if (p.playerId === toPlayerId) {
                                p.score += 100;
                            }
                        });
                    } else {
                        // Next turn
                        room.currentPlayerTurn = (room.currentPlayerTurn + 1) % 4;
                    }

                    await room.save();
                    io.to(roomCode).emit('roomUpdated', room);

                    // Check if next player is a robot
                    checkAndTriggerRobotAction(roomCode);
                }
            }
        } catch (err) {
            console.error('passCard socket error:', err);
        }
    });

    socket.on('nextRound', async (roomCode) => {
        try {
            console.log(`Starting next round for room ${roomCode}`);
            const room = await Room.findOne({ roomCode });
            if (room) {
                if (room.currentRound >= room.maxRounds) {
                    console.log(`Max rounds reached for room ${roomCode}. Finishing game.`);
                    room.phase = 'FINISHED';
                    await room.save();
                    io.to(roomCode).emit('gameFinished', room);
                    io.to(roomCode).emit('roomUpdated', room);
                    return;
                }

                room.currentRound += 1;
                room.phase = 'REVEAL';
                room.winner = null;
                room.isGuessCorrect = null;
                room.selectedChorGuess = null;

                if (room.gameMode === 'CHOR_POLISH') {
                    const roles = ['KING', 'MINISTER', 'THIEF', 'POLICE'];
                    const shuffledRoles = [...roles].sort(() => Math.random() - 0.5);
                    room.players.forEach((player, index) => {
                        player.role = shuffledRoles[index];
                        player.hasSeenRole = false;
                        if (player.role === 'KING') room.rajaPlayer = player.name;
                        if (player.role === 'MINISTER') room.mantriPlayer = player.name;
                        if (player.role === 'THIEF') room.chorPlayer = player.name;
                        if (player.role === 'POLICE') {
                            room.sipahiPlayer = player.name;
                            room.sipahiPlayerId = player.playerId;
                        }
                    });
                } else if (room.gameMode === 'LETTER_SET') {
                    const deck = [];
                    ['A', 'B', 'C', 'D'].forEach(l => {
                        for(let i=0; i<4; i++) deck.push(l);
                    });

                    let validDistribution = false;
                    const playerCards = {};

                    while (!validDistribution) {
                        deck.sort(() => Math.random() - 0.5);
                        validDistribution = true;

                        for (let i = 0; i < 4; i++) {
                            const hand = deck.slice(i * 4, (i + 1) * 4);
                            if (hand.every(card => card === hand[0])) {
                                validDistribution = false;
                                break;
                            }
                        }
                    }

                    room.players.forEach((player, index) => {
                        playerCards[player.playerId] = deck.slice(index * 4, (index + 1) * 4);
                        player.hasSeenRole = false;
                    });
                    room.playerCards = playerCards;
                    room.currentPlayerTurn = 0;
                }

                await room.save();
                io.to(roomCode).emit('roomUpdated', room);

                // Check if robots are playing
                checkAndTriggerRobotAction(roomCode);
            }
        } catch (err) {
            console.error('nextRound socket error:', err);
        }
    });

    socket.on('finishGame', async (data) => {
        try {
            const roomCode = typeof data === 'string' ? data : data.roomCode;
            console.log(`Finishing game attempt for room: ${roomCode}`);
            const room = await Room.findOne({ roomCode });
            if (room) {
                room.phase = 'FINISHED';
                await room.save();
                console.log(`Room ${roomCode} phase set to FINISHED. Emitting gameFinished...`);
                io.to(roomCode).emit('gameFinished', room);
                io.to(roomCode).emit('roomUpdated', room);
            } else {
                console.warn(`Room ${roomCode} not found for finishGame`);
            }
        } catch (err) {
            console.error('finishGame socket error:', err);
        }
    });

    socket.on('restartMatch', async (roomCode) => {
        try {
            console.log(`Restarting match for room ${roomCode}`);
            const room = await Room.findOne({ roomCode });
            if (room) {
                room.currentRound = 1;
                room.phase = 'REVEAL';
                room.winner = null;
                room.isGuessCorrect = null;
                room.selectedChorGuess = null;
                room.players.forEach(p => {
                    p.score = 0;
                    p.hasSeenRole = false;
                });

                if (room.gameMode === 'CHOR_POLISH') {
                    const roles = ['KING', 'MINISTER', 'THIEF', 'POLICE'];
                    const shuffledRoles = [...roles].sort(() => Math.random() - 0.5);
                    room.players.forEach((player, index) => {
                        player.role = shuffledRoles[index];
                        if (player.role === 'KING') room.rajaPlayer = player.name;
                        if (player.role === 'MINISTER') room.mantriPlayer = player.name;
                        if (player.role === 'THIEF') room.chorPlayer = player.name;
                        if (player.role === 'POLICE') {
                            room.sipahiPlayer = player.name;
                            room.sipahiPlayerId = player.playerId;
                        }
                    });
                } else if (room.gameMode === 'LETTER_SET') {
                    const deck = [];
                    ['A', 'B', 'C', 'D'].forEach(l => {
                        for(let i=0; i<4; i++) deck.push(l);
                    });
                    deck.sort(() => Math.random() - 0.5);
                    const playerCards = {};
                    room.players.forEach((player, index) => {
                        playerCards[player.playerId] = deck.slice(index * 4, (index + 1) * 4);
                    });
                    room.playerCards = playerCards;
                    room.currentPlayerTurn = 0;
                }

                await room.save();
                io.to(roomCode).emit('roomUpdated', room);

                checkAndTriggerRobotAction(roomCode);
            }
        } catch (err) {
            console.error('restartMatch socket error:', err);
        }
    });

    socket.on('sendEmoji', (data) => {
        try {
            const { roomCode, emoji, playerName } = typeof data === 'string' ? JSON.parse(data) : data;
            console.log(`Emoji ${emoji} sent by ${playerName} in room ${roomCode}`);
            io.to(roomCode).emit('emojiReceived', { emoji, playerName });
        } catch (err) {
            console.error('sendEmoji socket error:', err);
        }
    });

    socket.on('disconnect', async () => {
        try {
            const room = await Room.findOne({ "players.socketId": socket.id });
            if (room) {
                const player = room.players.find(p => p.socketId === socket.id);
                if (player) {
                    player.isDisconnected = true;
                    player.disconnectedAt = new Date();
                    await room.save();
                    console.log(`Player ${player.name} (${player.playerId}) disconnected from room ${room.roomCode}`);

                    // Inform others
                    io.to(room.roomCode).emit('roomUpdated', room);

                    // Check if the disconnected player was blocking the game
                    checkAndTriggerRobotAction(room.roomCode);
                }
            }
        } catch (err) {
            console.error('disconnect handler error:', err);
        }
    });
};
