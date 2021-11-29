package com.example.habitshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
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


public class MainActivity extends AppCompatActivity {

    public static String email; // This is the unique key to access user's data
    public static String userName;
    private final static String TAG = "MainActivity";
    private static final int REQUEST_LOGIN = 1;

    FirebaseFirestore db;
    DocumentSnapshot userDocument;
    boolean firstTimeLogin = true;
    LoadingDialog loadAnimation;
    boolean loginStatus = false;

    // NavDrawer related variables
    AppBarConfiguration mAppBarConfiguration;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAnimation = new LoadingDialog(MainActivity.this);
        startLogin();
    }

    /**
     * The method is from the android studio built-in navigation drawer activity.
     * It inflates the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * The method is from the android studio built-in navigation drawer activity.
     * It allows the user to navigate through different fragments
     */
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
                R.id.nav_my_habits, R.id.nav_habit_events, R.id.nav_friends, R.id.nav_friend_requests)
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

    /**
     * Android built-in method, corresponds to the startActivityForResult() method
     * It handles the result coming from another activity.
     * Different handler will handler different activities' results according to the requestCode
     * @param requestCode an identification for a type of activity call
     * @param resultCode tells the handler if the result is ok or not
     * @param data the data passed from another activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_LOGIN: {
                if(resultCode == RESULT_OK && data != null){
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
                                    inflateNavDrawer(); // inflate the NavDrawer after login to avoid app crushes
                                    loadAnimation.dismissLoadingDialog();
                                } else {
                                    Log.d(TAG, "No such document");
                                    loadAnimation.dismissLoadingDialog();
                                }
                            } else {
                                Log.d(TAG, "Get failed with ", task.getException());
                                loadAnimation.dismissLoadingDialog();
                            }
                        }
                    });
                    break;
                }
            }
        }
        if(resultCode == RESULT_CANCELED && firstTimeLogin){
            // If it is the first time login, then close the app when the user press the "return" button
            // This prevents the user from skipping the login step, and directly go to the MainActivity
            finish();
        }
    }
}