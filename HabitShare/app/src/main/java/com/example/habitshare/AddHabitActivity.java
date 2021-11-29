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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
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

    // view variables
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
    Switch switchSetPublic;

    // data variables
    FirebaseFirestore db;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String date;
    boolean checkConfirmCondition;
    boolean status;
    boolean isDisclosed = false;
    int position;
    int controlCode;
    int numberOfTimesActuallyDenoted;
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

        // find view by id
        enterHabitTitle = findViewById(R.id.enter_habit_title);
        enterReason = findViewById(R.id.enter_reason);
        selectDate = findViewById(R.id.button_select_date);
        confirm = findViewById(R.id.button_confirm_add_habit);
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
        switchSetPublic = findViewById(R.id.switch_set_public);

        // initialize some variables
        Intent intent = getIntent();
        controlCode = intent.getIntExtra("control_code", 0); // controlCode being 1 means the user gets here to edit a habit
        position = intent.getIntExtra("position", 0);
        loadingDialog = new LoadingDialog(AddHabitActivity.this);
        db = FirebaseFirestore.getInstance();

        // when the app enters here to edit a habit, we need to put the origin details into the corresponding edittexts and checkboxes
        if(controlCode == 1){
            // get data from intent
            habitTitle = intent.getStringExtra("habit_title");
            date = intent.getStringExtra("date");
            reason = intent.getStringExtra("reason");
            daysOfWeek = intent.getStringExtra("days_of_week");
            lastTimeDenoted = intent.getStringExtra("last_time_denoted");
            status = intent.getBooleanExtra("status", false);
            isDisclosed = intent.getBooleanExtra("is_disclosed", false);
            numberOfTimesActuallyDenoted = intent.getIntExtra("number_time_actual_denoted",0);

            previousTitle = habitTitle; // keep a copy of the original title, so that later we can check if the title has been changed or not
            Habit habit = new Habit(habitTitle, date, reason, daysOfWeek);
            boolean[] daysOfWeekList = habit.getSelectDayOfWeekList();

            // set edittext according to the detail of the habit
            enterHabitTitle.setText(habitTitle);
            enterReason.setText(reason);
            dateView.setText(date);

            // set checkbox status according to the detail of the habit
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

            // set the status of the switch for set public
            switchSetPublic.setChecked(isDisclosed);
        }

        setDatePicker();

        switchSetPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ // when the switch is on, it means the user wants to set the habit as public
                    isDisclosed = true;
                }
                else{ // when the switch is off, it means the user wants to set the habit as private
                    isDisclosed = false;
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHabit();
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
     * self-explanatory, closes the activity when the process is done.
     */
    private void addHabit(){
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");
        textViewSelectDaysOfAWeek.setTextColor(Color.BLACK);
        checkConfirmCondition = true; // a flag to show whether the input is valid or not

        // convert input into strings
        final String habitTitle = enterHabitTitle.getText().toString();
        final String reason = enterReason.getText().toString();
        final String date = dateView.getText().toString();

        // get daysOfWeek
        Habit habit = new Habit(habitTitle, date);
        setSelectedDays(habit);
        daysOfWeek = habit.getSelectDayOfWeek();

        // check input constraints
        if (date.equals("yyyy-mm-dd")) {// Check if the date is selected
            Snackbar.make(findViewById(R.id.add_habit), "Please select a date", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Set a Date", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectDate.performClick(); // allows the user to open the DatePickerDialog by clicking on the button on the SnackBar
                        }
                    }).show();
            checkConfirmCondition = false;
        }
        if (daysOfWeek.equals("")) { // check if the daysOfWeek is selected
            textViewSelectDaysOfAWeek.setTextColor(Color.RED);
            Snackbar setSelectDaysSnackBar = Snackbar.make(findViewById(R.id.add_habit), "Please select days of a week", Snackbar.LENGTH_INDEFINITE);
            setSelectDaysSnackBar.show();
            checkConfirmCondition = false;
        }
        if (reason.equals("")) {// check if the reason is empty
            enterReason.setError("Reason cannot be empty");
            checkConfirmCondition = false;
        }
        if (habitTitle.length() > 20) { // check if length of habit title exceeds the constraint
            enterHabitTitle.setError("Cannot longer than 20 characters");
            checkConfirmCondition = false;
        }
        if (reason.length() > 30) {// check if length of reason exceeds the constraint
            enterReason.setError("Cannot longer than 30 characters");
        }
        if (habitTitle.equals("")) { // check if the title is empty
            enterHabitTitle.setError("Habit Title cannot be empty");
            checkConfirmCondition = false;
        } else {
            // when the app enters here to add a habit, then simply upload all the info of a habit
            if (controlCode == 0) {
                loadingDialog.startLoadingDialog();
                collectionReference.document(habitTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot habitDocument = task.getResult();
                        loadingDialog.dismissLoadingDialog();
                        if (task.isSuccessful()) {
                            assert habitDocument != null;
                            // check the existence of the habit
                            if (habitDocument.exists()) { // prevent the user from adding a repeated habit
                                enterHabitTitle.setError("This habit has already existed, please enter another one");
                                checkConfirmCondition = false;
                            } else {
                                if (checkConfirmCondition) { // check if all conditions are fit
                                    HashMap<String, Object> data = new HashMap<>();
                                    data.put("Date of Start", date);
                                    data.put("Days of Week", daysOfWeek);
                                    data.put("Reason", reason);
                                    data.put("Status", false);
                                    data.put("Last Time Denoted", "");
                                    data.put("Position", position);
                                    data.put("IsDisclosed", isDisclosed);
                                    data.put("NumberOfTimeActuallyDenoted", numberOfTimesActuallyDenoted);
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
            }
            else { // when the app enters here to edit a habit
                if (checkConfirmCondition) {// check if length of habit title exceeds the constraint
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("Date of Start", date);
                    data.put("Days of Week", daysOfWeek);
                    data.put("Reason", reason);
                    data.put("Status", status);
                    data.put("Last Time Denoted", lastTimeDenoted);
                    data.put("Position", position);
                    data.put("IsDisclosed", isDisclosed);
                    data.put("NumberOfTimeActuallyDenoted", numberOfTimesActuallyDenoted);

                    // if the app enters here to edit a habit, then we need to check if the title has been changed or not
                    if(!habitTitle.equals(previousTitle)){
                        collectionReference.document(habitTitle).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                lastTimeDenoted = documentSnapshot.getString("Last Time Denoted");
                                data.put("Last Time Denoted", lastTimeDenoted);

                                // if the the title has been changed, we need to delete the previous document on FireStore
                                // delete the previous habit
                                collectionReference
                                        .document(previousTitle)
                                        .delete();
                                // upload the modified habit
                                collectionReference
                                        .document(habitTitle)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                finish();
                                            }
                                        });
                            }
                        });
                    }
                    else {// if the title is not changed, then we simply update the attributes of the habit on FireStore
                        collectionReference
                                .document(habitTitle)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Data has been added successfully!");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Data could not be added!" + e.toString());
                                    }
                                });
                    }
                }
            }
        }
    }

    /**
     * Allows an activity or fragment to use the android built-in date picker
     */
    private void setDatePicker(){
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
                String monthStr = Integer.toString(month + 1);
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