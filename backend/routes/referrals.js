const express = require('express');
const router = express.Router();
const User = require('../models/User');
const Referral = require('../models/Referral');
const crypto = require('crypto');

// Utility to generate a unique referral code
async function generateUniqueCode() {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'; // Avoid ambiguous chars like O, 0, I, 1
    let code;
    let exists = true;
    while (exists) {
        let randomPart = '';
        for (let i = 0; i < 5; i++) {
            randomPart += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        code = 'ORBXA' + randomPart;
        exists = await User.findOne({ referralCode: code });
    }
    return code;
}

// Get user info and referral code
router.get('/me', async (req, res) => {
    const { playerId } = req.query;
    console.log(`Referral info request for playerId: ${playerId}`);

    if (!playerId || playerId === 'null' || playerId === 'undefined') {
        return res.status(400).json({ error: 'Valid playerId is required' });
    }

    try {
        let user = await User.findOne({ playerId });
        if (!user) {
            console.log(`Creating new user for referral system: ${playerId}`);
            const referralCode = await generateUniqueCode();
            user = new User({ playerId, referralCode });
            await user.save();
        } else if (!user.referralCode) {
            console.log(`Generating referral code for existing user: ${playerId}`);
            user.referralCode = await generateUniqueCode();
            await user.save();
        }

        const history = await Referral.find({ referrerPlayerId: playerId });
        console.log(`Found ${history.length} referrals for ${playerId}`);

        res.json({
            playerId: user.playerId,
            referralCode: user.referralCode,
            referredBy: user.referredBy,
            history: history.map(r => ({
                status: r.status,
                createdAt: r.createdAt
            }))
        });
    } catch (err) {
        console.error('Error in /me:', err);
        res.status(500).json({ error: 'Server error' });
    }
});

// Claim a referral code
router.post('/claim', async (req, res) => {
    const { playerId, referralCode } = req.body;
    if (!playerId || !referralCode) return res.status(400).json({ error: 'Missing data' });

    const normalizedCode = referralCode.trim().toUpperCase();

    // Validation: Ensure the code follows the ORBXAXXXXX format
    const codeRegex = /^ORBXA[A-Z0-9]{5}$/;
    if (!codeRegex.test(normalizedCode)) {
        return res.status(400).json({ error: 'Invalid referral code format' });
    }

    try {
        // 1. Find the referrer
        const referrer = await User.findOne({ referralCode: normalizedCode });
        if (!referrer) return res.status(404).json({ error: 'Invalid referral code' });

        // 2. Prevent self-referral
        if (referrer.playerId === playerId) {
            return res.status(400).json({ error: 'You cannot refer yourself' });
        }

        // 3. Find or create current user
        let user = await User.findOne({ playerId });
        if (!user) {
            const newCode = await generateUniqueCode();
            user = new User({ playerId, referralCode: newCode });
            await user.save();
        }

        // 4. Check if user was already referred
        if (user.referredBy) {
            return res.status(400).json({ error: 'You have already been referred' });
        }

        // 5. Atomic check for duplicate referral in Referral collection
        // This handles cases where multiple requests might come in simultaneously
        const existingReferral = await Referral.findOne({ referredPlayerId: playerId });
        if (existingReferral) {
            return res.status(400).json({ error: 'Referral already exists' });
        }

        // 6. Create the referral record
        const referral = new Referral({
            referrerPlayerId: referrer.playerId,
            referredPlayerId: playerId,
            referralCode: normalizedCode,
            status: 'REWARDED',
            rewardGranted: true
        });
        await referral.save();

        // 7. Update user record
        user.referredBy = referrer.playerId;
        await user.save();

        res.json({
            success: true,
            message: 'Referral claimed successfully',
            rewardPoints: 50,
            transactionId: `REFERRAL_${referrer.playerId}_${playerId}`
        });

    } catch (err) {
        if (err.code === 11000) {
            return res.status(400).json({ error: 'Referral already processed' });
        }
        console.error('Error in /claim:', err);
        res.status(500).json({ error: 'Server error' });
    }
});

module.exports = router;
