package com.example.habitshare;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class MyHabitFragment extends Fragment{
    // Global Variables
    View view;
    SwitchMaterial switchTodayAll;
    private RecyclerView habitRecyclerView;
    private static ArrayList<Habit> habitDataList;
    private CustomHabitListAdapter habitAdapter;
    private ArrayList<Habit> todayHabitDataList;
    int i;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String date;
    String habitTitle;
    String reason;
    String daysOfWeek = "";
    String currentDate;
    String habitStatus;
    Habit habit;
    FirebaseFirestore db;
    private final String TAG = "MyHabits";
    boolean checkConfirmCondition;
    boolean checkSelectImage;
    Uri imageURI;
    Bitmap bitmap;
    LoadingDialog loadAnimation;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    boolean snapshotLock = false;
    boolean flag;

    // dialog views
    Button buttonConfirmReorder;
    Button buttonReorder;
    FloatingActionButton addHabitButton;
    TextView switchDescription;
    HashMap<String, Object> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * To keep a tally of each time a habit is done or not when the day rolls over,
         * so it can display to the user how closely they've been following their habits
         */
        /*
        //A potential alternative if the job scheduler doesn't work as intended
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        SharedPreferences settings = getActivity().getSharedPreferences("PREFS", 0);
        int lastDay = settings.getInt("day", 0);

        if (lastDay == currentDay){ // Is it a new day?
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day", currentDay);
            editor.commit();

            for(int i = 0; i < habitDataList.size(); i++){
                Habit currentHabit = habitDataList.get(i);

                if (currentHabit.getIsDenoted()){
                    currentHabit.addTimesDone();
                } else {
                    currentHabit.addTimesNotDone();
                }

                currentHabit.setIsDenoted(false);

            }//end for
        }//end if
         */

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_habits, container, false);
        addHabitButton = view.findViewById(R.id.button_add_habit);
        switchTodayAll = view.findViewById(R.id.switch_today_all);
        buttonConfirmReorder = view.findViewById(R.id.confirm_reorder);
        buttonReorder = view.findViewById(R.id.reorder);
        switchDescription = view.findViewById(R.id.switch_description);

        buttonConfirmReorder.setVisibility(View.INVISIBLE);
//        Log.d("MyHabitsFragment", habit.getTitle());
        todayHabitDataList = new ArrayList<>();
        habitRecyclerView = view.findViewById(R.id.friend_habit_list);
        habitDataList = new ArrayList<>();
