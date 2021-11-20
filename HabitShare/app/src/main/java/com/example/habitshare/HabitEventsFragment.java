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
    private String habitTitle;
    private boolean checkSelectImage;
    private Uri imageURI = null;
    private File imageFile;
    RecyclerView habitEventRecyclerView;
    ImageView habitEventImage;
    FirebaseFirestore db;
    Habit habit;
    String currentDate;
    String currentPhotoPath;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    LoadingDialog loadAnimation;
    Bitmap bitmap;
    boolean selectFromGallery;
    boolean selectFromCamera;
    int i;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_habit_events, container, false);
        habitEventRecyclerView = view.findViewById(R.id.list_habit_events);
        habitEventDataList = new ArrayList<>();
        habitEventArrayAdapter = new CustomHabitEventListAdapter(getContext(), habitEventDataList);
        habitEventRecyclerView.setAdapter(habitEventArrayAdapter);
        habitEventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        habitEventArrayAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                i = position;
                HabitEvent habitEvent = habitEventDataList.get(i);
                Intent intent = new Intent(getContext(), ViewHabitEventActivity.class);
                intent.putExtra("habit_title", habitEvent.getTitle());
                intent.putExtra("comment", habitEvent.getComment());
                intent.putExtra("denote_date", habitEvent.getDenoteDate());
                intent.putExtra("event_title", habitEvent.getEvenTitle());
                intent.putExtra("location", habitEvent.getLocation());

                    try{
                        if(habitEvent.isHasImage()) {
                            loadAnimation = new LoadingDialog(getContext());
                            loadAnimation.startLoadingDialog();
                            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            imageFile = File.createTempFile(habitEvent.getEvenTitle(), "", storageDir);
                            storageReference.child("images/" + habitEvent.getEvenTitle()).getFile(imageFile)
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
        });

        return view;
    }

//    /**
//     * start a view habit event dialog
//     */
//    private void showViewHabitEventDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.view_habit_event_layout);
//        CollectionReference collectionReference = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habit Events");
//        CollectionReference collectionReference2 = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habits");
//
//        HabitEvent habitEvent = habitEventDataList.get(i);
//
//        TextView viewHabitEventTitle = dialog.findViewById(R.id.view_habit_event_title);
//        TextView viewHabitEventComment = dialog.findViewById(R.id.view_habit_event_comment);
//        TextView viewHabitEventDate = dialog.findViewById(R.id.view_habit_event_date);
//        Button buttonViewHabitEventEdit = dialog.findViewById(R.id.edit_habit_event);
//        Button buttonViewHabitEventDelete = dialog.findViewById(R.id.delete_habit_event);
//        Button buttonViewHabitEventCancel = dialog.findViewById(R.id.cancel_habit_event);
//        ImageView viewHabitEventImage = dialog.findViewById(R.id.view_habit_event_image);
//
//
//
//        if(!habitEvent.getEvenTitle().equals("")){
//            imageURI = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", imageFile);
//            viewHabitEventImage.setImageURI(imageURI);
//            viewHabitEventImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    viewImage();
//                }
//            });
//        }
//        habitTitle = habitEvent.getTitle();
//        viewHabitEventTitle.setText(habitTitle);
//        if(habitEvent.getComment().equals("")){
//            viewHabitEventComment.setText("(No comment)");
//            viewHabitEventComment.setTextColor(Color.GRAY);
//        }
//        else{
//            viewHabitEventComment.setText(habitEvent.getComment());
//        }
//        viewHabitEventDate.setText(habitEvent.getDenoteDate());
//
//        buttonViewHabitEventEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                Intent intent = new Intent(getContext(), DenoteHabitActivity.class);
//                intent.putExtra("request_code", 1);
//                if(imageURI != null){
//                    intent.putExtra("image_uri", imageURI.toString());
//                }
//                intent.putExtra("habit_title", habitEvent.getTitle());
//                intent.putExtra("comment", habitEvent.getComment());
//                intent.putExtra("denote_date", habitEvent.getDenoteDate());
//                imageURI = null;
//                startActivity(intent);
//            }
//        });
//
//        buttonViewHabitEventDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String habitTitle = habitEvent.getTitle();
//                final StorageReference imageRef= storageReference.child("images/" + habitEvent.getEvenTitle());
//                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//                // remove the habit event
//                collectionReference
//                        .document(habitTitle)
//                        .delete();
//
//                // set the status of the habit to Not Done
//                collectionReference2.document(habitTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot doc = task.getResult();
//                            if(doc.exists()){
//                                String lastTimeDenoted = (String) doc.getData().get("Last Time Denoted");
//                                if(lastTimeDenoted.equals(habitEvent.getDenoteDate())){
//                                    String habitTitle = doc.getId();
//                                    String date = (String) doc.getData().get("Date of Start");
//                                    String daysOfWeek = (String) doc.getData().get("Days of Week");
//                                    String reason = (String) doc.getData().get("Reason");
//                                    HashMap<String, String> data = new HashMap<>();
//                                    data.put("Date of Start", date);
//                                    data.put("Reason", reason);
//                                    data.put("Days of Week", daysOfWeek);
//                                    data.put("Status", "Not Done");
//                                    data.put("Last Time Denoted", "");
//
//                                    collectionReference2.document(habitTitle)
//                                            .set(data)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    Log.d(TAG, "Data has been added successfully!");
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Log.d(TAG, "Data could not be added!" + e.toString());
//                                                }
//                                            });
//                                }
//
//                            }
//                        }
//                    }
//                });
//                dialog.dismiss();
//                imageURI = null;
//            }
//        });
//
//        buttonViewHabitEventCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                imageURI = null;
//            }
//
//        });
//
//        dialog.show();
//    }

