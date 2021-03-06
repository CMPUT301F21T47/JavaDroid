package com.example.habitshare;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DenoteHabitActivity extends AppCompatActivity {

    // View Variables
    TextView tvLocation;
    TextView denoteHabitName;
    EditText enterComment;
    Button confirmDenote;
    Button cancelDenote;
    Button pickLocation;
    ImageView habitEventImage;

    // Data Variables
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_LOCATION_PERMISSION = 999;
    private final static String TAG = "DenoteHabitActivity";
    private final static int REQUEST_GALLERY = 2;
    private final static int REQUEST_CAMERA = 3;
    private final static int REQUEST_MAP = 999;
    HashMap<String, Object> data = new HashMap<>();
    FirebaseFirestore db;
    String eventTitle;
    String currentDate;
    String habitTitle;
    String comment;
    String currentPhotoPath;
    String denoteDate;
    String addressLine = "N/A";
    Uri imageURI;
    LoadingDialog loadAnimation;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    boolean checkSelectImage;
    boolean selectFromGallery;
    boolean selectFromCamera;
    boolean hasImage;
    int controlCode;
    int numberOfTimesActuallyDenoted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denote_habit);

        // find view by id
        denoteHabitName = findViewById(R.id.denote_habit_name);
        enterComment = findViewById(R.id.denote_habit_comment);
        confirmDenote = findViewById(R.id.denote_habit_confirm_button);
        cancelDenote = findViewById(R.id.denote_habit_cancel_button);
        pickLocation = findViewById(R.id.button_pick_location);
        habitEventImage = findViewById(R.id.habit_event_image);
        tvLocation = findViewById(R.id.tv_location);

        // initialized some variables
        hasImage = false;
        db = FirebaseFirestore.getInstance();

        // get data from intent
        Intent intent = getIntent();
        controlCode = intent.getIntExtra("control_code", 0);
        habitTitle = intent.getStringExtra("habit_title");
        addressLine = intent.getStringExtra("location");
        numberOfTimesActuallyDenoted = intent.getIntExtra("number_time_actual_denoted",0);

        // preparation for editing a habit event
        if(controlCode == 1){ // controlCode being 1 means that the user reaches here to edit a habit event
            if(intent.getStringExtra("image_uri") != null){
                // when the habit event has an image then we set the imageView
                imageURI = Uri.parse(intent.getStringExtra("image_uri"));
                habitEventImage.setImageURI(imageURI);
                hasImage = true;
            }
            tvLocation.setText(addressLine);
            if(addressLine.equals("N/A")){
                // set the color to gray if the location is not set to show that this is not important
                tvLocation.setTextColor(Color.GRAY);
            }
            eventTitle = intent.getStringExtra("event_title");
            comment = intent.getStringExtra("comment");
            enterComment.setText(comment);
            denoteDate = intent.getStringExtra("denote_date");
            eventTitle = habitTitle + "-" +denoteDate;
        }

        // get current date to keep a record on the denote date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDate = dateFormat.format(cal.getTime());

        if(controlCode == 0){
            // when the user enters here to add a habit event
            denoteDate = currentDate;
            eventTitle = habitTitle + "-" + currentDate;
        }

        denoteHabitName.setText(habitTitle);

        // All OnClickerListeners
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(DenoteHabitActivity.this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(DenoteHabitActivity.this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DenoteHabitActivity.this, new String[]{FINE_LOCATION, COARSE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
                }
                else{
                Intent intent = new Intent(DenoteHabitActivity.this, MapActivity.class);
                startActivityForResult(intent, REQUEST_MAP);}
            }
        });

        habitEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageSourceDialog();
            }
        });

        confirmDenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                denoteHabit();
            }
        });

        cancelDenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * upload user input of an habit event to FireStore
     */
    private void denoteHabit(){
        final String comment = enterComment.getText().toString();
        final CollectionReference habitEventsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
        final CollectionReference habitsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        if(comment.length() > 20){// check if the length of comment exceeds the constraint
            enterComment.setError("A comment cannot have more than 20 characters");
        }
        else{
            data.put("Habit Title", habitTitle);
            data.put("Comment", comment);
            data.put("Denote Date", denoteDate);
            if(addressLine == null){
                data.put("Location", "N/A");
            }
            else {
                data.put("Location", addressLine);
            }
            if(checkSelectImage){
                if(imageURI != null){// when the user wants to upload an image
                    // upload image if URI is not null
                    data.put("Has Image", true);
                    StorageReference imageRef = storageReference.child("images/" + eventTitle);
                    loadAnimation = new LoadingDialog(DenoteHabitActivity.this);
                    loadAnimation.startLoadingDialog();
                    imageRef.putFile(imageURI)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    loadAnimation.dismissLoadingDialog();
                                    habitEventsReference
                                            .document(eventTitle)
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
                                    if(controlCode == 0){
                                        // when the user reaches here to denote a habit, we need to set the status to true and set  lastTimeDenoted
                                        data = new HashMap<>();
                                        data.put("NumberOfTimeActuallyDenoted", numberOfTimesActuallyDenoted + 1);
                                        data.put("Status", true);
                                        data.put("Last Time Denoted", denoteDate);
                                        habitsReference.document(habitTitle)
                                                .update(data)
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
                                    }
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadAnimation.dismissLoadingDialog();
                                }
                            });
                }
            }
            // denote without an image
            else{
                Log.d(TAG, "hasImage"  + hasImage);
                if(controlCode == 1 && hasImage){
                    data.put("Has Image", true);
                }
                else{
                    data.put("Has Image", false);
                }

                // upload the modified habit event
                habitEventsReference
                        .document(eventTitle)
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

                if(controlCode == 0){
                    // upload the new habit event
                    data = new HashMap<>();
                    data.put("Status", true);
                    data.put("Last Time Denoted", currentDate);
                    data.put("NumberOfTimeActuallyDenoted", numberOfTimesActuallyDenoted + 1);
                    habitsReference.document(habitTitle)
                            .update(data)
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

                }
                Log.d(TAG, "reached here");
                finish();
            }

        }
    }

    /**
     * Pops up a dialog to ask the user to get an image from gallery or camera
     */
    private void selectImageSourceDialog(){
        final Dialog dialog = new Dialog(DenoteHabitActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.select_image_source);

        ImageView gallery = dialog.findViewById(R.id.imageview_gallery);
        ImageView camera = dialog.findViewById(R.id.imageview_camera);

        selectFromGallery = false;
        selectFromCamera = false;

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dispatchTakePictureIntent();
            }
        });
        dialog.show();
    }

    /**
     * create an empty image file locally
     * This method is from Android Studio official documentation
     * Link of source: https://developer.android.com/training/camera/photobasics.html#TaskPath
     * @return an image file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Obtained From Google Android Studio Guides
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Start the camera activity to get a FULL-SIZE image instead of a lossy image
     * This method is from Android Studio official documentation
     * Link of source: https://developer.android.com/training/camera/photobasics.html#TaskPath
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageURI = FileProvider.getUriForFile(DenoteHabitActivity.this,
                        "com.example.android.fileprovider",
                        photoFile);
                Log.d(TAG, "image uri is " + imageURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case REQUEST_GALLERY :
                    imageURI = data.getData();
                    habitEventImage.setImageURI(imageURI);
                    checkSelectImage = true;
                    break;

                case REQUEST_CAMERA :
                    habitEventImage.setImageURI(imageURI);
                    checkSelectImage = true;
                    break;

                case REQUEST_MAP :
                    addressLine = data.getStringExtra("address_line");
                    tvLocation.setText(addressLine);
                    tvLocation.setTextColor(Color.BLACK);
                    break;
            }
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on ActivityCompat.requestPermissions(android.app.Activity, String[], int)
     * @param requestCode The request code passed in ActivityCompat.requestPermissions(android.app.Activity, String[], int)
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_LOCATION_PERMISSION:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_DENIED){
                            return;
                        }
                    }
                    Intent intent = new Intent(DenoteHabitActivity.this, MapActivity.class);
                    startActivityForResult(intent, REQUEST_MAP);
                }
                else{
                    Toast.makeText(DenoteHabitActivity.this, "Fail to get location permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


}