//        Habit habit = new Habit("Study", "2021-09-01");
//        habitDataList.add(habit);
        habitAdapter = new CustomHabitListAdapter(getContext(), habitDataList);

        habitRecyclerView.setAdapter(habitAdapter);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

        buttonReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonReorder.setVisibility(View.INVISIBLE);
                switchTodayAll.setVisibility(View.INVISIBLE);
                switchDescription.setVisibility(View.INVISIBLE);
                addHabitButton.setVisibility(View.INVISIBLE);
                buttonConfirmReorder.setVisibility(View.VISIBLE);
                snapshotLock = true;

                itemTouchHelper.attachToRecyclerView(habitRecyclerView);
            }
        });

        buttonConfirmReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    int finalJ = j;
                    Log.d(TAG, "Data size is "+ habitDataList.size());
                    Log.d(TAG, "J is " + j);
                    collectionReference.document(habit.getTitle())
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
                }
                data = new HashMap<>();
                data.put("Position", j);
                snapshotLock = false;
                Habit habit = habitDataList.get(j);
                collectionReference.document(habit.getTitle())
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loadAnimation.dismissLoadingDialog();
                                buttonReorder.setVisibility(View.VISIBLE);
                                switchTodayAll.setVisibility(View.VISIBLE);
                                switchDescription.setVisibility(View.VISIBLE);
                                addHabitButton.setVisibility(View.VISIBLE);
                                buttonConfirmReorder.setVisibility(View.INVISIBLE);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                itemTouchHelper.attachToRecyclerView(null);
            }
        });

        // switch between all habits and today's habits
        switchTodayAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // use different data list for the adapter depending on which mode the user want
                if(isChecked){
                    buttonReorder.setVisibility(View.INVISIBLE);
                    habitAdapter = new CustomHabitListAdapter(getContext(), todayHabitDataList);
                    habitRecyclerView.setAdapter(habitAdapter);
                    habitAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            i = position;
                            Habit habit = todayHabitDataList.get(i);
                            position = habitDataList.indexOf(habit);
                            Log.d(TAG, "Habit index is " + position);
                            Intent intent = new Intent(getContext(), ViewHabitActivity.class);
                            intent.putExtra("habit_title", habit.getTitle());
                            intent.putExtra("date", habit.getDate());
                            intent.putExtra("days_of_week", habit.getSelectDayOfWeek());
                            intent.putExtra("reason", habit.getReason());
                            intent.putExtra("status", habit.getStatus());
                            intent.putExtra("last_time_denoted", habit.getLastTimeDenoted());
                            intent.putExtra("position", position);
                            startActivity(intent);

                        }
                    });
                }
                else{
                    buttonReorder.setVisibility(View.VISIBLE);
                        habitAdapter = new CustomHabitListAdapter(getContext(), habitDataList);
                        habitRecyclerView.setAdapter(habitAdapter);
                        habitAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                i = position;
                                Log.d(TAG, "Position is" + position);
                                Habit habit = habitDataList.get(i);
                                Intent intent = new Intent(getContext(), ViewHabitActivity.class);
                                intent.putExtra("habit_title", habit.getTitle());
                                intent.putExtra("date", habit.getDate());
                                intent.putExtra("days_of_week", habit.getSelectDayOfWeek());
                                intent.putExtra("reason", habit.getReason());
                                intent.putExtra("status", habit.getStatus());
                                intent.putExtra("last_time_denoted", habit.getLastTimeDenoted());
                                intent.putExtra("position", position);
                                startActivity(intent);
                            }
                        });
                }

        }
    });


        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                intent.putExtra("position", habitDataList.size());
                startActivity(intent);
            }
        });

        habitAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                i = position;
                Log.d(TAG, "Position is" + position);
                Habit habit = habitDataList.get(i);
                Intent intent = new Intent(getContext(), ViewHabitActivity.class);
                intent.putExtra("habit_title", habit.getTitle());
                intent.putExtra("date", habit.getDate());
                intent.putExtra("days_of_week", habit.getSelectDayOfWeek());
                intent.putExtra("reason", habit.getReason());
                intent.putExtra("status", habit.getStatus());
                intent.putExtra("last_time_denoted", habit.getLastTimeDenoted());
                intent.putExtra("position", position);
                intent.putExtra("is_disclosed", habit.getIsDisclosed());
                startActivity(intent);
            }
        });

        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
            ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Log.d(TAG, "fromPosition is " + fromPosition);
            Log.d(TAG, "toPosition is " + toPosition);

            Collections.swap(habitDataList, fromPosition, toPosition);
            habitAdapter.notifyItemMoved(fromPosition, toPosition);

            final CollectionReference collectionReference = db.collection("UserData")
                    .document(MainActivity.email)
                    .collection("Habits");

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    /**
     * Change the data list when a change occurred in the cloud
     */
    private void setCollectionReferenceAddSnapshotListener(){
        //Log.d(TAG, "email is" + MainActivity.email);
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
                        HashMap<Integer, Habit> orderMap = new HashMap();
                        habitDataList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc != null){
                                //Log.d(TAG, String.valueOf(doc.getData().get("Habits")));
                                String habitTitle = doc.getId();
                                String date = (String) doc.getData().get("Date of Start");
                                String daysOfWeek = (String) doc.getData().get("Days of Week");
                                String reason = (String) doc.getData().get("Reason");
                                String lastTimeDenoted = (String) doc.getData().get("Last Time Denoted");
                                boolean status = (boolean) doc.getData().get("Status");
                                boolean isDisclosed = (boolean) doc.getData().get("IsDisclosed");
                                Integer position = ((Long) doc.getData().get("Position")).intValue();
                                //Log.d(TAG, "title is " + habitTitle);
//                            Log.d(TAG, "date is "  + date);
//                            Log.d(TAG, "days of week is " + daysOfWeek);
//                            Log.d(TAG, "reason is " + reason);
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

    public static ArrayList<Habit> getHabitDataList() {
        return habitDataList;
    }

    //    /**
//     * starts an add habit dialog
//     */
//    private void showAddHabitDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.activity_show_add_habit);
//        enterHabitTitle = dialog.findViewById(R.id.enter_habit_title);
//        enterReason = dialog.findViewById(R.id.enter_reason);
//        selectDate = dialog.findViewById(R.id.button_select_date);
//        confirm = dialog.findViewById(R.id.button_delete_view_habit);
//        dateView = dialog.findViewById(R.id.view_date_view_habit);
//        cancelAddHabit = dialog.findViewById(R.id.button_cancel_view_habit);
//        monCheckBox = dialog.findViewById(R.id.checkBox_monday);
//        tueCheckBox = dialog.findViewById(R.id.checkBox_tuesday);
//        wedCheckBox = dialog.findViewById(R.id.checkBox_wednesday);
//        thuCheckBox = dialog.findViewById(R.id.checkBox_thursday);
//        friCheckBox = dialog.findViewById(R.id.checkBox_friday);
//        satCheckBox = dialog.findViewById(R.id.checkBox_saturday);
//        sunCheckBox = dialog.findViewById(R.id.checkBox_sunday);
//
//        final CollectionReference collectionReference = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habits");
//
//        setDate();
//
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               checkConfirmCondition = true;
//               habitTitle = enterHabitTitle.getText().toString();
//               reason = enterReason.getText().toString();
//               Habit habit = new Habit(habitTitle, date);
//               setSelectedDays(habit);
//               daysOfWeek = habit.getSelectDayOfWeek();
//
//               // Check constraints of user input
//               collectionReference.document(habitTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                   @Override
//                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                       DocumentSnapshot habitDocument = task.getResult();
//                       if (task.isSuccessful()) {
//                           assert habitDocument != null;
//                           // check the existence of the habit
//                           if (habitDocument.exists()) {
//                               enterHabitTitle.setError("This habit has already existed, please enter another one");
//                           }
//                           else {
//                               // check other constraints
//                               if (habitTitle.equals("")) {
//                                   enterHabitTitle.setError("Habit Title cannot be empty");
//                                   checkConfirmCondition = false;
//                               }
//                               if (date.equals("----/--/--")) {
//                                   Toast.makeText(getContext(), "Must select date", LENGTH_SHORT).show();
//                                   checkConfirmCondition = false;
//                               }
//                               if (daysOfWeek.equals("")) {
//                                   Toast.makeText(getContext(), "Must select a day", LENGTH_SHORT).show();
//                                   checkConfirmCondition = false;
//                               }
//
//                               if (reason.equals("")) {
//                                   enterReason.setError("Reason cannot be empty");
//                                   checkConfirmCondition = false;
//                               }
//
//                               if (habitTitle.length() > 20) {
//                                   enterHabitTitle.setError("Cannot longer than 20 characters");
//                                   checkConfirmCondition = false;
//                               }
//                               if (reason.length() > 30) {
//                                   enterReason.setError("Cannot longer than 30 characters");
//                               }
//
//                               if (checkConfirmCondition) {
//                                   HashMap<String, String> data = new HashMap<>();
//                                   data.put("Date of Start", date);
//                                   data.put("Days of Week", daysOfWeek);
//                                   data.put("Reason", reason);
//                                   data.put("Status", "Not Done");
//                                   data.put("Last Time Denoted", "");
//                                   collectionReference
//                                           .document(habitTitle)
//                                           .set(data)
//                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                               @Override
//                                               public void onSuccess(Void unused) {
//                                                   Log.d(TAG, "Data has been added successfully!");
//                                               }
//                                           })
//                                           .addOnFailureListener(new OnFailureListener() {
//                                               @Override
//                                               public void onFailure(@NonNull Exception e) {
//                                                   Log.d(TAG, "Data could not be added!" + e.toString());
//                                               }
//                                           });
//                                   dialog.dismiss(); // close dialog only if all inputs are valid
//                               }
//                           }
//                       }
//                   }
//               });
//            }
//        });
//
//
//        cancelAddHabit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

//    void showViewHabitDetailDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.view_habit_detail_layout);
//
//        viewHabitTitle = dialog.findViewById(R.id.view_habit_title);
//        viewDate = dialog.findViewById(R.id.view_date_view_habit);
//        viewDaysOfWeek = dialog.findViewById(R.id.view_days_of_week);
//        viewReason = dialog.findViewById(R.id.view_reason);
//        delete = dialog.findViewById(R.id.button_delete_view_habit);
//        cancelViewHabit = dialog.findViewById(R.id.button_cancel_view_habit);
//        editHabit = dialog.findViewById(R.id.button_edit_habit);
//        denoteHabit = dialog.findViewById(R.id.button_denote_habit);
//
//        final CollectionReference collectionReference = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habits");
//
//        // set texts according to the attributes of the selected habit.
//        Habit habit = habitDataList.get(i);
//
//
//        // if a habit has been done this week then there shouldn't be a denote button for this habit
//        if(habit.getStatus()){
//            denoteHabit.setVisibility(View.INVISIBLE);
//        }
//
//        // define behaviors of all four buttons
//        denoteHabit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                showAddHabitEventDialog();
//            }
//        });
//
//        editHabit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                Intent intent = new Intent(getContext(), AddHabitActivity.class);
//                intent.putExtra("habit_title", habit.getTitle());
//                intent.putExtra("date", habit.getDate());
//                intent.putExtra("days_of_week", habit.getSelectDayOfWeek());
//                intent.putExtra("reason", habit.getReason());
//                intent.putExtra("request_code", 1);
//                startActivity(intent);
//            }
//        });
//
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String habitTitle = habit.getTitle();
//                collectionReference
//                        .document(habitTitle)
//                        .delete();
//                dialog.dismiss();
//            }
//        });
//
//        cancelViewHabit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//
//        dialog.show();
//    }

//    /**
//     * start an add habit event dialog, there will be a corresponding change in the HabitEventFragment
//     */
//    private void showAddHabitEventDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.add_habit_event_layout);
//
//        TextView denoteHabitName = dialog.findViewById(R.id.denote_habit_name);
//        EditText enterComment = dialog.findViewById(R.id.denote_habit_comment);
//        Button confirmDenote = dialog.findViewById(R.id.denote_habit_confirm_button);
//        Button cancelDenote = dialog.findViewById(R.id.denote_habit_cancel_button);
//        habitEventImage = dialog.findViewById(R.id.habit_event_image);
//
//        final CollectionReference collectionReference1 = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habit Events");
//        final CollectionReference collectionReference2 = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habits");
//
//        checkSelectImage = false;
//        habit = habitDataList.get(i);
//
//        // get current date to keep a record on the denote date
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        currentDate = dateFormat.format(cal.getTime());
//
//        habit.setLastTimeDenoted(currentDate);
//        denoteHabitName.setText(habit.getTitle());
//
//        habitEventImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImageDialog();
//            }
//        });
//
//        confirmDenote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String comment = enterComment.getText().toString();
//                // Check user input constraint
//                if(comment.length() > 20){
//                    enterComment.setError("A comment cannot have more than 20 characters");
//                }
//                else{
//                    final String habitTitle = habit.getTitle();
//                    final String date = habit.getDate();
//                    final String daysOfWeek = habit.getSelectDayOfWeek();
//                    final String reason = habit.getReason();
//                    final String lastTimeDenoted = habit.getLastTimeDenoted();
//
//                    // add a new habit event
//                    HashMap<String, String> data = new HashMap<>();
//                    data.put("Comment", comment);
//                    data.put("Denote Date", currentDate);
//                    if(checkSelectImage){
//                        String fileName = habitTitle + "_" + currentDate;
//                        data.put("Image File Name", fileName);
//                        StorageReference imageRef = storageReference.child("images/" + fileName);
//                        loadAnimation = new LoadingDialog(getContext());
//                        loadAnimation.startLoadingDialog();
//                        imageRef.putFile(imageURI)
//                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                        loadAnimation.dismissLoadingDialog();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        loadAnimation.dismissLoadingDialog();
//                                    }
//                                });
//                    }
//                    else{
//                        data.put("Image File Name", "");
//                    }
//                    collectionReference1
//                            .document(habitTitle)
//                            .set(data)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d(TAG, "Data has been added successfully!");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "Data could not be added!" + e.toString());
//                                }
//                            });
//
//                    // set the habit status to Done
//                    data = new HashMap<>();
//                    data.put("Date of Start", date);
//                    data.put("Days of Week", daysOfWeek);
//                    data.put("Reason", reason);
//                    data.put("Status", "Done");
//                    data.put("Last Time Denoted", lastTimeDenoted);
//                    collectionReference2.document(habitTitle)
//                            .set(data)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d(TAG, "Data has been added successfully!");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "Data could not be added!" + e.toString());
//                                }
//                            });
//                    habit.setStatus(true);
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        cancelDenote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }


