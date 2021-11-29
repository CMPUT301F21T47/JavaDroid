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
    private OnItemLongClickListener onItemLongClickListener;

    public CustomFriendListAdapter(Context context, ArrayList<Friend> friends) {
        this.friends = friends;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the given type. You can either create a new View manually or inflate it from an XML layout file.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public CustomFriendListAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_friend, parent, false);
        CustomFriendListAdapter.FriendViewHolder friendViewHolder = new CustomFriendListAdapter.FriendViewHolder(view, onItemClickListener, onItemLongClickListener);
        return friendViewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        Log.d("Search", friend.getUserName() + "_" + friend.getEmail());
        holder.friendName.setText(friend.getUserName());
        holder.friendEmail.setText(friend.getEmail());
    }

    /**
     * @return the total number of friends in the list
     */
    @Override
    public int getItemCount() {
        return friends.size();
    }

    /**
     * Allows the programmer to define the behavior after clicking on an item in the recyclerview
     * @param onItemClickListener Interface definition for a callback to be invoked when an item in this AdapterView has been clicked.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Allows the programmer to define the behavior after long pressed on an item in the recyclerview
     * @param onItemLongClickListener Interface definition for a callback to be invoked when an item in this AdapterView has been long pressed.
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * A custom view holder for each of the habits
     */
    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName, friendEmail;
        OnItemClickListener onItemClickListener;
        OnItemLongClickListener onItemLongClickListener;

        public FriendViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            this.onItemLongClickListener = onItemLongClickListener;
            friendName = itemView.findViewById(R.id.textview_friend_name);
            friendEmail = itemView.findViewById(R.id.textview_friend_email);

            if (onItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
            if(onItemLongClickListener != null){
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return onItemLongClickListener.onItemLongClick(getAdapterPosition());
                    }
                });
            }
        }
    }
}
