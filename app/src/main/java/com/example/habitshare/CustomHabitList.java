//Jake Sample

package com.example.habitshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This class serves to create the custom list displayed on the layout activity_habitlist used by the activity HabitListActivity
 * The layout file displays the Habit Name on the left, and the Frequency to the right
 */
public class CustomHabitList extends ArrayAdapter<Habit> {

    private ArrayList<Habit> habits;
    private Context context;

    /**
     * Custom list constructor
     * @param context
     * @param habits
     */
    public CustomHabitList(Context context, ArrayList<Habit> habits){
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }//end CustomHabitList constructor

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_habitlist, parent,false);
        }

        Habit habit = habits.get(position);

        TextView habitName = view.findViewById(R.id.habitText);
        TextView habitFrequency = view.findViewById(R.id.frequencyText);

        habitName.setText(habit.getName());
        habitFrequency.setText(habit.getFrequency());

        return view;

    }//end getView

    /**
     * This function will add a habit object to the list
     * @param habit
     */
    public void addHabit(Habit habit){
        habits.add(habit);
    }//end addHabit

    /**
     * This function will delete a habit object from the list
     * @param habit
     */
    public void deleteHabit(Habit habit){
        habits.remove(habit);
    }//end deleteHabit

}//end CustomHabitList
