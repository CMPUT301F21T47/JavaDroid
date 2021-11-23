package com.example.habitshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_my_habit_list, parent, false);
        HabitViewHolder viewHolder = new HabitViewHolder(view, onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);
        String dateToStart = "Date to Start: " + habit.getDate();
        String frequency = "Frequency: " + habit.getSelectDayOfWeek();
        String status;
        if(habit.getStatus()){
            status = "Status: Done";
        }
        else{
            status = "Status: Not Done";
        }
        holder.habitTitleView.setText(habit.getTitle());
        holder.dateToStartView.setText(dateToStart);
        holder.frequencyView.setText(frequency);
        holder.statusView.setText(status);

    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

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
