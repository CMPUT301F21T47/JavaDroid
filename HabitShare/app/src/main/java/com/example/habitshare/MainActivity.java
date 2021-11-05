package com.example.habitshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitshare.databinding.ActivityMainBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.habitshare.R;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static String email = "tianxia3@ualberta.ca";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_my_habits, R.id.nav_habit_events)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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
}

//public class MainActivity extends AppCompatActivity {
//
//    public static String email = "tianxia3@ualberta.ca";
//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityMainBinding binding;
//    String userName;
//    FirebaseFirestore db;
//    final static String TAG = "MainActivity";
//    TextView displayUserName;
//    DocumentSnapshot userDocument;
//    boolean firstTimeLogin = true;
//    LoadingDialog loadAnimation;
//    DrawerLayout drawerLayout;
//    ImageView menuButton;
//    NavigationView navigationView;
//    NavController navController;
//    TextView emailView;
//    TextView userNameView;
//    boolean loginStatus = false;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        loadAnimation = new LoadingDialog(MainActivity.this);
//        startLogin();
//
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
//
//    /**
//     * launch the LoginActivity
//     */
//    private void startLogin(){
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivityForResult(intent, 1);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && data != null) {
//            if (resultCode == RESULT_OK) {
//                loadAnimation.startLoadingDialog();
//                Log.d(TAG, "Return succeed");
//                email = data.getStringExtra("email_from_login");
//                Log.d(TAG, "Email fetched " + email);
//                db = FirebaseFirestore.getInstance();
//                final CollectionReference collectionReference = db.collection("UserData");
//                collectionReference.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            userDocument = task.getResult();
//                            assert userDocument != null;
//                            if (userDocument.exists()) {
//                                Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
//                                userName = userDocument.getString("UserName");
//                                Log.d(TAG, "Username in onComplete is " + userName);
//                                firstTimeLogin = false;
//                                loginStatus = true;
//                                inflateNavDrawer();
//                            } else {
//                                Log.d(TAG, "No such document");
//
//                            }
//                        } else {
//                            Log.d(TAG, "Get failed with ", task.getException());
//                        }
//                    }
//                });
//
//
//            }
//            loadAnimation.dismissLoadingDialog();
//        }
//
//        if(resultCode == RESULT_CANCELED && firstTimeLogin){
//            // If it is the first time login, then close the app when the user press the "return" button
//            // This prevents the user from skipping the login step, and directly go to the MainActivity
//            finish();
//        }
//    }
//
//    void inflateNavDrawer(){
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.appBarMain.toolbar);
//
//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_my_habits, R.id.nav_habit_events)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//
//        View headerView = navigationView.getHeaderView(0);
//        TextView navUsername = (TextView) headerView.findViewById(R.id.view_user_name);
//        TextView navEmail = (TextView) headerView.findViewById(R.id.view_user_email);
//        navUsername.setText(userName);
//        navEmail.setText(email);
//    }
//
//}