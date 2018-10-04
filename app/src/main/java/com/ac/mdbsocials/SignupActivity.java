package com.ac.mdbsocials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.ac.mdbsocials.Utils.displayError;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button createUser;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // init firebase auth

        // init UI elements
        mAuth = FirebaseAuth.getInstance();
        createUser = findViewById(R.id.createUserButton);
        emailText = findViewById(R.id.emailTextView);
        passwordText = findViewById(R.id.passwordTextView);
        confirmPasswordText = findViewById(R.id.confirmPasswordTextView);
        createUser.setOnClickListener(this);
    }


    /**
     * Creates a user with given email and password
     * @param email
     * @param password
     */
    private void createUser(String email, String password) {
        // dialog -- smoother UI experience
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setTitle("Creating Account");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.show();

        // create user in firebase
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // go to main socials page
                    Intent i = new Intent(getApplicationContext(), SocialsActivity.class);
                    startActivity(i);
                } else {
                    // register failed
                    // display toast
                    displayError(getApplicationContext(), getString(R.string.registration_failed));
                }
                nDialog.hide();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createUserButton:
                // attempt sign in
                String email = emailText.getText().toString();
                String pass = passwordText.getText().toString();
                String confirmPass = confirmPasswordText.getText().toString();

                // check to make sure email and pass work
                if (Utils.isValid(getApplicationContext(), email, pass, confirmPass)) {
                    createUser(email, pass);
                }
        }
    }
}
