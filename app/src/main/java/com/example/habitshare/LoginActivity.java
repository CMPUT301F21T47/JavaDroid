package com.example.habitshare;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
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
    LoadingDialog loadAnimation;
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
        loadAnimation = new LoadingDialog(LoginActivity.this);


        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("UserData");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the error message
                enterEmail.setError(null);
                enterPassword.setError(null);

                final String email = enterEmail.getText().toString();
                final String password = enterPassword.getText().toString();
                Log.d(TAG, "Password is " + password);

                if(email.equals("")){
                    enterEmail.setError("Email address cannot be empty");
                }
                if(password.equals("")){
                    enterPassword.setError("Password cannot be empty");
                }
                if(!email.equals("")){
                    loadAnimation.startLoadingDialog();
                    collectionReference.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot userDocument = task.getResult();
                                if (userDocument.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                                    final String cloudPassword = userDocument.getString("Password");
                                    if(password.equals(cloudPassword)){
                                        Log.d(TAG, "Before set result");
                                        Intent passBack = new Intent();
                                        passBack.putExtra("email_from_login", email);
                                        setResult(RESULT_OK, passBack);
                                        Log.d(TAG, "After set result");
                                        loadAnimation.dismissLoadingDialog();
                                        finish();
                                    }
                                    else{
                                        loadAnimation.dismissLoadingDialog();
                                        wrongPassword();
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                    loadAnimation.dismissLoadingDialog();
                                    wrongEmail();
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                                loadAnimation.dismissLoadingDialog();
                                showToast("Error: Failed to get data");

                            }
                        }
                    });
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set to edittext box to default value
                enterEmail.setText("");
                enterPassword.setText("");
                enterEmail.setError(null);
                enterPassword.setError(null);
                Intent registration = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(registration);
            }
        });
    }

    /**
     * Deal with wrong email address
     */
    private void wrongEmail(){
        enterEmail.setError("The email address you have entered is incorrect or does not exist. Please try again.");
    }

    /**
     * Deal with wrong password
     */
    private void wrongPassword(){
        enterPassword.setError("The password you have entered is incorrect. Please try again or reset your password.");
    }

    /**
     * Make a toast (short)
     * @param str a toast message
     */
    private void showToast(String str){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}


