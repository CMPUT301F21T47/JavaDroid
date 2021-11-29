package com.example.habitshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ViewHabitActivity extends AppCompatActivity {
    // view variables
    TextView viewHabitTitle;
    TextView viewDate;
    TextView viewDaysOfWeek;
    TextView viewReason;
    TextView viewIsDisclosed;
    Button delete;
    Button cancelViewHabit;
    Button editHabit;
    Button denoteHabit;

    // data variables
    FirebaseFirestore db;
    String date;
    String habitTitle;
    String reason;
    String daysOfWeek;
    String lastTimeDenoted;
    boolean status;
    boolean isDisclosed;
    int controlCode;
    int position;
    int listSize;
    int numberOfTimesActuallyDenoted;
    int i;
    LoadingDialog loadAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);

        // find view by id
        viewHabitTitle = findViewById(R.id.view_habit_title);
        viewDate = findViewById(R.id.view_date_view_habit);
        viewDaysOfWeek = findViewById(R.id.view_days_of_week);
        viewReason = findViewById(R.id.view_reason);
        viewIsDisclosed = findViewById(R.id.view_is_disclosed);
        delete = findViewById(R.id.button_confirm_add_habit);
        cancelViewHabit = findViewById(R.id.button_cancel_view_habit);
        editHabit = findViewById(R.id.button_edit_habit);
        denoteHabit = findViewById(R.id.button_denote_habit);

        // initialize some variables
        db =  FirebaseFirestore.getInstance();
        loadAnimation = new LoadingDialog(ViewHabitActivity.this);

        // get data from intent
        Intent intent = getIntent();
        habitTitle = intent.getStringExtra("habit_title");
        date = intent.getStringExtra("date");
        reason = intent.getStringExtra("reason");
        daysOfWeek = intent.getStringExtra("days_of_week");
        status = intent.getBooleanExtra("status", false);
        lastTimeDenoted = intent.getStringExtra("last_time_denoted");
        position = intent.getIntExtra("position", 0);
        listSize = intent.getIntExtra("list_size", 0);
        isDisclosed = intent.getBooleanExtra("is_disclosed", false);
        controlCode = intent.getIntExtra("control_code", 0);
        numberOfTimesActuallyDenoted = intent.getIntExtra("number_time_actual_denoted",0);

        if(controlCode == 1){
            // controlCode being 1 means the user is viewing other users' habits
            // we hide those buttons so that the user is not able to edit other users' habits
            denoteHabit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            editHabit.setVisibility(View.INVISIBLE);
        }

        // set texts according to the attributes of the selected habit.
        viewHabitTitle.setText(habitTitle);
        viewDate.setText(date);
        viewDaysOfWeek.setText(daysOfWeek);
        viewReason.setText(reason);
        viewIsDisclosed.setText(String.valueOf(isDisclosed));

        // if a habit has been done this week then hide the denote button to prevent the user from denoting a habit that has been denoted
        if(status){
            denoteHabit.setVisibility(View.INVISIBLE);
        }

        // define behaviors of all four buttons
        denoteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDenoteHabitActivity();
                finish();
            }
        });

        editHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditHabitActivity();
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHabit();
                finish();
            }
        });

        cancelViewHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * send the necessary data to the DenoteHabitActivity and start the activity
     */
    private void startDenoteHabitActivity(){
        Intent intent = new Intent(ViewHabitActivity.this, DenoteHabitActivity.class);
        intent.putExtra("habit_title", habitTitle);
        intent.putExtra("last_time_denoted", lastTimeDenoted);
        intent.putExtra("number_time_actual_denoted", numberOfTimesActuallyDenoted);
        startActivity(intent);
    }

    /**
     * send the necessary data to the AddHabitActivity and start the activity
     */
    private void startEditHabitActivity(){
        Intent intent = new Intent(ViewHabitActivity.this, AddHabitActivity.class);
        intent.putExtra("habit_title", habitTitle);
        intent.putExtra("date", date);
        intent.putExtra("days_of_week", daysOfWeek);
        intent.putExtra("reason", reason);
        intent.putExtra("last_time_denoted", lastTimeDenoted);
        intent.putExtra("control_code", 1);
        intent.putExtra("position", position);
        intent.putExtra("status", status);
        intent.putExtra("is_disclosed", isDisclosed);
        intent.putExtra("number_time_actual_denoted", numberOfTimesActuallyDenoted);
        startActivity(intent);
    }

    /**
     * delete the habit and its corresponding habit events
     */
    private void deleteHabit(){
        final CollectionReference habitsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");
        final CollectionReference habitEventsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");

        // delete the habit
        habitsReference
                .document(habitTitle)
                .delete();

        // delete all corresponding habit events
        habitEventsReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        String eventTitle = doc.getId();
                        String habitEventSubTitle = (String) doc.getData().get("Habit Title");
                        if(habitTitle.equals(habitEventSubTitle)){
                            habitEventsReference.document(eventTitle)
                                    .delete();
                        }
                    }
                }
            }
        });

        // update the indices of the habits
        habitsReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    i = 0;
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        String habitTitle = doc.getId();
                        Log.d("ViewHabitActivity", "habitTitle is" + habitTitle);
                        HashMap<String, Object> data = new HashMap<>();
                        int habitPosition = ((Long) doc.getData().get("Position")).intValue();
                        if(habitPosition > position){
                            data.put("Position", habitPosition - 1);
                        }
                        else{
                            data.put("Position", habitPosition);
                        }
                        habitsReference.document(habitTitle)
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                        i++;
                    }
                }
            }
        });
    }
}