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


public class CustomHabitEventListAdapter extends RecyclerView.Adapter<CustomHabitEventListAdapter.HabitEventViewHolder> {
    private ArrayList<HabitEvent> habitEvents;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CustomHabitEventListAdapter(Context context, ArrayList<HabitEvent> habitEvents) {
        this.habitEvents = habitEvents;
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
    public HabitEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_habit_event, parent, false);
        HabitEventViewHolder habitEventViewHolder = new HabitEventViewHolder(view, onItemClickListener);
        return habitEventViewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HabitEventViewHolder holder, int position) {
        HabitEvent habitEvent = habitEvents.get(position);
        holder.habitNameView.setText(habitEvent.getTitle());
        holder.denoteDateView.setText(habitEvent.getDenoteDate());
        Log.d("Event Adapter", "Here");
    }

    /**
     * @return the total number of habit events in the list
     */
    @Override
    public int getItemCount() {
        return habitEvents.size();
    }

    /**
     * Allows the programmer to define the behavior after clicking on an item in the recyclerview
     * @param onItemClickListener Interface definition for a callback to be invoked when an item in this AdapterView has been clicked.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * A custom view holder for each of the habits
     */
    public class HabitEventViewHolder extends RecyclerView.ViewHolder {
        TextView habitNameView, denoteDateView;
        OnItemClickListener onItemClickListener;

        public HabitEventViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            habitNameView = itemView.findViewById(R.id.habit_event_name);
            denoteDateView = itemView.findViewById(R.id.date_habit_event);
            if(onItemClickListener != null){
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
