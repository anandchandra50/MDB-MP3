package com.ac.mdbsocials;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private EditText emailText;
    private EditText passText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check login from Firebase
        mAuth = FirebaseAuth.getInstance();

        // init UI elements
        Button createUser = findViewById(R.id.signupButton);
        createUser.setOnClickListener(this);

        Button loginUser = findViewById(R.id.loginButton);
        loginUser.setOnClickListener(this);
        
        emailText = findViewById(R.id.loginEmailText);
        passText = findViewById(R.id.loginPasswordText);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // user exists, move to socials screen
            Intent i = new Intent(getApplicationContext(), SocialsActivity.class);
            startActivity(i);
        }

    }

    /**
     * Attempt sign in
     * @param email
     * @param pass
     */
    private void signIn(String email, String pass) {
        // check to make sure email and pass are valid first
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // transition to socials page
                    Intent i = new Intent(getApplicationContext(), SocialsActivity.class);
                    startActivity(i);
                } else {
                    // display toast error
                    Utils.displayError(getApplicationContext(), getString(R.string.login_failed));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loginButton:
                // check to make sure email and pass are valid
                String email = emailText.getText().toString();
                String pass = passText.getText().toString();
                // perform checks here to minimize calls of sign in
                if (Utils.isValid(getApplicationContext(), email, pass)) {
                    // attempt sign in
                    signIn(email, pass);
                }
                break;
            case R.id.signupButton:
                // transition to create user screen
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
                break;
        }
    }
}
