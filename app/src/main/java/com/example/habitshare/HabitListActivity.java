//Jake Sample

package com.example.habitshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HabitListActivity extends AppCompatActivity {

    private ListView habitList;
    private static ArrayAdapter<Habit> habitAdapter;
    private static ArrayList<Habit> habitDataList;

    Button add;

    private static Habit selectedHabit = new Habit ("Placeholder", "01-01-0001", "never", "If you're seeing this, it means selectedHabit was never properly initialized");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitlist);

        habitList = findViewById(R.id.habitListHabitList);
        add = findViewById(R.id.habitListAddButton);
        habitDataList = new ArrayList<>();

        /*
        //Comment out if you want to remove the example
        */
        Habit habit1 = new Habit("Walk Dog", "30-10-2021", "1 daily", "Doggy needs the exercise");
        Habit habit2 = new Habit("Take Medication", "23-09-2020", "2 daily", "Take my medication 'fukitol' at least twice a day");
        Habit habit3 = new Habit("Order 66", "03-11-2021", "1 weekly", "Execute Order 66");
        habitDataList.add(habit1);
        habitDataList.add(habit2);
        habitDataList.add(habit3);

        habitAdapter = new CustomHabitList(this, habitDataList);
        habitList.setAdapter(habitAdapter);

        //Register clicking an item on the list and record it
        habitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedHabit = habitAdapter.getItem(i);
                startActivity(new Intent(HabitListActivity.this, HabitInfoActivity.class));
            }
        });

        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add Button has been clicked
                startActivity(new Intent(HabitListActivity.this, HabitEditActivity.class));
            }
        };

        add.setOnClickListener(addListener);



    }//end onCreate

    public static Habit getHabit(){
        return selectedHabit;
    }

    //Currently unused
    public static ArrayList<Habit> getHabitDataList(){
        return habitDataList;
    }

    public static void updateData(){
        habitAdapter.notifyDataSetChanged();
    }

    public static void removeHabit(){
        habitDataList.remove(selectedHabit);
        habitAdapter.notifyDataSetChanged();
    }

    public static void addHabit(Habit habit){
        habitDataList.add(habit);
        habitAdapter.notifyDataSetChanged();
    }

}//end HabitListActivity
