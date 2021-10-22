package com.example.habitshare;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    TextView signup;
    EditText enterEmail;
    EditText enterPassword;
    FirebaseFirestore db;
    final static String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find Views by Id
        loginButton = findViewById(R.id.login_button);
        signup = findViewById(R.id.sign_up_textview);
        enterEmail = findViewById(R.id.login_enter_email);
        enterPassword = findViewById(R.id.login_enter_password);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("UserData");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = enterEmail.getText().toString();
                String password = enterPassword.getText().toString();
                collectionReference.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot userDocument = task.getResult();
                            if (userDocument.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                                final String cloudPassword = userDocument.getString("Password");
                                if(password.equals(cloudPassword)){
                                    finish();
                                }
                                else{

                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
    }

    private void wrongPassword(){
        enterPassword.setText("");
        enterPassword.setError("The password you have entered is incorrect. Please try again or reset your password.");
    }
}

