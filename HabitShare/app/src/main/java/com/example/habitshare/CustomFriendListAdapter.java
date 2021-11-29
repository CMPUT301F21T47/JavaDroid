package com.example.habitshare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomFriendListAdapter extends  RecyclerView.Adapter<CustomFriendListAdapter.FriendViewHolder>{
    private ArrayList<Friend> friends;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CustomFriendListAdapter(Context context, ArrayList<Friend> friends) {
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomFriendListAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_friend, parent, false);
        CustomFriendListAdapter.FriendViewHolder friendViewHolder = new CustomFriendListAdapter.FriendViewHolder(view, onItemClickListener);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        Log.d("Search", friend.getUserName() + "_" + friend.getEmail());
        holder.friendName.setText(friend.getUserName());
        holder.friendEmail.setText(friend.getEmail());
    }


    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName, friendEmail;
        OnItemClickListener onItemClickListener;

        public FriendViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            friendName = itemView.findViewById(R.id.textview_friend_name);
            friendEmail = itemView.findViewById(R.id.textview_friend_email);

            Log.d("In Item:", friendEmail + "_" +friendEmail);
            if (onItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }
}
