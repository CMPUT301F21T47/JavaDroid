package com.example.habitshare;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.habitshare.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    public static String email; // This is the unique key to access user's data
    public static String userName;
    public static Uri imageURI;
    private static final int REQUEST_LOGIN = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    FirebaseFirestore db;
    final static String TAG = "MainActivity";
    DocumentSnapshot userDocument;
    boolean firstTimeLogin = true;
    LoadingDialog loadAnimation;
    boolean loginStatus = false;

    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    int lastTimeStarted = settings.getInt("last_time_started", -1);
    Calendar calendar = Calendar.getInstance();
    int today = calendar.get(Calendar.DAY_OF_YEAR);
    boolean toSchedule = true;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((toSchedule) && (today != lastTimeStarted)){

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.commit();

            scheduleJob();

        }

        loadAnimation = new LoadingDialog(MainActivity.this);
        startLogin();

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob(){
        ComponentName componentName = new ComponentName(this, ScheduledJobService.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(1000 * 60 * 60 * 24)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);

        if (resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "Job Scheduled");
        } else {
            Log.d(TAG, "Job Scheduling Failed");
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob(){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(1);
        Log.d(TAG, "Job Cancelled");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * launch the LoginActivity
     * A simple function to control when to start the LoginActivity
     */
    private void startLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && data != null) {
            if (resultCode == RESULT_OK) {
                firstTimeLogin = false;
                loadAnimation.startLoadingDialog();
                Log.d(TAG, "Return succeed");
                email = data.getStringExtra("email_from_login");
                Log.d(TAG, "Email fetched " + email);
                db = FirebaseFirestore.getInstance();
                final CollectionReference collectionReference = db.collection("UserData");
                collectionReference.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            userDocument = task.getResult();
                            assert userDocument != null;
                            if (userDocument.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                                userName = userDocument.getString("UserName");
                                Log.d(TAG, "Username in onComplete is " + userName);
                                firstTimeLogin = false;
                                loginStatus = true;
                                inflateNavDrawer();
                            } else {
                                Log.d(TAG, "No such document");

                            }
                        } else {
                            Log.d(TAG, "Get failed with ", task.getException());
                        }
                    }
                });


            }
            loadAnimation.dismissLoadingDialog();
        }

        if(resultCode == RESULT_CANCELED && firstTimeLogin){
            // If it is the first time login, then close the app when the user press the "return" button
            // This prevents the user from skipping the login step, and directly go to the MainActivity
            finish();
        }
    }


    /**
     * This is navigation drawer inflater.
     * The code is from the android studio built-in navigation drawer activity.
     * This function helps to control when t0 inflate, since the default code would inflate before
     * LoginActivity which is undesired.
     */
    void inflateNavDrawer(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_my_habits, R.id.nav_habit_events)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.view_user_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.view_user_email);
        navUsername.setText(userName);
        navEmail.setText(email);
    }
}

// This is the original code from the navigation drawer activity template
//public class MainActivity extends AppCompatActivity {
//
//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityMainBinding binding;
//    public static String email = "tianxia3@ualberta.ca";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.appBarMain.toolbar);
////        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////            }
////        });
//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_my_habits, R.id.nav_habit_events)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
//}