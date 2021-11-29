package com.example.habitshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // View Variables
    Button loginButton;
    TextView signup;
    EditText enterEmail;
    EditText enterPassword;
    CheckBox rememberMe;

    // Data Variables
    FirebaseFirestore db;
    LoadingDialog loadAnimation;
    String email;
    String password;
    final static String TAG = "LoginActivity";
    boolean isSaveAccount = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find Views by Id
        loginButton = findViewById(R.id.login_button);
        signup = findViewById(R.id.sign_up_textview);
        enterEmail = findViewById(R.id.login_enter_email);
        enterPassword = findViewById(R.id.login_enter_password);
        rememberMe = findViewById(R.id.checkBox_remember_me);

        // initialize same data variables
        loadAnimation = new LoadingDialog(LoginActivity.this);
        db = FirebaseFirestore.getInstance();

        // put the save account information into the edittexts
        getSavedAccount();
        if(isSaveAccount){
            enterEmail.setText(email);
            enterPassword.setText(password);
            rememberMe.setChecked(true);
        }

        setRememberMeCheckbox();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the error message
                enterEmail.setError(null);
                enterPassword.setError(null);

                // convert the input to strings
                email = enterEmail.getText().toString();
                password = enterPassword.getText().toString();
                login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistrationActivity();
            }
        });
    }

    /**
     * Get the account info from the SharedPreference
     */
    private void getSavedAccount(){
        SharedPreferences preferences = getSharedPreferences("SaveAccount", MODE_PRIVATE);
        isSaveAccount = preferences.getBoolean("saved", false);
        email = preferences.getString("email", "");
        password = preferences.getString("password", "");
    }

    /**
     * Defines the behavior of the app when the checkbox is checked or unchecked
     */
    private void setRememberMeCheckbox(){
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSaveAccount = isChecked;
                if(!isChecked){
                    // clear the saved account
                    SharedPreferences preferences = getSharedPreferences("SaveAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("saved", false);
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.apply();
                }
            }
        });
    }

    /**
     * Save the account info to the SharedPreference
     * @param email the correct email address
     * @param password the correct password
     */
    private void saveAccount(String email, String password){
        SharedPreferences preferences = getSharedPreferences("SaveAccount", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("saved", true);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    /**
     * Empty the previous user input and start the registration activity
     */
    private void startRegistrationActivity(){
        // clear the error message
        enterEmail.setError(null);
        enterPassword.setError(null);
        Intent registration = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(registration);
    }

    /**
     * Check the user's input and set the corresponding error messages
     */
    private void login(){
        // check validity
        if(email.equals("")){ // check if the email is an empty string
            enterEmail.setError("Email address cannot be empty");
        }
        if(password.equals("")){ // check if the password is an empty string
            enterPassword.setError("Password cannot be empty");
        }
        if(!email.equals("")){ // check the existence of the account
            loadAnimation.startLoadingDialog();
            final CollectionReference collectionReference = db.collection("UserData");
            collectionReference.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDocument = task.getResult();
                        if (userDocument.exists()) { // if the account exist and the password is correct, then it is a successful login
                            Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                            final String cloudPassword = userDocument.getString("Password");
                            if(password.equals(cloudPassword)){ // a successful login
                                if(isSaveAccount){
                                    saveAccount(email, password);
                                }
                                Intent passBack = new Intent();
                                passBack.putExtra("email_from_login", email);
                                setResult(RESULT_OK, passBack);
                                loadAnimation.dismissLoadingDialog();
                                finish();
                            }
                            else{ // login failure due to wrong password
                                loadAnimation.dismissLoadingDialog();
                                wrongPassword();
                            }
                        } else { // login failure due to no such account
                            Log.d(TAG, "No such document");
                            loadAnimation.dismissLoadingDialog();
                            wrongEmail();
                        }
                    } else { // login failure due to other exceptions
                        Log.d(TAG, "get failed with ", task.getException());
                        loadAnimation.dismissLoadingDialog();
                        showToast("Error: Failed to get data");
                    }
                }
            });
        }
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
