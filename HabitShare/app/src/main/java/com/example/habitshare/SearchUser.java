package com.example.habitshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SearchUser extends AppCompatActivity {

    // View Variables
    SearchView searchUser;
    RecyclerView recyclerViewSearchResult;
    ProgressBar progressBar;

    // Data Variables;
    ArrayList<Friend> resultArrayList;
    CustomFriendListAdapter resultListAdapter;
    FirebaseFirestore db;
    String hostUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        searchUser = findViewById(R.id.search_user);
        recyclerViewSearchResult = findViewById(R.id.recyclerview_search_result);
        progressBar = findViewById(R.id.search_progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        resultArrayList = new ArrayList<>();
        resultListAdapter = new CustomFriendListAdapter(SearchUser.this, resultArrayList);
        recyclerViewSearchResult.setAdapter(resultListAdapter);
        recyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(SearchUser.this));

        db = FirebaseFirestore.getInstance();
        final CollectionReference userDataReference = db.collection("UserData");

        searchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                resultArrayList.clear();
                recyclerViewSearchResult.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                userDataReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                String userEmail = doc.getId();
                                String userName = (String) doc.getData().get("UserName");
                                if((query.equals(userEmail) || query.equals(userName) || (query.length() > 2 & userName.contains(query))) & !userEmail.equals(MainActivity.email)){
                                    Friend friend = new Friend(userName, userEmail);
                                    Log.d("Search: ", "passed");
                                    resultArrayList.add(friend);
                                }
                            }
                        }
                        resultListAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerViewSearchResult.setVisibility(View.VISIBLE);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        resultListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Friend friend = resultArrayList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchUser.this);

                builder.setMessage("Do you want to add " + friend.getUserName() + " as your friend?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final CollectionReference friendRequestReference = db.collection("UserData")
                                        .document(friend.getEmail())
                                        .collection("Friend Requests");

                                HashMap<String, String> data = new HashMap<>();
                                data.put("UserName", MainActivity.userName);

                                friendRequestReference.document(MainActivity.email)
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
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}