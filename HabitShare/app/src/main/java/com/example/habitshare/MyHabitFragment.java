package com.example.habitshare;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class MyHabitFragment extends Fragment{
    // Global Variables
    private final String TAG = "MyHabits";
    View view;
    SwitchMaterial switchTodayAll;
    RecyclerView habitRecyclerView;
    ArrayList<Habit> habitDataList;
    CustomHabitListAdapter habitAdapter;
    ArrayList<Habit> todayHabitDataList;
    FirebaseFirestore db;
    LoadingDialog loadAnimation;
    boolean snapshotLock = false;
    ItemTouchHelper itemTouchHelper;

    // dialog views
    Button buttonConfirmReorder;
    Button buttonReorder;
    FloatingActionButton addHabitButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // find view by id
        view = inflater.inflate(R.layout.fragment_my_habits, container, false);
        addHabitButton = view.findViewById(R.id.button_add_habit);
        switchTodayAll = view.findViewById(R.id.switch_today_all);
        buttonConfirmReorder = view.findViewById(R.id.confirm_reorder);
        buttonReorder = view.findViewById(R.id.reorder);

        // initialize some variables
        buttonConfirmReorder.setVisibility(View.INVISIBLE);
        todayHabitDataList = new ArrayList<>();
        habitRecyclerView = view.findViewById(R.id.friend_habit_list);
        habitDataList = new ArrayList<>();
        habitAdapter = new CustomHabitListAdapter(getContext(), habitDataList);
        habitRecyclerView.setAdapter(habitAdapter);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();

        setCollectionReferenceAddSnapshotListener();

        // This is for the reordering feature
        itemTouchHelper = new ItemTouchHelper(simpleCallback);

        buttonReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when reordering the habits, it is not allow to view today's habit, so we need to hide some widgets from the user
                buttonReorder.setVisibility(View.INVISIBLE);
                switchTodayAll.setVisibility(View.INVISIBLE);
                addHabitButton.setVisibility(View.INVISIBLE);
                buttonConfirmReorder.setVisibility(View.VISIBLE);
                snapshotLock = true; // this prevents the SnapshotListener to update the habitArrayList too early
                itemTouchHelper.attachToRecyclerView(habitRecyclerView); // enable drag and drop
            }
        });

        buttonConfirmReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform the reordering
                reorder();
            }
        });

        switchTodayAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // switch between all habits and today's habits
                // use different data list for the adapter depending on which mode the user want
                if(isChecked){ //
                    buttonReorder.setVisibility(View.INVISIBLE);
                    habitAdapter = new CustomHabitListAdapter(getContext(), todayHabitDataList);
                    habitRecyclerView.setAdapter(habitAdapter);
                    habitAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Habit habit = todayHabitDataList.get(position);
                            viewHabit(habit);
                        }
                    });
                }
                else{
                    buttonReorder.setVisibility(View.VISIBLE);
                    // reset the adapter
                    habitAdapter = new CustomHabitListAdapter(getContext(), habitDataList);
                    habitRecyclerView.setAdapter(habitAdapter);
                    habitAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Habit habit = habitDataList.get(position);
                            viewHabit(habit);
                        }
                    });
                }
            }
        });

        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start AddHabitActivity
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                intent.putExtra("position", habitDataList.size());
                startActivity(intent);
            }
        });

        habitAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Habit habit = habitDataList.get(position);
                viewHabit(habit);
            }
        });

        return view;
    }

    /**
     * start the ViewHabitActivity
     * @param habit a habit that the user chooses to view the details
     */
    private void viewHabit(Habit habit){
        int position = habitDataList.indexOf(habit); // get the original position in the habitDataList instead of the position in the todayHabitList
        Intent intent = new Intent(getContext(), ViewHabitActivity.class);
        intent.putExtra("habit_title", habit.getTitle());
        intent.putExtra("date", habit.getDate());
        intent.putExtra("days_of_week", habit.getSelectDayOfWeek());
        intent.putExtra("reason", habit.getReason());
        intent.putExtra("status", habit.getStatus());
        intent.putExtra("last_time_denoted", habit.getLastTimeDenoted());
        intent.putExtra("position", position);
        intent.putExtra("number_time_actual_denoted", habit.getNumberOfTimesActuallyDenoted());
        startActivity(intent);
    }

    /**
     * Perform the reordering
     */
    private void reorder(){
        int j;
        HashMap<String, Object> data;
        loadAnimation = new LoadingDialog(getContext());
        loadAnimation.startLoadingDialog();
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");
        for(j = 0; j < habitDataList.size() - 1; j++){
            Habit habit = habitDataList.get(j);
            data = new HashMap<>();
            data.put("Position", j);
            collectionReference.document(habit.getTitle())
                    .update(data)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Fail to update the data on FireStore");
                        }
                    });
        }
        data = new HashMap<>();
        data.put("Position", j);
        // set the snapshotLock to false and update the last habit so that the SnapshotListener will capture this update and handle the update
        snapshotLock = false;
        Habit habit = habitDataList.get(j);
        collectionReference.document(habit.getTitle())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadAnimation.dismissLoadingDialog();
                        // Make some widgets to visible again and hide the confirm button
                        buttonReorder.setVisibility(View.VISIBLE);
                        switchTodayAll.setVisibility(View.VISIBLE);
                        addHabitButton.setVisibility(View.VISIBLE);
                        buttonConfirmReorder.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Fail to update the data on FireStore");
                    }
                });
        itemTouchHelper.attachToRecyclerView(null); // disable the drag and drop
    }

    /*
    The enables the drag and drop feature
    Learnt from the YouTube Video: https://www.youtube.com/watch?v=H9D_HoOeKWM
     */
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
            ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(habitDataList, fromPosition, toPosition);
            habitAdapter.notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // empty, no definition is needed here since we don't use the swipe feature
        }
    };

    /**
     * Change the data list when a change occurred in the cloud
     */
    private void setCollectionReferenceAddSnapshotListener(){
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if(!snapshotLock){
                    if (error!=null){
                        Log.d(TAG,"Error:"+error.getMessage());
                    }
                    else {
                        int listSize = 0;
                        HashMap<Integer, Habit> orderMap = new HashMap(); // a hashmap that tracks the ordering of the habits
                        habitDataList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc != null){
                                // get data from FireStore
                                String habitTitle = doc.getId();
                                String date = (String) doc.getData().get("Date of Start");
                                String daysOfWeek = (String) doc.getData().get("Days of Week");
                                String reason = (String) doc.getData().get("Reason");
                                String lastTimeDenoted = (String) doc.getData().get("Last Time Denoted");
                                boolean status = (boolean) doc.getData().get("Status");
                                boolean isDisclosed = (boolean) doc.getData().get("IsDisclosed");
                                Integer position = ((Long) doc.getData().get("Position")).intValue();
                                Integer numberOfTimesActuallyDenoted = ((Long) doc.getData().get("NumberOfTimeActuallyDenoted")).intValue();

                                // create a habit object according the data from FireStore and add it to the ArrayList
                                Habit habit = new Habit(habitTitle, date, reason, daysOfWeek);
                                habit.setNumberOfTimesActuallyDenoted(numberOfTimesActuallyDenoted);
                                habit.setStatus(status);
                                habit.setLastTimeDenoted(lastTimeDenoted);
                                if(isDisclosed){
                                    habit.setPublic();
                                }
                                else{
                                    habit.setPrivate();
                                }
                                try {
                                    habit.updateNumberOfTimesShouldDenoted();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                orderMap.put(position, habit);
                                listSize += 1;
                            }
                        }
                        for(int i = 0; i < listSize; i++){
                            if(orderMap.get(i) != null){
                                habitDataList.add(orderMap.get(i));
                            }
                        }
                        updateTodayHabitList();
                        habitAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                    }
                }
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

    /**
     * Make a toast (short)
     * @param str a toast message
     */
    private void showToast(String str){
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
}