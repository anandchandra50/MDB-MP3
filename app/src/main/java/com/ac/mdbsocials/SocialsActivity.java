package com.ac.mdbsocials;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class SocialsActivity extends AppCompatActivity {

    private ArrayList<Social> posts;
    private DatabaseReference ref;
    private SocialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socials);

//        fetchData();

        addTestData();

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
        adapter = new SocialAdapter(this, posts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchData() {
        posts = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    String posterEmail = postSnapshot.child("posterEmail").getValue().toString();
//                    String posterCaption = postSnapshot.child("posterCaption").getValue().toString();
//                    String imageURL = postSnapshot.child("imageURL").getValue().toString();
//                    Post post = new Post(posterEmail, posterCaption, imageURL);
//                    posts.add(post);
                }
                Collections.reverse(posts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LISTACT", "Error");
            }
        });
    }

    private void addTestData() {
        posts = new ArrayList<>();
        Social a = new Social("alsdkfj@gmail.com",
                "test event",
                "test event description",
                "https://www.planwallpaper.com/static/images/abstract-colourful-cool-wallpapers-55ec7905a6a4f.jpg",
                20);
        posts.add(a);
        posts.add(a);
    }
}
