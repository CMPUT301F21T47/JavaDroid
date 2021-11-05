package com.example.habitshare;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.example.habitshare.R;


public class MyHabitFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";


    // Global Variables
    View view;
    FloatingActionButton addHabitButton;
    SwitchMaterial switchTodayAll;
    private ListView habitList;
    private ArrayList<Habit> habitDataList;
    private ArrayAdapter<Habit> habitAdapter;
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
    boolean isViewTodayOnly = false;


    // dialog views
    TextView dateView;
    EditText enterHabitTitle;
    EditText enterReason;
    Button selectDate;
    Button confirm;
    Button cancelAddHabit;
    Button delete;
    Button cancelViewHabit;
    Button editHabit;
    Button denoteHabit;
    CheckBox monCheckBox;
    CheckBox tueCheckBox;
    CheckBox wedCheckBox;
    CheckBox thuCheckBox;
    CheckBox friCheckBox;
    CheckBox satCheckBox;
    CheckBox sunCheckBox;
    TextView viewHabitTitle;
    TextView viewDate;
    TextView viewDaysOfWeek;
    TextView viewReason;




//    public MyHabitFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param email Parameter 1.
//     * @return A new instance of fragment MyHabitsFragment.
//     */
//    public static MyHabitFragment newInstance(String email) {
//        MyHabitFragment fragment = new MyHabitFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, email);
//
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_habits, container, false);
        addHabitButton = view.findViewById(R.id.button_add_habit);
        switchTodayAll = view.findViewById(R.id.switch_today_all);
//        Log.d("MyHabitsFragment", habit.getTitle());
        todayHabitDataList = new ArrayList<>();
        habitList = view.findViewById(R.id.my_habit_list);
        habitDataList = new ArrayList<>();
