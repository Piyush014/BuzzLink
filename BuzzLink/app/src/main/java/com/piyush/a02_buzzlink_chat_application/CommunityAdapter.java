package com.piyush.a02_buzzlink_chat_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommunityUser> usersArrayList;
    private OnAddUserClickListener onAddUserClickListener;

    public CommunityAdapter(Context context, ArrayList<CommunityUser> usersArrayList, OnAddUserClickListener onAddUserClickListener) {
        this.context = context;
        this.usersArrayList = usersArrayList;
        this.onAddUserClickListener = onAddUserClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.community_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityUser user = usersArrayList.get(position);
        holder.username.setText(user.getUserName());
        holder.userstatus.setText(user.getStatus());
        Picasso.get().load(user.getProfilepic()).into(holder.userimg);

        holder.useradd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddUserClickListener.onAddUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username;
        TextView userstatus;
        ImageView useradd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
            useradd = itemView.findViewById(R.id.useradd);
        }
    }

    public interface OnAddUserClickListener {
        void onAddUserClick(CommunityUser user);
    }
}
