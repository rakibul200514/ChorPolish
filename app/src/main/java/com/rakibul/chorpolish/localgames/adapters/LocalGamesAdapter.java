package com.rakibul.chorpolish.localgames.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rakibul.chorpolish.R;
import com.rakibul.chorpolish.localgames.models.LocalGameModel;

import java.util.List;

public class LocalGamesAdapter extends RecyclerView.Adapter<LocalGamesAdapter.ViewHolder> {

    private final List<LocalGameModel> gameList;
    private final OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(LocalGameModel game);
    }

    public LocalGamesAdapter(List<LocalGameModel> gameList, OnGameClickListener listener) {
        this.gameList = gameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalGameModel game = gameList.get(position);
        holder.tvGameName.setText(game.getTitle());
        holder.tvSubtitle.setText(game.getSubtitle());
        holder.tvDifficulty.setText(game.getDifficulty());
        holder.tvStatus.setText(game.getStatus());
        holder.ivGameIcon.setImageResource(game.getIconRes());

        if (game.isLocked()) {
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.container.setAlpha(0.6f);
        } else {
            holder.ivLock.setVisibility(View.GONE);
            holder.container.setAlpha(1.0f);
        }

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_up);
        animation.setStartOffset((long) position * 100);
        holder.itemView.startAnimation(animation);

        holder.itemView.setOnClickListener(v -> {
            if (!game.isLocked() && listener != null) {
                v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.button_click));
                listener.onGameClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGameName, tvSubtitle, tvDifficulty, tvStatus;
        ImageView ivGameIcon, ivLock;
        View container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameName = itemView.findViewById(R.id.tvGameName);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivGameIcon = itemView.findViewById(R.id.ivGameIcon);
            ivLock = itemView.findViewById(R.id.ivLock);
            container = itemView.findViewById(R.id.container);
        }
    }
}