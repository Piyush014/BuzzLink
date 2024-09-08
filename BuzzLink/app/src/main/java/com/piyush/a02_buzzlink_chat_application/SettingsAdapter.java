package com.piyush.a02_buzzlink_chat_application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingItem> settingsList;
    private OnItemClickListener onItemClickListener;

    public SettingsAdapter(List<SettingItem> settingsList, OnItemClickListener onItemClickListener) {
        this.settingsList = settingsList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingItem item = settingsList.get(position);
        holder.setName.setText(item.getName());
        holder.content.setText(item.getDescription());

        int imageResId = holder.itemView.getContext().getResources().getIdentifier(item.getIconName(), "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext()).load(imageResId).into(holder.settingImage);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(SettingItem item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView settingImage;
        TextView setName;
        TextView content;

        ViewHolder(View itemView) {
            super(itemView);
            settingImage = itemView.findViewById(R.id.settingimg);
            setName = itemView.findViewById(R.id.setname);
            content = itemView.findViewById(R.id.content);
        }
    }
}
