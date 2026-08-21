package com.rakibul.chorpolish.data.rewards;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SharedPreferences implementation of RewardRepository for local persistence.
 * Stores points and transaction history in a dedicated "OrbixaRewardsPrefs" file.
 */
public class LocalRewardRepository implements RewardRepository {
    private static final String PREF_NAME = "OrbixaRewardsPrefs";
    private static final String KEY_POINTS = "orbixa_points";
    private static final String KEY_HISTORY = "reward_history";
    private static final String KEY_PROCESSED_IDS = "processed_transaction_ids";

    private final SharedPreferences prefs;

    public LocalRewardRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getCurrentPoints() {
        return prefs.getInt(KEY_POINTS, 0);
    }

    @Override
    public void updatePoints(int points) {
        prefs.edit().putInt(KEY_POINTS, points).apply();
    }

    @Override
    public List<RewardTransaction> getRewardHistory() {
        String historyJson = prefs.getString(KEY_HISTORY, "[]");
        List<RewardTransaction> history = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(historyJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RewardTransaction tx = new RewardTransaction(
                        obj.getString("id"),
                        RewardType.valueOf(obj.getString("type")),
                        RewardSource.valueOf(obj.getString("source")),
                        obj.getInt("points"),
                        obj.has("gameName") && !obj.isNull("gameName") ? obj.getString("gameName") : null,
                        obj.has("levelNumber") ? obj.getInt("levelNumber") : null
                );
                history.add(tx);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return history;
    }

    @Override
    public void saveTransaction(RewardTransaction transaction) {
        List<RewardTransaction> history = getRewardHistory();
        history.add(0, transaction); // Add to the beginning (latest first)
        
        JSONArray array = new JSONArray();
        for (RewardTransaction tx : history) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", tx.getId());
                obj.put("type", tx.getType().name());
                obj.put("source", tx.getSource().name());
                obj.put("points", tx.getPoints());
                obj.put("timestamp", tx.getTimestamp());
                obj.put("gameName", tx.getGameName());
                obj.put("levelNumber", tx.getLevelNumber());
                array.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        Set<String> processedIds = new HashSet<>(prefs.getStringSet(KEY_PROCESSED_IDS, new HashSet<>()));
        processedIds.add(transaction.getId());

        prefs.edit()
                .putString(KEY_HISTORY, array.toString())
                .putStringSet(KEY_PROCESSED_IDS, processedIds)
                .apply();
    }

    @Override
    public boolean isTransactionProcessed(String transactionId) {
        if (transactionId == null) return false;
        Set<String> processedIds = prefs.getStringSet(KEY_PROCESSED_IDS, new HashSet<>());
        return processedIds.contains(transactionId);
    }
}
