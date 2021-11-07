package com.example.habitshare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class HabitEventsFragment extends Fragment {

    private ArrayList<HabitEvent> habitEventDataList;
    private CustomHabitEventListAdapter<HabitEvent> habitEventArrayAdapter;
    private final static String TAG = "Habit Events";
    private String habitTitle;
    private boolean checkSelectImage;
    private Uri imageURI = null;
    private File imageFile;
    ListView habitEventList;
    ImageView habitEventImage;
    FirebaseFirestore db;
    Habit habit;
    String currentDate;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    LoadingDialog loadAnimation;
    Bitmap bitmap;
    int i;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_habit_events, container, false);
        habitEventList = view.findViewById(R.id.list_habit_events);
        habitEventDataList = new ArrayList<>();
        habitEventArrayAdapter = new CustomHabitEventListAdapter<HabitEvent>(getContext(), habitEventDataList);
        habitEventList.setAdapter(habitEventArrayAdapter);
        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        habitEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                i = position;
                HabitEvent habitEvent = habitEventDataList.get(i);
                if(!habitEvent.getImageFileName().equals("")){
                    try{
                        loadAnimation = new LoadingDialog(getContext());
                        loadAnimation.startLoadingDialog();
                        imageFile = File.createTempFile(habitEvent.getImageFileName(), "");
                        storageReference.child("images/" + habitEvent.getImageFileName()).getFile(imageFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                        loadAnimation.dismissLoadingDialog();
                                        showViewHabitEventDialog();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadAnimation.dismissLoadingDialog();
                                    }
                                });
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                    showViewHabitEventDialog();
                }
            }
        });

        return view;
    }

    /**
     * start a view habit event dialog
     */
    private void showViewHabitEventDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.view_habit_event_layout);
        CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
        CollectionReference collectionReference2 = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");

        HabitEvent habitEvent = habitEventDataList.get(i);

        TextView viewHabitEventTitle = dialog.findViewById(R.id.view_habit_event_title);
        TextView viewHabitEventComment = dialog.findViewById(R.id.view_habit_event_comment);
        TextView viewHabitEventDate = dialog.findViewById(R.id.view_habit_event_date);
        Button buttonViewHabitEventEdit = dialog.findViewById(R.id.edit_habit_event);
        Button buttonViewHabitEventDelete = dialog.findViewById(R.id.delete_habit_event);
        Button buttonViewHabitEventCancel = dialog.findViewById(R.id.cancel_habit_event);
        ImageView viewHabitEventImage = dialog.findViewById(R.id.view_habit_event_image);
        if(!habitEvent.getImageFileName().equals("")){
            viewHabitEventImage.setImageBitmap(bitmap);
        }
        habitTitle = habitEvent.getTitle();
        viewHabitEventTitle.setText(habitTitle);
        viewHabitEventComment.setText(habitEvent.getComment());
        viewHabitEventDate.setText(habitEvent.getDenoteDate());

        buttonViewHabitEventEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showEditHabitEventDialog();

            }
        });

        buttonViewHabitEventDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String habitTitle = habitEvent.getTitle();
                final StorageReference imageRef= storageReference.child("images/" + habitEvent.getImageFileName());
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
                collectionReference
                        .document(habitTitle)
                        .delete();

                // set the status of the habit to Not Done
                collectionReference2.document(habitTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()){
                                String lastTimeDenoted = (String) doc.getData().get("Last Time Denoted");
                                Log.d(TAG, "Denoate Date is: " + habitEvent.getDate());
                                Log.d(TAG, "Last Time Denoted is: " + lastTimeDenoted);
                                if(lastTimeDenoted.equals(habitEvent.getDenoteDate())){
                                    String habitTitle = doc.getId();
                                    String date = (String) doc.getData().get("Date of Start");
                                    String daysOfWeek = (String) doc.getData().get("Days of Week");
                                    String reason = (String) doc.getData().get("Reason");
                                    HashMap<String, String> data = new HashMap<>();
                                    data.put("Date of Start", date);
                                    data.put("Reason", reason);
                                    data.put("Days of Week", daysOfWeek);
                                    data.put("Status", "Not Done");
                                    data.put("Last Time Denoted", "");

                                    collectionReference2.document(habitTitle)
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
                                }

                            }
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        buttonViewHabitEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * start an edit habit event dialog
     */
    private void showEditHabitEventDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_habit_event_layout);

        HabitEvent habitEvent = habitEventDataList.get(i);
        habitTitle = habitEvent.getTitle();
        String comment = habitEvent.getComment();
        String denoteDate = habitEvent.getDenoteDate();

        final CollectionReference collectionReference1 = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");
        final CollectionReference collectionReference2 = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habits");


        TextView denoteHabitName = dialog.findViewById(R.id.denote_habit_name);
        EditText enterComment = dialog.findViewById(R.id.denote_habit_comment);
        Button confirmDenote = dialog.findViewById(R.id.denote_habit_confirm_button);
        Button cancelDenote = dialog.findViewById(R.id.denote_habit_cancel_button);
        habitEventImage = dialog.findViewById(R.id.habit_event_image);

        denoteHabitName.setText(habitTitle);
        enterComment.setText(comment);
        checkSelectImage = false;

        if(!habitEvent.getImageFileName().equals("")){
            habitEventImage.setImageBitmap(bitmap);
        }

        habitEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        confirmDenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = enterComment.getText().toString();
                final String denoteDate = habitEvent.getDenoteDate();
                if(comment.length() > 20){
                    enterComment.setError("A comment cannot have more than 20 characters");
                }
                else{
                    HashMap<String, String> data = new HashMap<>();
                    data.put("Comment", comment);
                    data.put("Denote Date", denoteDate);
                    if(checkSelectImage){
                        String fileName = habitTitle + "_" + denoteDate;
                        data.put("Image File Name", fileName);
                        if(imageURI != null){
                            StorageReference imageRef = storageReference.child("images/" + fileName);
                            loadAnimation = new LoadingDialog(getContext());
                            loadAnimation.startLoadingDialog();
                            imageRef.putFile(imageURI)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            loadAnimation.dismissLoadingDialog();
                                            imageURI = null;
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
                    else{
                        data.put("Image File Name", "");
                    }
                    collectionReference1
                            .document(habitTitle)
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
                    dialog.dismiss();
                }
            }
        });

        cancelDenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Change the data list when a change occurred in the cloud
     */
    private void setCollectionReferenceAddSnapshotListener(){
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Habit Events");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                if (error!=null){
                    Log.d(TAG,"Error:"+error.getMessage());
                }
                else {
                    habitEventDataList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getData().get("Habit Events")));
                        String habitTitle = doc.getId();
                        // Log.d(TAG, "habit title is "+ habitTitle);
                        String denoteDate = (String) doc.getData().get("Denote Date");
                        // Log.d(TAG, "denote date is " + denoteDate);
                        HabitEvent habitEvent = new HabitEvent(habitTitle, denoteDate);
                        String comment = (String) doc.getData().get("Comment");
                        String imageFileName = (String) doc.getData().get("Image File Name");
                        habitEvent.setComment(comment);
                        habitEvent.setImageFileName(imageFileName);
                        habitEventDataList.add(habitEvent); // Adding the cities and provinces from FireStore
                    }
                    habitEventArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                }
            }
        });

    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 3 && data != null){
                imageURI = data.getData();
                habitEventImage.setImageURI(imageURI);
                checkSelectImage = true;
            }
        }
        if(resultCode == Activity.RESULT_CANCELED){
            checkSelectImage = true;
        }
    }
}