//    /**
//     * start an edit habit dialog
//     */
//    private void showEditHabitDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.activity_show_add_habit);
//
//        habit = habitDataList.get(i);
//        final CollectionReference collectionReference = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habits");
//
//        enterHabitTitle = dialog.findViewById(R.id.enter_habit_title);
//        enterReason = dialog.findViewById(R.id.enter_reason);
//        selectDate = dialog.findViewById(R.id.button_select_date);
//        confirm = dialog.findViewById(R.id.button_delete_view_habit);
//        dateView = dialog.findViewById(R.id.view_date_view_habit);
//        cancelAddHabit = dialog.findViewById(R.id.button_cancel_view_habit);
//        monCheckBox = dialog.findViewById(R.id.checkBox_monday);
//        tueCheckBox = dialog.findViewById(R.id.checkBox_tuesday);
//        wedCheckBox = dialog.findViewById(R.id.checkBox_wednesday);
//        thuCheckBox = dialog.findViewById(R.id.checkBox_thursday);
//        friCheckBox = dialog.findViewById(R.id.checkBox_friday);
//        satCheckBox = dialog.findViewById(R.id.checkBox_saturday);
//        sunCheckBox = dialog.findViewById(R.id.checkBox_sunday);
//
//        habitTitle = habit.getTitle();
//        date = habit.getDate();
//        boolean[] daysOfWeekList = habit.getSelectDayOfWeekList();
//        reason= habit.getReason();
//
//        // set original details
//        enterHabitTitle.setText(habitTitle);
//        enterReason.setText(reason);
//        dateView.setText(date);
//        for(int j = 0; j < daysOfWeekList.length; j++){
//            if (daysOfWeekList[j]) {
//                switch (j) {
//                    case 1:
//                        monCheckBox.setChecked(true);
//                        break;
//                    case 2:
//                        tueCheckBox.setChecked(true);
//                        break;
//                    case 3:
//                        wedCheckBox.setChecked(true);
//                        break;
//                    case 4:
//                        thuCheckBox.setChecked(true);
//                        break;
//                    case 5:
//                        friCheckBox.setChecked(true);
//                        break;
//                    case 6:
//                        satCheckBox.setChecked(true);
//                        break;
//                    case 0:
//                        sunCheckBox.setChecked(true);
//                }
//            }
//        }
//
//        // set edited details
//        setDate();
//        setSelectedDays(habit);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean checkConfirmCondition = true;
//                habitTitle = enterHabitTitle.getText().toString();
//                reason = enterReason.getText().toString();
//                Habit habit = habitDataList.get(i);
//                daysOfWeek = habit.getSelectDayOfWeek();
//                if(habitTitle.equals("")){
//                    enterHabitTitle.setError("Habit Title cannot be empty");
//                    checkConfirmCondition = false;
//                }
//                if(daysOfWeek.equals("")){
//                    Toast.makeText(getContext(), "Must select a day", LENGTH_SHORT).show();
//                    checkConfirmCondition = false;
//                }
//
//                if(reason.equals("")){
//                    enterReason.setError("Reason cannot be empty");
//                    checkConfirmCondition = false;
//                }
//
//                if(habitTitle.length() > 20){
//                    enterHabitTitle.setError("Cannot longer than 20 characters");
//                    checkConfirmCondition =false;
//                }
//                if(reason.length() > 30){
//                    enterReason.setError("Cannot longer than 30 characters");
//                }
//                if(checkConfirmCondition) {
//                    collectionReference
//                            .document(habitTitle)
//                            .delete();
//                    habitTitle = enterHabitTitle.getText().toString();
//                    reason = enterReason.getText().toString();
//
//                    HashMap<String, String> data = new HashMap<>();
//                    data.put("Date of Start", date);
//                    data.put("Reason", reason);
//                    if(habit.getStatus()){
//                        data.put("Status", "Done");
//                    }
//                    else{
//                        data.put("Status", "Not Done");
//                    }
//                    habit = new Habit(habitTitle, reason);
//                    setSelectedDays(habit);
//                    daysOfWeek = habit.getSelectDayOfWeek();
//                    data.put("Days of Week", daysOfWeek);
//
//                    collectionReference
//                            .document(habitTitle)
//                            .set(data)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d(TAG, "Data has been added successfully!");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "Data could not be added!" + e.toString());
//                                }
//                            });
//                    dialog.dismiss();
//                }
//
//            }
//        });
//
//        cancelAddHabit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == Activity.RESULT_OK){
//            if((requestCode == 2 || requestCode == 4)&& data != null){
//                Log.d(TAG, "Reached here");
//                imageURI = data.getData();
//                Log.d(TAG, "Image URI " + (imageURI != null));
//                habitEventImage.setImageURI(imageURI);
//                checkSelectImage = true;
//            }
////            if(requestCode == 4 && data != null){
////                Bundle bundle = data.getExtras();
////                bundle.get("data");
////                bitmap = (bitmap)
////            }
//        }
//    }

