package com.example.habitshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsFragment extends Fragment {

    // View Variables
    View view;
    RecyclerView friendRecyclerView;
    FloatingActionButton addFriendButton;

    // Data Variables
    ArrayList<Friend> friendArrayList;
    CustomFriendListAdapter friendListAdapter;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendRecyclerView = view.findViewById(R.id.recycler_view_friends);
        addFriendButton = view.findViewById(R.id.button_add_friend);

        // initialize
        friendArrayList = new ArrayList<>();
        friendListAdapter = new CustomFriendListAdapter(getContext(), friendArrayList);
        friendRecyclerView.setAdapter(friendListAdapter);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        setCollectionReferenceAddSnapshotListener();

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchUser.class);
                startActivity(intent);
            }
        });

        friendListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Friend friend = friendArrayList.get(position);
                Intent intent = new Intent(getContext(), FriendHabitsActivity.class);
                intent.putExtra("friend_email", friend.getEmail());
                startActivity(intent);
            }
        });

        friendListAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                Friend friend = friendArrayList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to unfriend this user?\nUser Name: " + friend.getUserName() +"\n"+"Email: " + friend.getEmail())
                        .setPositiveButton("Unfriend", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete from the host's profile
                                final CollectionReference friendListReference = db.collection("UserData")
                                        .document(MainActivity.email)
                                        .collection("Friend List");
                                friendListReference.document(friend.getEmail()).delete();

                                // delete from the other user's profile
                                final CollectionReference friendListReference2 = db.collection("UserData")
                                        .document(friend.getEmail())
                                        .collection("Friend List");
                                friendListReference2.document(MainActivity.email).delete();
                            }
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

        return view;
    }

    private void setCollectionReferenceAddSnapshotListener(){
        //Log.d(TAG, "email is" + MainActivity.email);
        final CollectionReference collectionReference = db.collection("UserData")
                .document(MainActivity.email)
                .collection("Friend List");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                friendArrayList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc != null) {
                        String friendEmail = doc.getId();
                        final CollectionReference userDataCollectionReference = db.collection("UserData");
                        userDataCollectionReference.document(friendEmail)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            String friendName = document.getString("UserName");
                                            Friend friend = new Friend(friendName, friendEmail);
                                            friendArrayList.add(friend);
                                            friendListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}