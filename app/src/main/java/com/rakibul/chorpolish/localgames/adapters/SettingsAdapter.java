package com.rakibul.chorpolish.localgames.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.rakibul.chorpolish.R;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private final List<SettingItem> items;
    private final OnSettingClickListener listener;

    public interface OnSettingClickListener {
        void onSettingClick(int position);
        void onToggle(int position, boolean checked);
    }

    public SettingsAdapter(List<SettingItem> items, OnSettingClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_local, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingItem item = items.get(position);
        holder.tvName.setText(item.name);
        holder.ivIcon.setImageResource(item.iconRes);

        if (item.isToggle) {
            holder.switchSetting.setVisibility(View.VISIBLE);
            holder.ivArrow.setVisibility(View.GONE);
            holder.switchSetting.setChecked(item.isChecked);
            holder.switchSetting.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onToggle(position, isChecked));
        } else {
            holder.switchSetting.setVisibility(View.GONE);
            holder.ivArrow.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!item.isToggle) {
                listener.onSettingClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SettingItem {
        String name;
        int iconRes;
        boolean isToggle;
        boolean isChecked;

        public SettingItem(String name, int iconRes, boolean isToggle, boolean isChecked) {
            this.name = name;
            this.iconRes = iconRes;
            this.isToggle = isToggle;
            this.isChecked = isChecked;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon, ivArrow;
        SwitchCompat switchSetting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSettingName);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            switchSetting = itemView.findViewById(R.id.switchSetting);
        }
    }
}