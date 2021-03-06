package com.ac.mdbsocials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EventDetailsActivity";
    private boolean user_did_rsvp;
    private Social post;
    private DatabaseReference ref;
    private DatabaseReference rsvpRef;
    private String userID;
    private TextView hasRSVP;
    private Button rsvpButton;
    private TextView rsvpNum;
    private int currentRSVP;
    private OnCompleteListener<Void> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // init data
        Intent intent = getIntent();
        post = (Social) intent.getSerializableExtra("post");
        user_did_rsvp = intent.getExtras().getBoolean("user_rsvp");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference();
        rsvpRef = ref.child("posts").child(post.id).child("rsvpNum");
        listenToRSVPStatus();

        // fill in UI
        TextView postName = findViewById(R.id.detailsName);
        TextView posterEmail = findViewById(R.id.detailsPosterEmail);
        TextView date = findViewById(R.id.detailsDate);
        final de.hdodenhof.circleimageview.CircleImageView image = findViewById(R.id.detailsEventImage);
        rsvpNum = findViewById(R.id.detailsRSVP);
        hasRSVP = findViewById(R.id.detailsHasRSVP);
        TextView description = findViewById(R.id.detailsEventDesc);
        rsvpButton = findViewById(R.id.detailsRSVPButton);

        postName.setText(post.eventName);
        posterEmail.setText(post.email);
//        Glide.with(image.getContext()).load(post.photoURL).into(image);
        new Utils.FetchImage() {
            @Override public void onPostExecute(Bitmap result) {
                image.setImageBitmap(result);
            }
        }.execute(post.photoURL);

        currentRSVP = post.rsvpNum;
        String rsvpNumString = "RSVP: " + Integer.toString(currentRSVP);
        rsvpNum.setText(rsvpNumString);
        String dateText = post.month + "/" + post.day + "/" + post.year;
        date.setText(dateText);

        description.setText(post.eventDescription);

        if (user_did_rsvp) {
            // user already rsvped -- make that clear
            hasRSVP.setText(R.string.are_going);
            rsvpButton.setText(R.string.cancel_rsvp);
        } else {
            hasRSVP.setText(R.string.not_going);
            rsvpButton.setText(R.string.rsvp);
        }
        callback = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user_did_rsvp = !user_did_rsvp;
                    updateRSVPStatus();
                } else {
                    Utils.displayError(getApplicationContext(), getString(R.string.rsvp_error));
                }
            }
        };

        rsvpButton.setOnClickListener(this);
    }

    /**
     * Listen to RSVP number for post and update UI if it changes while user is viewing
     */
    private void listenToRSVPStatus() {
        rsvpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // value is fetched as an int
                currentRSVP = Integer.valueOf(dataSnapshot.getValue().toString());
                String rsvpNumString = "RSVP: " + Integer.toString(currentRSVP);
                rsvpNum.setText(rsvpNumString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Update RSVP status -- flip to RSVP if not already, vice versa
     */
    private void updateRSVPStatus() {
        if (user_did_rsvp) {
            // user already rsvped -- make that clear
            hasRSVP.setText(R.string.are_going);
            rsvpButton.setText(R.string.cancel_rsvp);
        } else {
            hasRSVP.setText(R.string.not_going);
            rsvpButton.setText(R.string.rsvp);
        }

        rsvpRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Object rsvpNumObject = mutableData.getValue();
                if (rsvpNumObject == null) {
                    return Transaction.success(mutableData);
                }
                int rsvpNum = Integer.valueOf(rsvpNumObject.toString());
                if (user_did_rsvp) {
                    // just did rsvp, so add one
                    currentRSVP = rsvpNum + 1;
                } else {
                    currentRSVP = rsvpNum - 1;
                }
                mutableData.setValue(currentRSVP);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    // success
                    String rsvpNumString = "RSVP: " + Integer.toString(currentRSVP);
                    rsvpNum.setText(rsvpNumString);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detailsRSVPButton:
                if (user_did_rsvp) {
                    // cancel rsvp
                    ref.child("RSVP").child(userID).child(post.id).removeValue().addOnCompleteListener(callback);
                } else {
                    // make rsvp
                    ref.child("RSVP").child(userID).child(post.id).setValue(post.id).addOnCompleteListener(callback);
                }
                break;
        }
    }
}
