package com.ac.mdbsocials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Math.toIntExact;

public class SocialsActivity extends AppCompatActivity {

    private ArrayList<Social> posts;
    private DatabaseReference ref;
    private SocialAdapter adapter;
    private String userID;
    private ArrayList<String> userRSVPList; // contains post ID that user rsvp to

    private boolean fetchedPosts = false; // determines whether posts were fetched
    private boolean fetchedRSVP = false; // whether rsvp were fetched -- only update once both are true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socials);

        userRSVPList = new ArrayList<>();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchData();

        FloatingActionButton addPost = findViewById(R.id.fab);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make new post page
                Intent i = new Intent(getApplicationContext(), AddSocial.class);
                startActivity(i);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new SocialAdapter(this, posts, userRSVPList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchData() {
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setTitle("Loading Posts");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.show();

        posts = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String id = postSnapshot.child("id").getValue().toString();
                    String eventName = postSnapshot.child("eventName").getValue().toString();
                    String photoURL = postSnapshot.child("photoURL").getValue().toString();
                    String email = postSnapshot.child("email").getValue().toString();
                    String eventDescription = postSnapshot.child("eventDescription").getValue().toString();
                    int rsvpNum = Integer.valueOf(postSnapshot.child("rsvpNum").getValue().toString());
                    int day = Integer.valueOf(postSnapshot.child("day").getValue().toString());
                    int month = Integer.valueOf(postSnapshot.child("month").getValue().toString());
                    int year = Integer.valueOf(postSnapshot.child("year").getValue().toString());
                    long timestamp = Long.valueOf(postSnapshot.child("timestamp").getValue().toString());
                    Social post = new Social(id, email, eventName,
                            eventDescription, photoURL,
                            rsvpNum, day, month, year, timestamp);
                    posts.add(post);
                }
                Collections.sort(posts, new Comparator<Social>() {
                    @Override
                    public int compare(Social o1, Social o2) {
                        return (int) (o2.timestamp - o1.timestamp);
                    }
                });

                fetchedPosts = true;
                if (fetchedRSVP) {
                    adapter.notifyDataSetChanged();
                }
                nDialog.hide();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LISTACT", "Error");
            }
        });

        DatabaseReference userRsvp = database.getReference("RSVP");
        userRsvp.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // data snapshot will contain post ID to post ID
                for (DataSnapshot rsvpSnapshot : dataSnapshot.getChildren()) {
                    userRSVPList.add((String) rsvpSnapshot.getValue());
                }
                System.out.println(userRSVPList);
                fetchedRSVP = true; // must do this way because you don't know which will return first, and only update once both are filled
                if (fetchedPosts) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LISTACT", "Error");
            }
        });

    }

}
