const mongoose = require('mongoose');

const onlinePlayerSchema = new mongoose.Schema({
    playerId: { type: String, required: true, unique: true },
    name: { type: String, required: true },
    socketId: { type: String, required: true },
    isAvailable: { type: Boolean, default: true },
    lastSeen: { type: Date, default: Date.now, index: { expires: '5m' } } // Auto-cleanup after 5 mins of inactivity
});

module.exports = mongoose.model('OnlinePlayer', onlinePlayerSchema);
