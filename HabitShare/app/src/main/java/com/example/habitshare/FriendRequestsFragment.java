package com.example.habitshare;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class FriendRequestsFragment extends Fragment {

    // View Variables
    View view;
    RecyclerView recyclerViewFriendRequests;

    // Data Variables
    ArrayList<Friend> requestList;
    CustomFriendListAdapter requestAdapter;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        recyclerViewFriendRequests = view.findViewById(R.id.recyclerview_friend_requests);

        // initialize
        requestList = new ArrayList<>();
        requestAdapter = new CustomFriendListAdapter(getContext(), requestList);
        recyclerViewFriendRequests.setAdapter(requestAdapter);
        recyclerViewFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        requestAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Friend friend = requestList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(friend.getUserName() + " wants to be your friend")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // add friend to the sender's friend list
                                final CollectionReference senderFriendListReference = db.collection("UserData")
                                        .document(friend.getEmail())
                                        .collection("Friend List");
                                HashMap<String, String> data = new HashMap<>();
                                data.put("UserName", MainActivity.userName);
                                senderFriendListReference.document(MainActivity.email)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                                // add friend to the receiver's friend list
                                data.clear();
                                data.put("UserName", friend.getUserName());
                                final CollectionReference receiverFriendListReference = db.collection("UserData")
                                        .document(MainActivity.email)
                                        .collection("Friend List");
                                receiverFriendListReference.document(friend.getEmail())
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                                // delete the friend request
                                final CollectionReference friendRequestReference = db.collection("UserData")
                                        .document(MainActivity.email)
                                        .collection("Friend Requests");
                                friendRequestReference.document(friend.getEmail()).delete();
                            }
                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete the friend request
                                final CollectionReference friendRequestReference = db.collection("UserData")
                                        .document(MainActivity.email)
                                        .collection("Friend Requests");
                                friendRequestReference.document(friend.getEmail()).delete();
                            }
                        })
                        .setNeutralButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }
    private void setCollectionReferenceAddSnapshotListener(){
        //Log.d(TAG, "email is" + MainActivity.email);
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Friend Requests");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                requestList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc != null) {
                        String friendEmail = doc.getId();
                        String friendName = (String) doc.getData().get("UserName");
                        Friend friend = new Friend(friendName, friendEmail);
                        requestList.add(friend);
                    }
                }
                requestAdapter.notifyDataSetChanged();
            }
        });
    }

}