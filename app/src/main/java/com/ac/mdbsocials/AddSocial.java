package com.ac.mdbsocials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AddSocial extends AppCompatActivity {

    private ImageView uploadImage;
    private EditText eventName;
    private EditText eventDescription;
    private Button createPostButton;
    private boolean selectedPhoto = false;

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

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clicked on image
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedPhoto) {
                    Toast.makeText(getApplicationContext(), "Please Upload an Image", Toast.LENGTH_LONG).show();
                } else {
                    uploadPicture();
                }
            }
        });
    }

    private void uploadPicture() {
        // write to database and to storage
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("posts");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("images");
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
                        Social post = new Social(userEmail, name, description, photoURL.toString(), 0);
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

        databaseRef.child(postID);
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