//        Habit habit = new Habit("Study", "2021-09-01");
//        habitDataList.add(habit);
        habitAdapter = new CustomHabitListAdapter(getContext(), habitDataList);

        habitList.setAdapter(habitAdapter);

        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        Log.d(TAG, "email is"+ MainActivity.email);

        // switch between all habits and today's habits
        switchTodayAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // use different data list for the adapter depending on which mode the user want
                if(isChecked){
                    isViewTodayOnly = true;
                    updateTodayHabitList();
                    habitAdapter = new CustomHabitListAdapter(getContext(), todayHabitDataList);
                }
                else{
                    habitAdapter = new CustomHabitListAdapter(getContext(), habitDataList);
                }
                habitList.setAdapter(habitAdapter);
            }
        });

        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddHabitDialog();
            }
        });

        habitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 i = position;
                showViewHabitDetailDialog();
            }
        });

        return view;
    }

    /**
     * starts an add habit dialog
     */
    private void showAddHabitDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_habit_layout);
        enterHabitTitle = dialog.findViewById(R.id.enter_habit_title);
        enterReason = dialog.findViewById(R.id.enter_reason);
        selectDate = dialog.findViewById(R.id.button_select_date);
        confirm = dialog.findViewById(R.id.button_delete_view_habit);
        dateView = dialog.findViewById(R.id.view_date_view_habit);
        cancelAddHabit = dialog.findViewById(R.id.button_cancel_view_habit);
        monCheckBox = dialog.findViewById(R.id.checkBox_monday);
        tueCheckBox = dialog.findViewById(R.id.checkBox_tuesday);
        wedCheckBox = dialog.findViewById(R.id.checkBox_wednesday);
        thuCheckBox = dialog.findViewById(R.id.checkBox_thursday);
        friCheckBox = dialog.findViewById(R.id.checkBox_friday);
        satCheckBox = dialog.findViewById(R.id.checkBox_saturday);
        sunCheckBox = dialog.findViewById(R.id.checkBox_sunday);

        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        setDate();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkConfirmCondition = true;
                habitTitle = enterHabitTitle.getText().toString();
                reason = enterReason.getText().toString();
                Habit habit = new Habit(habitTitle, date);
                setSelectedDays(habit);
                daysOfWeek = habit.getSelectDayOfWeek();

                // Check constraints of user input
                if(habitTitle.equals("")){
                    enterHabitTitle.setError("Habit Title cannot be empty");
                    checkConfirmCondition = false;
                }
                if(date.equals("----/--/--")){
                    Toast.makeText(getContext(), "Must select date", LENGTH_SHORT).show();
                    checkConfirmCondition = false;
                }
                if(daysOfWeek.equals("")){
                    Toast.makeText(getContext(), "Must select a day", LENGTH_SHORT).show();
                    checkConfirmCondition = false;
                }

                if(reason.equals("")){
                    enterReason.setError("Reason cannot be empty");
                    checkConfirmCondition = false;
                }

                if(habitTitle.length() > 20){
                    enterHabitTitle.setError("Cannot longer than 20 characters");
                    checkConfirmCondition =false;
                }
                if(reason.length() > 30){
                    enterReason.setError("Cannot longer than 30 characters");
                }
                if(checkConfirmCondition){
                    HashMap<String, String> data = new HashMap<>();
                    data.put("Date of Start", date);
                    data.put("Days of Week", daysOfWeek);
                    data.put("Reason", reason);
                    data.put("Status", "Not Done");
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
                    dialog.dismiss(); // close dialog only if all inputs are valid
                }

            }
        });

        cancelAddHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void showViewHabitDetailDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.view_habit_detail_layout);

        viewHabitTitle = dialog.findViewById(R.id.view_habit_title);
        viewDate = dialog.findViewById(R.id.view_date_view_habit);
        viewDaysOfWeek = dialog.findViewById(R.id.view_days_of_week);
        viewReason = dialog.findViewById(R.id.view_reason);
        delete = dialog.findViewById(R.id.button_delete_view_habit);
        cancelViewHabit = dialog.findViewById(R.id.button_cancel_view_habit);
        editHabit = dialog.findViewById(R.id.button_edit_habit);
        denoteHabit = dialog.findViewById(R.id.button_denote_habit);

        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        // set texts according to the attributes of the selected habit.
        Habit habit = habitDataList.get(i);
        habitTitle = habit.getTitle();
        viewHabitTitle.setText(habit.getTitle());
        viewDate.setText(habit.getDate());
        viewDaysOfWeek.setText(habit.getSelectDayOfWeek());
        viewReason.setText(habit.getReason());

        // if a habit has been done this week then there shouldn't be a denote button for this habit
        if(habit.getStatus()){
            denoteHabit.setVisibility(View.INVISIBLE);
        }

        // define behaviors of all four buttons
        denoteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showAddHabitEventDialog();
            }
        });

        editHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showEditHabitDialog();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String habitTitle = habit.getTitle();
                collectionReference
                        .document(habitTitle)
                        .delete();
                dialog.dismiss();
            }
        });

        cancelViewHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * start an add habit event dialog, there will be a corresponding change in the HabitEventFragment
     */
    private void showAddHabitEventDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_habit_event_layout);

        TextView denoteHabitName = dialog.findViewById(R.id.denote_habit_name);
        EditText enterComment = dialog.findViewById(R.id.denote_habit_comment);
        Button confirmDenote = dialog.findViewById(R.id.denote_habit_confirm_button);
        Button cancelDenote = dialog.findViewById(R.id.denote_habit_cancel_button);

        final CollectionReference collectionReference1 = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
        final CollectionReference collectionReference2 = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        habit = habitDataList.get(i);

        // get current date to keep a record on the denote date
        Calendar cal = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(cal.getTime());

        denoteHabitName.setText(habit.getTitle());

        confirmDenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = enterComment.getText().toString();

                // Check user input constraint
                if(comment.length() > 20){
                    enterComment.setError("A comment cannot have more than 20 characters");
                }
                else{
                    final String habitTitle = habit.getTitle();
                    final String date = habit.getDate();
                    final String daysOfWeek = habit.getSelectDayOfWeek();
                    final String reason = habit.getReason();

                    // add a new habit event
                    HashMap<String, String> data = new HashMap<>();
                    data.put("Comment", comment);
                    data.put("Denote Date", currentDate);
                    collectionReference1
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

                    // set the habit status to Done
                    data = new HashMap<>();
                    data.put("Date of Start", date);
                    data.put("Days of Week", daysOfWeek);
                    data.put("Reason", reason);
                    data.put("Status", "Done");
                    collectionReference2.document(habitTitle)
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
                    habit.setStatus(true);
                    dialog.dismiss();
                }
            }
        });

        cancelDenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * start an edit habit dialog
     */
    private void showEditHabitDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_habit_layout);

        habit = habitDataList.get(i);
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        enterHabitTitle = dialog.findViewById(R.id.enter_habit_title);
        enterReason = dialog.findViewById(R.id.enter_reason);
        selectDate = dialog.findViewById(R.id.button_select_date);
        confirm = dialog.findViewById(R.id.button_delete_view_habit);
        dateView = dialog.findViewById(R.id.view_date_view_habit);
        cancelAddHabit = dialog.findViewById(R.id.button_cancel_view_habit);
        monCheckBox = dialog.findViewById(R.id.checkBox_monday);
        tueCheckBox = dialog.findViewById(R.id.checkBox_tuesday);
        wedCheckBox = dialog.findViewById(R.id.checkBox_wednesday);
        thuCheckBox = dialog.findViewById(R.id.checkBox_thursday);
        friCheckBox = dialog.findViewById(R.id.checkBox_friday);
        satCheckBox = dialog.findViewById(R.id.checkBox_saturday);
        sunCheckBox = dialog.findViewById(R.id.checkBox_sunday);

        habitTitle = habit.getTitle();
        date = habit.getDate();
        boolean[] daysOfWeekList = habit.getSelectDayOfWeekList();
        reason= habit.getReason();

        // set original details
        enterHabitTitle.setText(habitTitle);
        enterReason.setText(reason);
        dateView.setText(date);
        for(int j = 0; j < daysOfWeekList.length; j++){
            if (daysOfWeekList[j]) {
                switch (j) {
                    case 0:
                        monCheckBox.setChecked(true);
                        break;
                    case 1:
                        tueCheckBox.setChecked(true);
                        break;
                    case 2:
                        wedCheckBox.setChecked(true);
                        break;
                    case 3:
                        thuCheckBox.setChecked(true);
                        break;
                    case 4:
                        friCheckBox.setChecked(true);
                        break;
                    case 5:
                        satCheckBox.setChecked(true);
                        break;
                    case 6:
                        sunCheckBox.setChecked(true);
                }
            }
        }

        // set edited details
        setDate();
        setSelectedDays(habit);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkConfirmCondition = true;
                habitTitle = enterHabitTitle.getText().toString();
                reason = enterReason.getText().toString();
                Habit habit = habitDataList.get(i);
                setDate();
                setSelectedDays(habit);
                daysOfWeek = habit.getSelectDayOfWeek();
                if(habitTitle.equals("")){
                    enterHabitTitle.setError("Habit Title cannot be empty");
                    checkConfirmCondition = false;
                }
                if(daysOfWeek.equals("")){
                    Toast.makeText(getContext(), "Must select a day", LENGTH_SHORT).show();
                    checkConfirmCondition = false;
                }

                if(reason.equals("")){
                    enterReason.setError("Reason cannot be empty");
                    checkConfirmCondition = false;
                }

                if(habitTitle.length() > 20){
                    enterHabitTitle.setError("Cannot longer than 20 characters");
                    checkConfirmCondition =false;
                }
                if(reason.length() > 30){
                    enterReason.setError("Cannot longer than 30 characters");
                }
                if(checkConfirmCondition) {
                    collectionReference
                            .document(habitTitle)
                            .delete();
                    habitTitle = enterHabitTitle.getText().toString();
                    reason = enterReason.getText().toString();

                    HashMap<String, String> data = new HashMap<>();
                    data.put("Date of Start", date);
                    data.put("Reason", reason);
                    if(habit.getStatus()){
                        data.put("Status", "Done");
                    }
                    else{
                        data.put("Status", "Not Done");
                    }
                    habit = new Habit(habitTitle, reason);
                    setSelectedDays(habit);
                    daysOfWeek = habit.getSelectDayOfWeek();
                    data.put("Days of Week", daysOfWeek);

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
                    dialog.dismiss();
                }

            }
        });

        cancelAddHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * Change the data list when a change occurred in the cloud
     */
    private void setCollectionReferenceAddSnapshotListener(){
        Log.d(TAG, "email is" + MainActivity.email);
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                if (error!=null){
                    Log.d(TAG,"Error:"+error.getMessage());
                }
                else {
                    habitDataList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc != null){
                            Log.d(TAG, String.valueOf(doc.getData().get("Habits")));
                            String habitTitle = doc.getId();
                            String date = (String) doc.getData().get("Date of Start");
                            String daysOfWeek = (String) doc.getData().get("Days of Week");
                            String reason = (String) doc.getData().get("Reason");
                            String habitStatus = (String) doc.getData().get("Status");
                            boolean status;
//                            Log.d(TAG, "title is " + habitTitle);
//                            Log.d(TAG, "date is "  + date);
//                            Log.d(TAG, "days of week is " + daysOfWeek);
//                            Log.d(TAG, "reason is " + reason);

                            if(habitTitle != null && date != null &&  daysOfWeek != null && reason != null && habitStatus != null){
                                status = !habitStatus.equals("Not Done");
                                Habit habit = new Habit(habitTitle, date, reason, daysOfWeek);
                                habit.setStatus(status);
                                habitDataList.add(habit); // Adding the cities and provinces from FireStore
                            }

                        }

                    }
                    updateTodayHabitList();
                    habitAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                }

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
                        getContext(),
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
                ++month; // In android, month is from 0 to 11, so we need to add 1 to it.
                String month_str;
                String day_str;

                // Check if the month or day is less than two digits.
                // If it is one digit then we add 0 in front of it,
                // so that it always matches the format yyyy-mm-dd
                if(month < 10){
                    month_str = "0" + month;
                }
                else{
                    month_str = "" + month;
                }
                if(day < 10){
                    day_str = "0" + day;
                }
                else{
                    day_str = "" + day;
                }
                date = year +"-" + month_str + "-" + day_str;
                // date = year + "-" + month + "-" + day;
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
            habit.selectDayOfWeek(0);
        }
        if(tueCheckBox.isChecked()){
            habit.selectDayOfWeek(1);
        }
        if(wedCheckBox.isChecked()){
            habit.selectDayOfWeek(2);
        }
        if(thuCheckBox.isChecked()){
            habit.selectDayOfWeek(3);
        }
        if(friCheckBox.isChecked()){
            habit.selectDayOfWeek(4);
        }
        if(satCheckBox.isChecked()){
            habit.selectDayOfWeek(5);
        }
        if(sunCheckBox.isChecked()){
            habit.selectDayOfWeek(6);
        }
    }

    /**
     * This will update today's habit list.
     * It simply filters out all the habit that don't have to be done in this day of week.
     */
    private void updateTodayHabitList(){
        todayHabitDataList.clear();
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "today is " + day);
        for(int i = 0; i < habitDataList.size(); i++){
            Habit habit = habitDataList.get(i);
            if(habit.getSelectDayOfWeekList()[day-2] && !habit.getStatus()){
                todayHabitDataList.add(habit);
            }
        }
    }

}