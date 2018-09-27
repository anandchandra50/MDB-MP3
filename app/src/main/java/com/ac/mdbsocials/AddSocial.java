package com.ac.mdbsocials;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class AddSocial extends AppCompatActivity {

    private de.hdodenhof.circleimageview.CircleImageView uploadImage;
    private EditText eventName;
    private EditText eventDescription;
    private Button createPostButton;
    private Switch goingSwitch;
    private Button eventDateButton;

    private boolean selectedPhoto = false;
    private boolean userGoing = true;
    private int eventMonth = -1;
    private int eventDay = -1;
    private int eventYear = -1;
    private DatePickerDialog.OnDateSetListener dateListener;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_social);

        uploadImage = findViewById(R.id.createEventImage);
        eventName = findViewById(R.id.createEventName);
        eventDescription = findViewById(R.id.createEventDesc);
        createPostButton = findViewById(R.id.createEventButton);
        goingSwitch = findViewById(R.id.addPostGoingSwitch);
        eventDateButton = findViewById(R.id.addPostSelectDate);

        goingSwitch.setChecked(userGoing);
        goingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userGoing = isChecked;
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clicked on image
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = Integer.toString(month + 1) + "/" + Integer.toString(dayOfMonth) + "/" + Integer.toString(year);
                eventYear = year;
                eventMonth = month + 1;
                eventDay = dayOfMonth;
                eventDateButton.setText(date);
            }
        };

        eventDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddSocial.this,
                        R.style.Theme_AppCompat_Light_Dialog_Alert,
                        dateListener,
                        year, month, day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedPhoto) {
                    Utils.displayError(getApplicationContext(), getString(R.string.upload_image));
                } else if (eventName.getText().toString().isEmpty()) {
                    Utils.displayError(getApplicationContext(), getString(R.string.enter_event_name));
                } else if (eventDescription.getText().toString().isEmpty()) {
                    Utils.displayError(getApplicationContext(), getString(R.string.event_description));
                } else if (eventDay == -1 || eventMonth == -1 || eventYear == -1) {
                    Utils.displayError(getApplicationContext(), getString(R.string.enter_date));
                } else {
                    // everything is there
                    uploadPost();
                }
            }
        });

    }

    private void uploadPost() {
        // write to database and to storage
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("posts");
        final DatabaseReference rsvpRef = database.getReference("RSVP");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("images");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String userID = currentUser.getUid();
        if (currentUser == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            return;
        }
        final String postID = databaseRef.push().getKey();
        final String userEmail = currentUser.getEmail();
        final String name = eventName.getText().toString();
        final String description = eventDescription.getText().toString();

        storageRef.child(postID).putFile(imageURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // upload to database
                        Uri photoURL = taskSnapshot.getDownloadUrl();
                        Date current = new Date();
                        int rsvpNum = userGoing ? 1 : 0;
                        Social post = new Social(postID, userEmail, name, description,
                                photoURL.toString(),
                                rsvpNum, eventDay, eventMonth, eventYear, current.getTime());
                        if (userGoing) {
                            rsvpRef.child(userID).child(postID).setValue(postID);
                        }
                        databaseRef.child(postID).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) try {
            imageURI = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageURI);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            uploadImage.setImageBitmap(selectedImage);
            selectedPhoto = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}
