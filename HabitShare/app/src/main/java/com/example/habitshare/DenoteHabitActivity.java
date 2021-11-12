package com.example.habitshare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    private final static String TAG = "DenoteHabitActivity";
    private final static int REQUEST_GALLERY = 2;
    private final static int REQUEST_CAMERA = 3;
    HashMap<String, Object> data = new HashMap<>();
    TextView denoteHabitName;
    EditText enterComment;
    Button confirmDenote;
    Button cancelDenote;
    Button pickLocation;
    ImageView habitEventImage;
    FirebaseFirestore db;
    String eventTitle;
    String currentDate;
    String habitTitle;
    String comment;
    String currentPhotoPath;
    String denoteDate;
    Uri imageURI;
    LoadingDialog loadAnimation;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    boolean checkSelectImage;
    boolean selectFromGallery;
    boolean selectFromCamera;
    boolean hasImage;
    int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denote_habit);
        denoteHabitName = findViewById(R.id.denote_habit_name);
        enterComment = findViewById(R.id.denote_habit_comment);
        confirmDenote = findViewById(R.id.denote_habit_confirm_button);
        cancelDenote = findViewById(R.id.denote_habit_cancel_button);
        pickLocation = findViewById(R.id.button_pick_location);
        habitEventImage = findViewById(R.id.habit_event_image);

        hasImage = false;
        db = FirebaseFirestore.getInstance();
        final CollectionReference habitEventsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
        final CollectionReference habitsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        // get data from intent
        Intent intent = getIntent();

        requestCode = intent.getIntExtra("request_code", 0);
        habitTitle = intent.getStringExtra("habit_title");
        eventTitle = intent.getStringExtra("event_title");


        // preparation for editing a habit event
        if(requestCode == 1){
            if(intent.getStringExtra("image_uri") != null){
                imageURI = Uri.parse(intent.getStringExtra("image_uri"));
                habitEventImage.setImageURI(imageURI);
                hasImage = true;
            }
            comment = intent.getStringExtra("comment");
            enterComment.setText(comment);
            denoteDate = intent.getStringExtra("denote_date");
            eventTitle = habitTitle + "-" +denoteDate;
        }

        // get current date to keep a record on the denote date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDate = dateFormat.format(cal.getTime());

        if(requestCode == 0){
            denoteDate = currentDate;
            eventTitle = habitTitle + "-" + currentDate;
        }

        denoteHabitName.setText(habitTitle);

        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(DenoteHabitActivity.this, MapActivity.class);
//                startActivity(intent);
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
                final String comment = enterComment.getText().toString();
                if(comment.length() > 20){
                    enterComment.setError("A comment cannot have more than 20 characters");
                }
                else{
                    data.put("Habit Title", habitTitle);
                    data.put("Comment", comment);
                    data.put("Denote Date", denoteDate);
                    if(checkSelectImage){
                        if(imageURI != null){
                            // upload image if URI is not null
                            data.put("Has Image", "True");
                            StorageReference imageRef = storageReference.child("images/" + eventTitle);
                            loadAnimation = new LoadingDialog(DenoteHabitActivity.this);
                            loadAnimation.startLoadingDialog();
                            imageRef.putFile(imageURI)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            loadAnimation.dismissLoadingDialog();
                                            imageURI = null;
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
                                            if(requestCode == 0){
                                                data = new HashMap<>();
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
                        if(requestCode == 1 && hasImage){
                            data.put("Has Image", "True");
                        }
                        else{
                            data.put("Has Image", "False");
                        }

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
                        if(requestCode == 0){
                            data = new HashMap<>();
                            data.put("Status", true);
                            data.put("Last Time Denoted", currentDate);
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
        });

        cancelDenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_GALLERY && data != null){
                imageURI = data.getData();
                habitEventImage.setImageURI(imageURI);
                checkSelectImage = true;
            }
            if(requestCode == REQUEST_CAMERA && data != null){
                if(imageURI != null){
                    habitEventImage.setImageURI(imageURI);
                    checkSelectImage = true;
                }
            }
        }
    }


}