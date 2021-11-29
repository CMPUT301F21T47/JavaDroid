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
    // View Variables
    private final static String TAG = "ViewHabitEventActivity";
    TextView viewHabitEventTitle;
    TextView viewHabitEventComment;
    TextView viewHabitEventDate;
    TextView viewHabitEventLocation;
    Button buttonViewHabitEventEdit;
    Button buttonViewHabitEventDelete;
    Button buttonViewHabitEventCancel;
    ImageView viewHabitEventImage;

    // Data Variables
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

        // find view by id
        viewHabitEventTitle = findViewById(R.id.view_habit_event_title);
        viewHabitEventComment = findViewById(R.id.view_habit_event_comment);
        viewHabitEventDate = findViewById(R.id.view_habit_event_date);
        buttonViewHabitEventEdit = findViewById(R.id.edit_habit_event);
        buttonViewHabitEventDelete = findViewById(R.id.delete_habit_event);
        buttonViewHabitEventCancel = findViewById(R.id.cancel_habit_event);
        viewHabitEventImage = findViewById(R.id.view_habit_event_image);
        viewHabitEventLocation = findViewById(R.id.view_habit_event_location);

        db = FirebaseFirestore.getInstance();

        // get data from intent
        Intent intent = getIntent();
        habitTitle = intent.getStringExtra("habit_title");
        comment = intent.getStringExtra("comment");
        denoteDate = intent.getStringExtra("denote_date");
        eventTitle = intent.getStringExtra("event_title");
        location = intent.getStringExtra("location");

        // if there is uri data passed to this activity, then fetch it
        if(intent.getStringExtra("image_uri") != null){
            imageURI = Uri.parse(intent.getStringExtra("image_uri"));
            viewHabitEventImage.setImageURI(imageURI);
            viewHabitEventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage();
                }
            });
        }

        viewHabitEventTitle.setText(habitTitle);

        if(comment.equals("")){ // if the user didn't set the comment, then we use the gray font to display "(No comment)"
            viewHabitEventComment.setText("(No comment)");
            viewHabitEventComment.setTextColor(Color.GRAY);
        }
        else{
            viewHabitEventComment.setText(comment);
        }

        viewHabitEventLocation.setText(location);

        if(location.equals("N/A")){ // if the user didn't set the location, then we use the gray font to display "N/A"
            viewHabitEventLocation.setTextColor(Color.GRAY);
        }
        viewHabitEventDate.setText(denoteDate);

        buttonViewHabitEventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editHabitEvent();
                finish();
            }
        });

        buttonViewHabitEventDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHabitEvent();
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

    /**
     * Opens an activity to see the image in full screen
     */
    private void viewImage() {
        if(imageURI != null){
            Intent intent = new Intent(ViewHabitEventActivity.this, ViewLargerImageActivity.class);
            intent.putExtra("image_uri", imageURI.toString());
            startActivity(intent);
        }
    }

    /**
     * Opens DenoteHabitActivity to edit the habit event
     */
    private void editHabitEvent(){
        Intent intent = new Intent(ViewHabitEventActivity.this, DenoteHabitActivity.class);
        intent.putExtra("control_code", 1); // controlCode being 1 means that we open DenoteHabitActivity to edit a habit event instead of adding a habit event
        if(imageURI != null){
            intent.putExtra("image_uri", imageURI.toString());
        }
        intent.putExtra("location", location);
        intent.putExtra("habit_title", habitTitle);
        intent.putExtra("comment", comment);
        intent.putExtra("denote_date", denoteDate);
        startActivity(intent);
    }

    /**
     * Deletes a habit event and the corresponding image (if it has one)
     */
    private void deleteHabitEvent(){
        final StorageReference imageRef = storageReference.child("images/" + eventTitle);
        final CollectionReference habitEventsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
        final CollectionReference habitsReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        if(imageURI != null) {
            // delete the corresponding image if it has one
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        // delete the habit event
        habitEventsReference
                .document(eventTitle)
                .delete();
    }
}