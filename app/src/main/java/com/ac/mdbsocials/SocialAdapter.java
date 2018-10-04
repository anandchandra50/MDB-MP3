package com.ac.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.SocialHolder> {
    ArrayList<Social> posts;
    ArrayList<String> userRSVPList;
    Context context;

    public SocialAdapter (Context context, ArrayList<Social> posts, ArrayList<String> userRSVPList) {
        this.context = context;
        this.posts = posts;
        this.userRSVPList = userRSVPList;
    }

    @NonNull
    @Override
    public SocialHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_social_card, viewGroup, false);
        return new SocialHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SocialHolder socialHolder, int i) {
        // fill image and texts
        Social post = posts.get(i);
//        Glide.with(socialHolder.imageView.getContext()).load(post.photoURL).into(socialHolder.imageView);
        new Utils.FetchImage() {
            @Override public void onPostExecute(Bitmap result) {
                socialHolder.imageView.setImageBitmap(result);
            }
        }.execute(post.photoURL);

        socialHolder.eventName.setText(post.eventName);
        socialHolder.eventPosterEmail.setText(post.email);
        String rsvpStatus = "RSVP: " + Integer.toString(post.rsvpNum);
        String date = post.month + "/" + post.day + "/" + post.year;
        socialHolder.eventDate.setText(date);
        if (userRSVPList.contains(post.id)) {
            rsvpStatus = rsvpStatus + " (going)";
        }
        socialHolder.rsvpNum.setText(rsvpStatus);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class SocialHolder extends RecyclerView.ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView imageView;
        TextView eventName;
        TextView eventPosterEmail;
        TextView rsvpNum;
        TextView eventDate;
        public SocialHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.eventImage);
            eventName = itemView.findViewById(R.id.eventName);
            eventPosterEmail = itemView.findViewById(R.id.eventPosterEmail);
            rsvpNum = itemView.findViewById(R.id.eventRSVPNum);
            eventDate = itemView.findViewById(R.id.eventDateText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, EventDetailsActivity.class);
                    i.putExtra("post", posts.get(getAdapterPosition()));
                    i.putExtra("user_rsvp", userRSVPList.contains(posts.get(getAdapterPosition()).id));
                    i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }
    }

}
