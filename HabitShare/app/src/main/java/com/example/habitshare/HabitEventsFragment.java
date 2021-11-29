package com.example.habitshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class HabitEventsFragment extends Fragment {

    private ArrayList<HabitEvent> habitEventDataList;
    private CustomHabitEventListAdapter habitEventArrayAdapter;
    private final static String TAG = "Habit Events";
    private final static int REQUEST_GALLERY = 2;
    private final static int REQUEST_CAMERA = 3;
    private boolean checkSelectImage;
    private Uri imageURI = null;
    private File imageFile;
    RecyclerView habitEventRecyclerView;
    ImageView habitEventImage;
    FirebaseFirestore db;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    LoadingDialog loadAnimation;
    Bitmap bitmap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_events, container, false);

        // preparation for recyclerview
        habitEventRecyclerView = view.findViewById(R.id.list_habit_events);
        habitEventDataList = new ArrayList<>();
        habitEventArrayAdapter = new CustomHabitEventListAdapter(getContext(), habitEventDataList);
        habitEventRecyclerView.setAdapter(habitEventArrayAdapter);
        habitEventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // preparation for FireStore
        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        habitEventArrayAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HabitEvent habitEvent = habitEventDataList.get(position);
                startViewHabitEventActivity(habitEvent);
                }
        });

        return view;
    }

    /**
     * Send the data of the habit event to the ViewHabitEventActivity and then start the ViewHabitEventActivity
     * @param habitEvent user selected habit event
     */
    private void startViewHabitEventActivity(HabitEvent habitEvent){
        Intent intent = new Intent(getContext(), ViewHabitEventActivity.class);
        intent.putExtra("habit_title", habitEvent.getTitle());
        intent.putExtra("comment", habitEvent.getComment());
        intent.putExtra("denote_date", habitEvent.getDenoteDate());
        intent.putExtra("event_title", habitEvent.getEventTitle());
        intent.putExtra("location", habitEvent.getLocation());
        try{
            if(habitEvent.isHasImage()) {
                loadAnimation = new LoadingDialog(getContext());
                loadAnimation.startLoadingDialog();
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                imageFile = File.createTempFile(habitEvent.getEventTitle(), "", storageDir);
                storageReference.child("images/" + habitEvent.getEventTitle()).getFile(imageFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                imageURI = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", imageFile);
                                intent.putExtra("image_uri", imageURI.toString());
                                loadAnimation.dismissLoadingDialog();
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadAnimation.dismissLoadingDialog();
                            }
                        });
            }
            else{
                startActivity(intent);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
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
                        // get data from FireStore document
                        String eventTitle = doc.getId();
                        String habitTitle = (String) doc.getData().get("Habit Title");
                        String denoteDate = (String) doc.getData().get("Denote Date");
                        String comment = (String) doc.getData().get("Comment");
                        String location = (String) doc.getData().get("Location");
                        Boolean isHasImage = (Boolean) doc.getData().get("Has Image");

                        // set values to the habit event object and add it to the ArrayList
                        HabitEvent habitEvent = new HabitEvent(habitTitle, denoteDate);
                        habitEvent.setLocation(location);
                        habitEvent.setComment(comment);
                        habitEvent.setEventTitle(eventTitle);
                        habitEvent.setTitle(habitTitle);
                        habitEvent.setHasImage(isHasImage);
                        habitEventDataList.add(habitEvent); // Adding the cities and provinces from FireStore
                    }
                    habitEventArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                }
            }
        });

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
            if(requestCode == REQUEST_GALLERY && data != null){
                imageURI = data.getData();
                habitEventImage.setImageURI(imageURI);
                checkSelectImage = true;
            }
            if(requestCode == REQUEST_CAMERA){
                if(imageURI != null){
                    habitEventImage.setImageURI(imageURI);
                    checkSelectImage = true;
                }
            }
        }
        if(resultCode == Activity.RESULT_CANCELED){
            checkSelectImage = true;
        }
    }
}