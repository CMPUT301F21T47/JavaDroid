package com.example.habitshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FriendHabitsActivity extends AppCompatActivity {
    // View Variables
    SwitchMaterial switchTodayAll;
    private RecyclerView habitRecyclerView;
    private ArrayList<Habit> habitDataList;
    private CustomHabitListAdapter habitAdapter;
    private ArrayList<Habit> todayHabitDataList;

    // Data Variables
    FirebaseFirestore db;
    String friendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_habits);
        // get data from intent
        friendEmail = getIntent().getStringExtra("friend_email");

        // set views
        habitRecyclerView = findViewById(R.id.friend_habit_list);
        switchTodayAll = findViewById(R.id.switch_today_all);

        // initialize
        habitDataList = new ArrayList<>();
        todayHabitDataList = new ArrayList<>();
        habitAdapter = new CustomHabitListAdapter(FriendHabitsActivity.this, habitDataList);
        habitRecyclerView.setAdapter(habitAdapter);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(FriendHabitsActivity.this));
        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        habitAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(FriendHabitsActivity.this, ViewHabitActivity.class);
                Habit habit = habitDataList.get(position);
                intent.putExtra("habit_title", habit.getTitle());
                intent.putExtra("date", habit.getDate());
                intent.putExtra("days_of_week", habit.getSelectDayOfWeek());
                intent.putExtra("reason", habit.getReason());
                intent.putExtra("status", habit.getStatus());
                intent.putExtra("last_time_denoted", habit.getLastTimeDenoted());
                intent.putExtra("position", position);
                intent.putExtra("is_disclosed", habit.getIsDisclosed());
                intent.putExtra("control_code", 1);
                startActivity(intent);
            }
        });
    }

    /**
     * Change the data list when a change occurred in the cloud
     */
    private void setCollectionReferenceAddSnapshotListener(){
        //Log.d(TAG, "email is" + MainActivity.email);
        final CollectionReference collectionReference = db.collection("UserData")
                .document(friendEmail)
                .collection("Habits");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                HashMap<Integer, Habit> orderMap = new HashMap();
                habitDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc != null){
                        boolean isDisclosed = (boolean) doc.getData().get("IsDisclosed");
                        if(isDisclosed){
                            String habitTitle = doc.getId();
                            String date = (String) doc.getData().get("Date of Start");
                            String daysOfWeek = (String) doc.getData().get("Days of Week");
                            String reason = (String) doc.getData().get("Reason");
                            String lastTimeDenoted = (String) doc.getData().get("Last Time Denoted");
                            boolean status = (boolean) doc.getData().get("Status");
                            Integer position = ((Long) doc.getData().get("Position")).intValue();
                            Habit habit = new Habit(habitTitle, date, reason, daysOfWeek);
                            habit.setStatus(status);
                            habit.setLastTimeDenoted(lastTimeDenoted);
                            if(isDisclosed){
                                habit.setPublic();
                            }
                            else{
                                habit.setPrivate();
                            }
                            orderMap.put(position, habit);
                            habitDataList.add(habit);
                        }
                    }
                }
                int listSize = habitDataList.size();
                habitDataList.clear();
                for(int i = 0; i < listSize; i++){
                    if(orderMap.get(i) != null){
                        habitDataList.add(orderMap.get(i));
                    }
                }
                updateTodayHabitList();
                habitAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        });
    }

    /**
     * This will update today's habit list.
     * It simply filters out all the habit that don't have to be done in this day of week.
     */
    private void updateTodayHabitList(){
        todayHabitDataList.clear();
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        //Log.d(TAG, "today is " + day);
        for(int i = 0; i < habitDataList.size(); i++){
            Habit habit = habitDataList.get(i);
            if(habit.getSelectDayOfWeekList()[day-1] && !habit.getStatus()){
                todayHabitDataList.add(habit);
            }
        }
    }
}