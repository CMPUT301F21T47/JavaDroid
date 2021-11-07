package com.example.habitshare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomHabitListAdapter extends ArrayAdapter<Habit> {
    private ArrayList<Habit> habits;
    private Context context;

    public CustomHabitListAdapter(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_my_habit_list, parent, false);
        }

        Habit habit = habits.get(position);

        // Find all xml variables by id
        TextView habitName = view.findViewById(R.id.habit_name);
        TextView habitDate = view.findViewById(R.id.habit_date);
        TextView habitFrequency = view.findViewById(R.id.habit_frequency);
        TextView habitStatus = view.findViewById(R.id.habit_status);

        // we want to display the value in the format we want
        habitName.setText(habit.getTitle());
        habitDate.setText("Date: " + habit.getDate());
        habitFrequency.setText("Frequency: " + habit.getSelectDayOfWeek());
        if(habit.getStatus()){
            habitStatus.setText("Status: Done");
        }
        else{
            habitStatus.setText("Status: Not Done");
        }

        return view;
    }
}
