package com.example.habitshare;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class AddHabitActivity extends AppCompatActivity {
    private final static String TAG = "showAddHabitActivity";
    EditText enterHabitTitle;
    EditText enterReason;
    Button  selectDate;
    Button confirm;
    Button cancelAddHabit;
    CheckBox monCheckBox;
    CheckBox tueCheckBox;
    CheckBox wedCheckBox;
    CheckBox thuCheckBox;
    CheckBox friCheckBox;
    CheckBox satCheckBox;
    CheckBox sunCheckBox;
    TextView dateView;
    TextView textViewSelectDaysOfAWeek;
    FirebaseFirestore db;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String date;
    boolean checkConfirmCondition;
    int position;
    String previousTitle;
    String habitTitle;
    String reason;
    String daysOfWeek;
    String lastTimeDenoted;
    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        enterHabitTitle = findViewById(R.id.enter_habit_title);
        enterReason = findViewById(R.id.enter_reason);
        selectDate = findViewById(R.id.button_select_date);
        confirm = findViewById(R.id.button_delete_view_habit);
        dateView = findViewById(R.id.view_date_view_habit);
        textViewSelectDaysOfAWeek = findViewById(R.id.textview_select_days_of_a_week);
        cancelAddHabit = findViewById(R.id.button_cancel_view_habit);
        monCheckBox = findViewById(R.id.checkBox_monday);
        tueCheckBox = findViewById(R.id.checkBox_tuesday);
        wedCheckBox = findViewById(R.id.checkBox_wednesday);
        thuCheckBox = findViewById(R.id.checkBox_thursday);
        friCheckBox = findViewById(R.id.checkBox_friday);
        satCheckBox = findViewById(R.id.checkBox_saturday);
        sunCheckBox = findViewById(R.id.checkBox_sunday);

        Intent intent = getIntent();
        int requestCode = intent.getIntExtra("request_code", 0);
        position = intent.getIntExtra("position", 0);

        if(requestCode == 1){
            habitTitle = intent.getStringExtra("habit_title");
            date = intent.getStringExtra("date");
            reason = intent.getStringExtra("reason");
            daysOfWeek = intent.getStringExtra("days_of_week");
            lastTimeDenoted = intent.getStringExtra("last_time_denoted");

            previousTitle = habitTitle;
            Habit habit = new Habit(habitTitle, date, reason, daysOfWeek);
            boolean[] daysOfWeekList = habit.getSelectDayOfWeekList();
            enterHabitTitle.setText(habitTitle);
            enterReason.setText(reason);
            dateView.setText(date);
            for(int j = 0; j < daysOfWeekList.length; j++){
                if (daysOfWeekList[j]) {
                    switch (j) {
                        case 1:
                            monCheckBox.setChecked(true);
                            break;
                        case 2:
                            tueCheckBox.setChecked(true);
                            break;
                        case 3:
                            wedCheckBox.setChecked(true);
                            break;
                        case 4:
                            thuCheckBox.setChecked(true);
                            break;
                        case 5:
                            friCheckBox.setChecked(true);
                            break;
                        case 6:
                            satCheckBox.setChecked(true);
                            break;
                        case 0:
                            sunCheckBox.setChecked(true);
                    }
                }
            }
        }

        loadingDialog = new LoadingDialog(AddHabitActivity.this);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        setDate();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewSelectDaysOfAWeek.setTextColor(Color.BLACK);
                checkConfirmCondition = true;
                final String habitTitle = enterHabitTitle.getText().toString();
                final String reason = enterReason.getText().toString();
                final String date = dateView.getText().toString();
                Habit habit = new Habit(habitTitle, date);
                setSelectedDays(habit);
                daysOfWeek = habit.getSelectDayOfWeek();
                // Check constraints of user input
                Log.d(TAG, "Reached here");
                // check other constraints
                if (date.equals("yyyy-mm-dd")) {
                    Snackbar.make(findViewById(R.id.add_habit), "Please select a date", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Set a Date", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectDate.performClick();
                                }
                            }).show();
                    checkConfirmCondition = false;
                }
                if (daysOfWeek.equals("")) {
                    textViewSelectDaysOfAWeek.setTextColor(Color.RED);
                    Snackbar setSelectDaysSnackBar = Snackbar.make(findViewById(R.id.add_habit), "Please select days of a week", Snackbar.LENGTH_INDEFINITE);
                    setSelectDaysSnackBar.show();
                    checkConfirmCondition = false;
                }

                if (reason.equals("")) {
                    enterReason.setError("Reason cannot be empty");
                    checkConfirmCondition = false;
                }

                if (habitTitle.length() > 20) {
                    enterHabitTitle.setError("Cannot longer than 20 characters");
                    checkConfirmCondition = false;
                }
                if (reason.length() > 30) {
                    enterReason.setError("Cannot longer than 30 characters");
                }
                if (habitTitle.equals("")) {
                    enterHabitTitle.setError("Habit Title cannot be empty");
                    checkConfirmCondition = false;
                } else {
                    if (requestCode == 0) {
                        loadingDialog.startLoadingDialog();
                        collectionReference.document(habitTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot habitDocument = task.getResult();
                                loadingDialog.dismissLoadingDialog();
                                if (task.isSuccessful()) {
                                    assert habitDocument != null;
                                    // check the existence of the habit
                                    if (habitDocument.exists()) {
                                        enterHabitTitle.setError("This habit has already existed, please enter another one");
                                        checkConfirmCondition = false;
                                    } else {
                                        if (checkConfirmCondition) {
                                            HashMap<String, Object> data = new HashMap<>();
                                            data.put("Date of Start", date);
                                            data.put("Days of Week", daysOfWeek);
                                            data.put("Reason", reason);
                                            data.put("Status", false);
                                            data.put("Last Time Denoted", "");
                                            data.put("Position", position);
                                            collectionReference
                                                    .document(habitTitle)
                                                    .set(data)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "Data has been added successfully!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Data could not be added!" + e.toString());
                                                        }
                                                    });
                                            finish();
                                        }
                                    }
                                }

                            }
                        });
                    } else {
                        if (checkConfirmCondition) {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("Date of Start", date);
                            data.put("Days of Week", daysOfWeek);
                            data.put("Reason", reason);
                            data.put("Status", false);
                            data.put("Last Time Denoted", lastTimeDenoted);
                            data.put("Position", position);
                            if(!habitTitle.equals(previousTitle)){
                                collectionReference
                                        .document(previousTitle)
                                        .delete();
                            }
                            collectionReference
                                    .document(habitTitle)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "Data has been added successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Data could not be added!" + e.toString());
                                        }
                                    });
                            finish();
                        }

                    }
                }
            }
        });

        cancelAddHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Allows an activity or fragment to use the android built-in date picker
     */
    private void setDate(){
        /* This function will implement selecting a date from the android built-in DatePickerDialog.
         *  The approach is adapted from https://www.youtube.com/watch?v=hwe1abDO2Ag */

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddHabitActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String monthStr = Integer.toString(month);
                String dayStr = Integer.toString(day);
                if(month < 10){
                    monthStr = "0" + month;
                }
                if(day < 10){
                    dayStr  = "0" + day ;
                }
                date = year + "-" + monthStr + "-" + dayStr;
                dateView.setText(date);
            }
        };
    }

    /**
     * Set selected days of a habit from checkboxes
     * @param habit a class the represents a user's habit
     */
    private void setSelectedDays(Habit habit){
        if(monCheckBox.isChecked()){
            habit.selectDayOfWeek(1);
        }
        if(tueCheckBox.isChecked()){
            habit.selectDayOfWeek(2);
        }
        if(wedCheckBox.isChecked()){
            habit.selectDayOfWeek(3);
        }
        if(thuCheckBox.isChecked()){
            habit.selectDayOfWeek(4);
        }
        if(friCheckBox.isChecked()){
            habit.selectDayOfWeek(5);
        }
        if(satCheckBox.isChecked()){
            habit.selectDayOfWeek(6);
        }
        if(sunCheckBox.isChecked()){
            habit.selectDayOfWeek(0);
        }
    }
}