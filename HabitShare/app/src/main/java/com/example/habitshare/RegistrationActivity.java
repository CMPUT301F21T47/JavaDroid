package com.example.habitshare;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    String userName;
    String email;
    String password;
    String password2;
    EditText emailEdittext;
    EditText passwordEdittext;
    EditText password2Edittext;
    EditText userNameEdittext;
    LoadingDialog loadAnimation;
    Button registerButton;
    FirebaseFirestore db;
    String specialCharacters = "!@#$%^&*()";
    final static String TAG = "RegistrationActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // find view by id
        emailEdittext = findViewById(R.id.register_enter_email);
        passwordEdittext = findViewById(R.id.register_enter_password);
        password2Edittext = findViewById(R.id.register_reenter_password);
        userNameEdittext = findViewById(R.id.register_enter_username);
        registerButton = findViewById(R.id.register_button);
        loadAnimation = new LoadingDialog(RegistrationActivity.this);

        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEdittext.getText().toString();
                password = passwordEdittext.getText().toString();
                password2 = password2Edittext.getText().toString();
                userName = userNameEdittext.getText().toString();
                register();
            }
        });
    }

    private void register(){
        // Check input validity
        if(email.equals("")){ // check if email is an empty string
            emailEdittext.setError("Email address cannot be empty");
        }
        if(password.equals("")){ // check if password is an empty string
            passwordEdittext.setError("Password cannot be empty");
        }
        if(userName.equals("")){ // check if user name is an empty string
            userNameEdittext.setError("User name cannot be empty");
        }
        if(!isValidEmail(email)){ // check if email's validity'
            emailEdittext.setError("This email address is invalid");
        }
        if(!isValidPassword(password)){ // check password's validity
            passwordEdittext.setError("Your password must contain the following:\n" +
                    "▪ 8-64 characters\n" +
                    "▪ At least 1 lowercase letter\n" +
                    "▪ At least 1 uppercase letter\n" +
                    "▪ At least 1 number");
        }
        if(!password.equals(password2)) { // check if the two passwords are identical
            password2Edittext.setError("Does not match with the first password you entered!");
        }
        if(!email.equals("")) { // Check if the email has been taken (this ensures that an email address is the unique identification of an account)
            final CollectionReference collectionReference = db.collection("UserData");
            collectionReference.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDocument = task.getResult();
                        assert userDocument != null;
                        if (userDocument.exists()) { // if the email has already exists in the firebase, then tell the user that the email has been taken
                            Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                            emailEdittext.setError("This email address has been taken. Please try with another one.");
                        }
                        else {
                            Log.d(TAG, "No such document");
                            // If everything is fine, then we complete a successful registration
                            if(isValidEmail(email) && isValidPassword(password) && !userName.equals("") && password.equals(password2)){
                                loadAnimation.startLoadingDialog();
                                HashMap<String, String> data = new HashMap<>();
                                data.put("Password", password);
                                data.put("UserName", userName);
                                collectionReference.document(email)
                                        .set(data) // put user name and password onto FireStore
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {// a successful registration
                                                Log.d(TAG, "Data has been added successfully!");
                                                loadAnimation.dismissLoadingDialog();
                                                showToast("Registration complete");
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() { // registration failure due to unknown reason
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Data could not be added!" + e.toString());
                                                loadAnimation.dismissLoadingDialog();
                                                showToast("Error: Cannot add this user profile!");
                                            }
                                        });
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    /**
     * Check if an email address is valid
     * @param email a string that contains an email address
     * @return true, if the email is valid; false otherwise.
     */
    private boolean isValidEmail(String email){
        int before_at = 1;
        int before_dot = 2;
        int after_dot = 3;
        int indicator = 1;

        for(int i = 0; i < email.length(); i++){
            // check formats
            if(email.charAt(i) == '@')  {
                if(indicator != before_at){
                    return false;
                }
                if(i == 0){
                    return false;
                }
                indicator = before_dot;
                continue;
            }
            if(email.charAt(i) == '.'){
                if(indicator != before_dot){
                    return false;
                }
                if(i == email.length() - 1){
                    return false;
                }
                if(email.charAt(i-1) == '@'){
                    return false;
                }
                indicator = after_dot;
                continue;
            }
            // check special characters
            if(specialCharacters.contains(String.valueOf(email.charAt(i)))){
                return false;
            }
        }
        if(indicator == after_dot){
            Log.d(TAG, "Email is valid");
        }
        return indicator == after_dot;
    }


    /**
     * Check if a password is valid
     * @param password a string that contains password
     * @return true if the password is valid; false otherwise
     */
    private boolean isValidPassword(String password){
        boolean containLowercase = false;
        boolean containUppercase = false;
        boolean containNumber = false;

        // check length
        if(password.length() < 8 || password.length() > 64){
            return false;
        }

        // check if the password contains at least a lowercase, an uppercase and a number
        for(int i = 0; i < password.length(); i++){
            if(isUpperCase(password.charAt(i))){
                containUppercase = true;
            }
            if(isLowerCase(password.charAt(i))){
                containLowercase = true;
            }
            if(isDigit(password.charAt(i))){
                containNumber = true;
            }
        }
        if(containLowercase && containUppercase && containNumber){
            Log.d(TAG, "Password is valid");
        }
        return containLowercase && containUppercase && containNumber;

    }

    /**
     * Make a toast (short)
     * @param str a toast message
     */
    private void showToast(String str){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }


}