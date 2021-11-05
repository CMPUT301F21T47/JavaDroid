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

public class CustomHabitEventListAdapter<H> extends ArrayAdapter {
    private ArrayList<HabitEvent> habitEvents;
    private Context context;
    public CustomHabitEventListAdapter(@NonNull Context context, ArrayList<HabitEvent> habitEvents ) {
        super(context, 0, habitEvents);
        this.habitEvents = habitEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_habit_event, parent, false);
        }

        HabitEvent habitEvent = habitEvents.get(position);

        // Find all xml variables by id
        TextView habitName = view.findViewById(R.id.habit_event_name);
        TextView denoteDate = view.findViewById(R.id.date_habit_event);

        habitName.setText(habitEvent.getTitle());
        denoteDate.setText(habitEvent.getDenoteDate());

        return view;
    }
}
