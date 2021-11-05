//Jake Sample

package com.example.habitshare;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HabitEditActivity extends AppCompatActivity {

    Button confirm;
    EditText name;
    EditText frequency;
    EditText date;
    EditText reason;
    //The below three exist just in case we ever want to change their values on the fly
    TextView nameCharLimit;
    TextView frequencyFormat;
    TextView dateFormat;

    Habit selectedHabit = HabitListActivity.getHabit();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habiteditadd);

        confirm = findViewById(R.id.habitEditConfirmButton);
        name = findViewById(R.id.habitEditHabitName);
        frequency = findViewById(R.id.habitEditFrequency);
        date = findViewById(R.id.habitEditDate);
        reason = findViewById(R.id.habitEditDescription);
        nameCharLimit = findViewById(R.id.habitEditCharacterLimit);
        frequencyFormat = findViewById(R.id.habitEditFrequencyFormat);
        dateFormat = findViewById(R.id.habitEditDateFormat);

        //Run different code based on where you reached this activity from
        if (HabitInfoActivity.getFromEdit()){
            /**
             * If you reached this activity because you wanted to edit an existing habit
             */
            name.setText(selectedHabit.getName());
            frequency.setText(selectedHabit.getFrequency());
            date.setText(selectedHabit.getDate());
            reason.setText(selectedHabit.getReason());

        } else {
            /**
             * Otherwise you must have reached this activity because you wanted to add a new habit
             */
            //Don't prewrite data into the fields like when editing an existing habit

            //Anything else you may want to do (which is currently nothing but better leave this here just to be safe)

        }//end if

        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirm button has been clicked
                if (HabitInfoActivity.getFromEdit()){
                    //If you are editing an existing habit

                    selectedHabit.setName(name.getText().toString());
                    selectedHabit.setFrequency(frequency.getText().toString());
                    selectedHabit.setDate(date.getText().toString());
                    selectedHabit.setReason(reason.getText().toString());

                } else {
                    //If you are adding a new habit

                    Habit newHabit = new Habit("Placeholder", "Placeholder", "Placeholder", "Placeholder");
                    newHabit.setName(name.getText().toString());
                    newHabit.setFrequency(frequency.getText().toString());
                    newHabit.setDate(date.getText().toString());
                    newHabit.setReason(reason.getText().toString());

                    HabitListActivity.addHabit(newHabit);

                }//end if

                HabitInfoActivity.setFromEdit(false);
                HabitListActivity.updateData();
                startActivity(new Intent(HabitEditActivity.this, HabitListActivity.class));
            }
        };//end confirmListener

        confirm.setOnClickListener(confirmListener);

    }//end onCreate

}//end HabitEditActivity
