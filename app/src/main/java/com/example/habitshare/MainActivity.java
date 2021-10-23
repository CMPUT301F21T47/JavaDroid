package com.example.habitshare;

import android.content.Intent;
import android.os.Bundle;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    String email;
    String userName = "SomeUser";
    FirebaseFirestore db;
    final static String TAG = "MainActivity";
    TextView displayUserName;
    DocumentSnapshot userDocument;
    boolean firstTimeLogin = true;
    LoadingDialog loadAnimation;

    private ActivityResultLauncher<Intent> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLogin();


        /* ------------------------------------------------------------------- */
        // This part is for demonstration only!
        displayUserName = findViewById(R.id.textView4);
        /* ------------------------------------------------------------------- */
        loadAnimation = new LoadingDialog(MainActivity.this);
    }

    /**
     * launch the LoginActivity
     */
    private void startLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            loadAnimation.startLoadingDialog();
            if (resultCode == RESULT_OK) {
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

                                /* ------------------------------------------------------------------- */
                                // This part is for demonstration only!
                                String password = userDocument.getString("Password");
                                String userinfo = "UserName: " + userName +"\n"
                                        + "Email Address: " + email + "\n"
                                        + "Password: " + password;
                                displayUserName.setText(userinfo);
                                /* ------------------------------------------------------------------- */

                                firstTimeLogin = false;

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

}