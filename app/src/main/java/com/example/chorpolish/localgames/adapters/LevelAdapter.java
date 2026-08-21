package com.rakibul.chorpolish.localgames.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rakibul.chorpolish.R;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {

    private final int totalLevels;
    private final int unlockedLevel;
    private final OnLevelClickListener listener;

    public interface OnLevelClickListener {
        void onLevelClick(int level);
    }

    public LevelAdapter(int totalLevels, int unlockedLevel, OnLevelClickListener listener) {
        this.totalLevels = totalLevels;
        this.unlockedLevel = unlockedLevel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int level = position + 1;
        holder.tvLevelNum.setText(String.valueOf(level));

        if (level <= unlockedLevel) {
            holder.ivLock.setVisibility(View.GONE);
            holder.tvLevelNum.setVisibility(View.VISIBLE);
            holder.levelContainer.setAlpha(1.0f);
            if (level == unlockedLevel) {
                holder.levelContainer.setBackgroundResource(R.drawable.btn_premium_gold);
            } else {
                holder.levelContainer.setBackgroundResource(R.drawable.card_glass_bg);
            }
        } else {
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.tvLevelNum.setVisibility(View.GONE);
            holder.levelContainer.setAlpha(0.5f);
            holder.levelContainer.setBackgroundResource(R.drawable.card_locked_bg);
        }

        holder.itemView.setOnClickListener(v -> {
            if (level <= unlockedLevel) {
                v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                listener.onLevelClick(level);
            }
        });
    }

    @Override
    public int getItemCount() {
        return totalLevels;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLevelNum;
        ImageView ivLock;
        View levelContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLevelNum = itemView.findViewById(R.id.tvLevelNum);
            ivLock = itemView.findViewById(R.id.ivLock);
            levelContainer = itemView.findViewById(R.id.levelContainer);
        }
    }
}