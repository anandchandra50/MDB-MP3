package com.ac.mdbsocials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class SocialsActivity extends AppCompatActivity {

    private ArrayList<Social> socials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socials);

        addTestData();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        SocialAdapter adapter = new SocialAdapter(this, socials);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addTestData() {
        socials = new ArrayList<>();
        Social a = new Social("my event", "https://www.planwallpaper.com/static/images/abstract-colourful-cool-wallpapers-55ec7905a6a4f.jpg",
                "test@gmail.com", 10);
        Social b = new Social("my second", "https://www.planwallpaper.com/static/images/abstract-colourful-cool-wallpapers-55ec7905a6a4f.jpg",
                "test2@gmail.com", 11);
        Social c = new Social("my third", "https://www.planwallpaper.com/static/images/abstract-colourful-cool-wallpapers-55ec7905a6a4f.jpg",
                "test3@gmail.com", 12);
        socials.add(a);
        socials.add(b);
        socials.add(c);
    }
}
