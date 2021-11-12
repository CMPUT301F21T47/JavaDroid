package com.example.habitshare;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ViewHabitEventActivity extends AppCompatActivity {
    private final static String TAG = "ViewHabitEventActivity";
    TextView viewHabitEventTitle;
    TextView viewHabitEventComment;
    TextView viewHabitEventDate;
    TextView viewHabitEventLocation;
    Button buttonViewHabitEventEdit;
    Button buttonViewHabitEventDelete;
    Button buttonViewHabitEventCancel;
    ImageView viewHabitEventImage;
    String location;
    String habitTitle;
    String eventTitle;
    String comment;
    String denoteDate;
    Uri imageURI;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit_event);
        viewHabitEventTitle = findViewById(R.id.view_habit_event_title);
        viewHabitEventComment = findViewById(R.id.view_habit_event_comment);
        viewHabitEventDate = findViewById(R.id.view_habit_event_date);
        buttonViewHabitEventEdit = findViewById(R.id.edit_habit_event);
        buttonViewHabitEventDelete = findViewById(R.id.delete_habit_event);
        buttonViewHabitEventCancel = findViewById(R.id.cancel_habit_event);
        viewHabitEventImage = findViewById(R.id.view_habit_event_image);
        viewHabitEventLocation = findViewById(R.id.view_habit_event_location);

        Log.d(TAG, "viewHabitEventLocation" + (viewHabitEventLocation == null));

        // get data from intent
        Intent intent = getIntent();
        habitTitle = intent.getStringExtra("habit_title");
        comment = intent.getStringExtra("comment");
        denoteDate = intent.getStringExtra("denote_date");
        eventTitle = intent.getStringExtra("event_title");
        location = intent.getStringExtra("location");



        Log.d(TAG, "image file name is " + eventTitle);
        if(intent.getStringExtra("image_uri") != null){
            imageURI = Uri.parse(intent.getStringExtra("image_uri"));
            viewHabitEventImage.setImageURI(imageURI);
        }

        db = FirebaseFirestore.getInstance();
        final CollectionReference habitEventsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
        final CollectionReference habitsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        if(!eventTitle.equals("")){
            viewHabitEventImage.setImageURI(imageURI);
            viewHabitEventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage();
                }
            });
        }

        viewHabitEventTitle.setText(habitTitle);
        if(comment.equals("")){
            viewHabitEventComment.setText("(No comment)");
            viewHabitEventComment.setTextColor(Color.GRAY);
        }
        else{
            viewHabitEventComment.setText(comment);
        }

        viewHabitEventLocation.setText(location);

        if(location.equals("N/A")){
            viewHabitEventLocation.setTextColor(Color.GRAY);
        }
        viewHabitEventDate.setText(denoteDate);

        buttonViewHabitEventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewHabitEventActivity.this, DenoteHabitActivity.class);
                intent.putExtra("request_code", 1);
                if(imageURI != null){
                    intent.putExtra("image_uri", imageURI.toString());
                }
                intent.putExtra("habit_title", habitTitle);
                intent.putExtra("comment", comment);
                intent.putExtra("denote_date", denoteDate);
                imageURI = null;
                startActivity(intent);
                finish();
            }
        });

        buttonViewHabitEventDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StorageReference imageRef = storageReference.child("images/" + eventTitle);
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                // remove the habit event
                habitEventsReference
                        .document(eventTitle)
                        .delete();

                // set the status of the habit to Not Done
                habitsReference.document(habitTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String lastTimeDenoted = (String) doc.getData().get("Last Time Denoted");
                                if (lastTimeDenoted.equals(denoteDate)) {
                                    String habitTitle = doc.getId();
                                    HashMap<String, Object> data = new HashMap<>();
                                    data.put("Status", true);
                                    data.put("Last Time Denoted", "");

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

                            }
                        }
                    }
                });
                finish();
            }
        });
        buttonViewHabitEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void viewImage() {
        Intent intent = new Intent(ViewHabitEventActivity.this, ViewLargerImageActivity.class);
        intent.putExtra("image_uri", imageURI.toString());
        startActivity(intent);
    }
}