//
//    /**
//     * Allows an activity or fragment to use the android built-in date picker
//     */
//    private void setDate(){
//        /* This function will implement selecting a date from the android built-in DatePickerDialog.
//         *  The approach is adapted from https://www.youtube.com/watch?v=hwe1abDO2Ag */
//
//        selectDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar cal = Calendar.getInstance();
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dialog = new DatePickerDialog(
//                        getContext(),
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                        dateSetListener,
//                        year, month, day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//
//            }
//        });
//
//        dateSetListener = new DatePickerDialog.OnDateSetListener(){
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                ++month; // In android, month is from 0 to 11, so we need to add 1 to it.
//                String month_str;
//                String day_str;
//
//                // Check if the month or day is less than two digits.
//                // If it is one digit then we add 0 in front of it,
//                // so that it always matches the format yyyy-mm-dd
//                if(month < 10){
//                    month_str = "0" + month;
//                }
//                else{
//                    month_str = "" + month;
//                }
//                if(day < 10){
//                    day_str = "0" + day;
//                }
//                else{
//                    day_str = "" + day;
//                }
//                date = year +"-" + month_str + "-" + day_str;
//                dateView.setText(date);
//
//            }
//        };
//    }
//
//    /**
//     * Set selected days of a habit from checkboxes
//     * @param habit a class the represents a user's habit
//     */
//    private void setSelectedDays(Habit habit){
//        if(monCheckBox.isChecked()){
//            habit.selectDayOfWeek(1);
//        }
//        if(tueCheckBox.isChecked()){
//            habit.selectDayOfWeek(2);
//        }
//        if(wedCheckBox.isChecked()){
//            habit.selectDayOfWeek(3);
//        }
//        if(thuCheckBox.isChecked()){
//            habit.selectDayOfWeek(4);
//        }
//        if(friCheckBox.isChecked()){
//            habit.selectDayOfWeek(5);
//        }
//        if(satCheckBox.isChecked()){
//            habit.selectDayOfWeek(6);
//        }
//        if(sunCheckBox.isChecked()){
//            habit.selectDayOfWeek(0);
//        }
//    }



//    private void selectImageDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.select_image_source);
//
//        ImageView gallery = dialog.findViewById(R.id.imageview_gallery);
//        ImageView camera = dialog.findViewById(R.id.imageview_camera);
//
//        gallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, 2);
//            }
//        });
//
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
//                    startActivityForResult(intent,4 );
//                }
//            }
//        });
//
//    }


}