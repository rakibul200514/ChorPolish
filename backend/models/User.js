const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    playerId: { type: String, required: true, unique: true },
    referralCode: { type: String, unique: true },
    referredBy: { type: String }, // playerId of the referrer
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('User', userSchema);
