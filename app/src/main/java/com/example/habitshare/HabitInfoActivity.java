//Jake Sample

package com.example.habitshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HabitInfoActivity extends AppCompatActivity {

    Habit selectedHabit = HabitListActivity.getHabit();

    Button denote;
    Button edit;
    Button delete;
    Button back;
    TextView name;
    TextView frequency;
    TextView date;
    TextView reason;

    private static Boolean fromEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitinfo);

        denote = findViewById(R.id.habitInfoDenoteButton);
        edit = findViewById(R.id.habitInfoEditButton);
        delete = findViewById(R.id.habitInfoDeleteButton);
        back = findViewById(R.id.habitInfoBackButton);
        name = findViewById(R.id.habitInfoHabitTextview);
        frequency = findViewById(R.id.habitInfoFrequencyTextview);
        date = findViewById(R.id.habitInfoDateTextview);
        reason = findViewById(R.id.habitInfoDescriptionTextview);

        name.setText("Habit Name: " + selectedHabit.getName());
        frequency.setText("Frequency: " + selectedHabit.getFrequency());
        date.setText("Start Date: " + selectedHabit.getDate());
        reason.setText("Description & Reason: " + selectedHabit.getReason());

        View.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Back Button has been clicked
                startActivity(new Intent(HabitInfoActivity.this, HabitListActivity.class));
            }
        };

        View.OnClickListener denoteListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Denote Button has been clicked
                //Whoever is adding the page that leads from Denote can add code here
            }
        };

        View.OnClickListener editListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Edit Button has been clicked
                fromEdit = true;
                startActivity(new Intent(HabitInfoActivity.this, HabitEditActivity.class));
            }
        };

        View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete Button has been clicked
                //HabitListActivity.getHabitDataList().remove(selectedHabit);
                //HabitListActivity.updateData();
                HabitListActivity.removeHabit();
                startActivity(new Intent(HabitInfoActivity.this, HabitListActivity.class));
            }
        };

        denote.setOnClickListener(denoteListener);
        edit.setOnClickListener(editListener);
        delete.setOnClickListener(deleteListener);
        back.setOnClickListener(backListener);

    }//end onCreate

    /**
     * Methods to keep track of whether HabitEditActivity is editing an existing habit, or adding a new one
     * @return
     */
    public static Boolean getFromEdit(){
        return fromEdit;
    }//end getFromEdit

    public static void setFromEdit(Boolean argument){
        fromEdit = argument;
    }//end setFromEdit

}//end HabitInfoActivity
