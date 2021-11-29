package com.example.habitshare;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomHabitListAdapter extends RecyclerView.Adapter<CustomHabitListAdapter.HabitViewHolder> {
    private ArrayList<Habit> habits;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CustomHabitListAdapter(Context context, ArrayList<Habit> habits) {
        this.habits = habits;
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
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_my_habit_list, parent, false);
        HabitViewHolder viewHolder = new HabitViewHolder(view, onItemClickListener);
        return viewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        String dateToStart = "Date to Start: " + habit.getDate();
        String frequency = "Frequency: " + habit.getSelectDayOfWeek();
        String status;
        double denoteOnTimeRate = habit.getDenoteOnTimeRate();
        if(habit.getStatus()){
            status = "Status: Done";
        }
        else{
            status = "Status: Not Done";
        }

        // if the user is able to denote this habit on time by more than 85% of the times then the title of the habit is green
        // between 85% and 75%, the title will be yellow
        // less then 75%, the title will be red to warn the user that he/she didn't follow the plan closely
        if(Double.compare(denoteOnTimeRate, 0.85) >= 0){
            holder.habitTitleView.setTextColor(Color.GREEN);
        }
        if(Double.compare(denoteOnTimeRate, 0.85) < 0 & Double.compare(denoteOnTimeRate, 0.75) >= 0){
            holder.habitTitleView.setTextColor(Color.YELLOW);
        }
        if(Double.compare(denoteOnTimeRate, 0.75) < 0){
            holder.habitTitleView.setTextColor(Color.RED);
        }
        holder.habitTitleView.setText(habit.getTitle());
        holder.dateToStartView.setText(dateToStart);
        holder.frequencyView.setText(frequency);
        holder.statusView.setText(status);
    }

    /**
     * Allows the programmer to define the behavior after clicking on an item in the recyclerview
     * @param onItemClickListener Interface definition for a callback to be invoked when an item in this AdapterView has been clicked.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * @return the total number of habits in the list
     */
    @Override
    public int getItemCount() {
        return habits.size();
    }

    /**
     * A custom view holder for each of the habits
     */
    public class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitleView, dateToStartView, frequencyView, statusView;
        OnItemClickListener onItemClickListener;

        public HabitViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            habitTitleView = itemView.findViewById(R.id.habit_name);
            dateToStartView = itemView.findViewById(R.id.habit_date);
            frequencyView = itemView.findViewById(R.id.habit_frequency);
            statusView = itemView.findViewById(R.id.habit_status);
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
