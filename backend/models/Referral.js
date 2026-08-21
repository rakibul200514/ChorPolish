const mongoose = require('mongoose');

const referralSchema = new mongoose.Schema({
    referrerPlayerId: { type: String, required: true },
    referredPlayerId: { type: String, required: true, unique: true },
    referralCode: { type: String, required: true },
    status: { type: String, enum: ['PENDING', 'VERIFIED', 'REWARDED', 'REJECTED'], default: 'VERIFIED' },
    rewardGranted: { type: Boolean, default: false },
    createdAt: { type: Date, default: Date.now },
    verifiedAt: { type: Date, default: Date.now }
});

// One referred user can only be referred once
referralSchema.index({ referredPlayerId: 1 }, { unique: true });

module.exports = mongoose.model('Referral', referralSchema);
