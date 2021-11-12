package com.example.habitshare;

import android.app.DatePickerDialog;
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
    TextView viewHabitTitle;
    TextView viewDate;
    TextView viewDaysOfWeek;
    TextView viewReason;
    Button delete;
    Button cancelViewHabit;
    Button editHabit;
    Button denoteHabit;
    FirebaseFirestore db;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String date;
    boolean checkConfirmCondition;
    String habitTitle;
    String reason;
    String daysOfWeek;
    String lastTimeDenoted;
    boolean status;
    int position;
    int listSize;
    int i;
    LoadingDialog loadAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);
        viewHabitTitle = findViewById(R.id.view_habit_title);
        viewDate = findViewById(R.id.view_date_view_habit);
        viewDaysOfWeek = findViewById(R.id.view_days_of_week);
        viewReason = findViewById(R.id.view_reason);
        delete = findViewById(R.id.button_delete_view_habit);
        cancelViewHabit = findViewById(R.id.button_cancel_view_habit);
        editHabit = findViewById(R.id.button_edit_habit);
        denoteHabit = findViewById(R.id.button_denote_habit);

        db =  FirebaseFirestore.getInstance();
        final CollectionReference habitsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");
        final CollectionReference habitEventsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
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

        // set texts according to the attributes of the selected habit.
        viewHabitTitle.setText(habitTitle);
        viewDate.setText(date);
        viewDaysOfWeek.setText(daysOfWeek);
        viewReason.setText(reason);

        // if a habit has been done this week then there shouldn't be a denote button for this habit
        if(status){
            denoteHabit.setVisibility(View.INVISIBLE);
        }

        // define behaviors of all four buttons
        denoteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewHabitActivity.this, DenoteHabitActivity.class);
                intent.putExtra("habit_title", habitTitle);
                intent.putExtra("last_time_denoted", lastTimeDenoted);
                startActivity(intent);
                finish();
            }
        });

        editHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewHabitActivity.this, AddHabitActivity.class);
                intent.putExtra("habit_title", habitTitle);
                intent.putExtra("date", date);
                intent.putExtra("days_of_week", daysOfWeek);
                intent.putExtra("reason", reason);
                intent.putExtra("last_time_denoted", lastTimeDenoted);
                intent.putExtra("request_code", 1);
                intent.putExtra("position", position);
                startActivity(intent);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadAnimation.startLoadingDialog();
                habitsReference
                        .document(habitTitle)
                        .delete();
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
}