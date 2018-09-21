package com.ac.mdbsocials;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.SocialHolder> {
    ArrayList<Post> posts;
    Context context;

    public SocialAdapter (Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public SocialHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_social_card, viewGroup, false);
        return new SocialHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialHolder socialHolder, int i) {
        // fill image and texts
        Glide.with(socialHolder.imageView.getContext()).load(posts.get(i).imageURL).into(socialHolder.imageView);
        AssetManager am = context.getApplicationContext().getAssets();
        Typeface proxima = Typeface.createFromAsset(am,  "fonts/ProximaNova-Regular.otf");
        socialHolder.posterEmail.setTypeface(proxima);
        socialHolder.posterCaption.setTypeface(proxima);
        socialHolder.posterEmail.setText(posts.get(i).posterEmail);
        socialHolder.posterCaption.setText(posts.get(i).posterCaption);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class SocialHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView posterEmail;
        TextView posterCaption;
        public SocialHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.postImage);
            posterEmail = itemView.findViewById(R.id.posterEmail);
            posterCaption = itemView.findViewById(R.id.posterCaption);
        }
    }

}