//    /**
//     * start an edit habit event dialog
//     */
//    private void showEditHabitEventDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.add_habit_event_layout);
//
//        HabitEvent habitEvent = habitEventDataList.get(i);
//        habitTitle = habitEvent.getTitle();
//        String comment = habitEvent.getComment();
//        String denoteDate = habitEvent.getDenoteDate();
//
//        final CollectionReference collectionReference1 = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habit Events");
//        final CollectionReference collectionReference2 = db.collection("UserData")
//                .document(MainActivity.email)
//                .collection("Habits");
//
//
//        TextView denoteHabitName = dialog.findViewById(R.id.denote_habit_name);
//        EditText enterComment = dialog.findViewById(R.id.denote_habit_comment);
//        Button confirmDenote = dialog.findViewById(R.id.denote_habit_confirm_button);
//        Button cancelDenote = dialog.findViewById(R.id.denote_habit_cancel_button);
//        habitEventImage = dialog.findViewById(R.id.habit_event_image);
//
//        denoteHabitName.setText(habitTitle);
//        enterComment.setText(comment);
//        checkSelectImage = false;
//
//        if(!habitEvent.getImageFileName().equals("")){
//            habitEventImage.setImageURI(imageURI);
//        }
//
//        habitEventImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImageDialog();
//            }
//        });
//
//        confirmDenote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String comment = enterComment.getText().toString();
//                final String denoteDate = habitEvent.getDenoteDate();
//                if(comment.length() > 20){
//                    enterComment.setError("A comment cannot have more than 20 characters");
//                }
//                else{
//                    HashMap<String, String> data = new HashMap<>();
//                    data.put("Comment", comment);
//                    data.put("Denote Date", denoteDate);
//                    if(checkSelectImage){
//                        String fileName = habitTitle + "_" + denoteDate;
//                        data.put("Image File Name", fileName);
//                        if(imageURI != null){
//                            StorageReference imageRef = storageReference.child("images/" + fileName);
//                            loadAnimation = new LoadingDialog(getContext());
//                            loadAnimation.startLoadingDialog();
//                            imageRef.putFile(imageURI)
//                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                            loadAnimation.dismissLoadingDialog();
//                                            imageURI = null;
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            loadAnimation.dismissLoadingDialog();
//                                        }
//                                    });
//                        }
//                    }
//                    else{
//                        data.put("Image File Name", "");
//                    }
//                    collectionReference1
//                            .document(habitTitle)
//                            .set(data)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d(TAG, "Data has been added successfully!");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "Data could not be added!" + e.toString());
//                                }
//                            });
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        cancelDenote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

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
                        String eventTitle = doc.getId();
                        String habitTitle = (String) doc.getData().get("Habit Title");
                        String denoteDate = (String) doc.getData().get("Denote Date");
                        HabitEvent habitEvent = new HabitEvent(habitTitle, denoteDate);
                        String comment = (String) doc.getData().get("Comment");
                        String location = (String) doc.getData().get("Location");
                        Boolean isHasImage = (Boolean) doc.getData().get("Has Image");
                        habitEvent.setLocation(location);
                        habitEvent.setComment(comment);
                        habitEvent.setEvenTitle(eventTitle);
                        habitEvent.setTitle(habitTitle);
                        habitEvent.setHasImage(isHasImage);
                        habitEventDataList.add(habitEvent); // Adding the cities and provinces from FireStore
                    }
                    habitEventArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                }
            }
        });

    }
//    private void selectImageDialog(){
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.select_image_source);
//
//        ImageView gallery = dialog.findViewById(R.id.imageview_gallery);
//        ImageView camera = dialog.findViewById(R.id.imageview_camera);
//
//        selectFromGallery = false;
//        selectFromCamera = false;
//
//        gallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, REQUEST_GALLERY);
//            }
//        });
//
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                dispatchTakePictureIntent();
//            }
//        });
//
//        dialog.show();
//    }

    private void viewImage(){
        Intent intent = new Intent(getContext(), ViewLargerImageActivity.class);
        intent.putExtra("image_uri", imageURI.toString());
        startActivity(intent);
    }

//    private File createImageFile() throws IOException {
//        // Obtained From Google Android Studio Guides
//
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                imageURI = FileProvider.getUriForFile(getContext(),
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
//                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
//            }
//        }
//    }

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