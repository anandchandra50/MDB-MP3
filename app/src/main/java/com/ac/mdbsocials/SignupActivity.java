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

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        Button createUser = findViewById(R.id.createUserButton);
        final EditText emailText = findViewById(R.id.emailTextView);
        final EditText passwordText = findViewById(R.id.passwordTextView);
        final EditText confirmPasswordText = findViewById(R.id.confirmPasswordTextView);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // attempt sign in
                // check to make sure email and pass work
                String email = emailText.getText().toString();
                String pass = passwordText.getText().toString();
                String confirmPass = confirmPasswordText.getText().toString();

                if (Utils.isValid(getApplicationContext(), email, pass, confirmPass)) {
                    createUser(email, pass);
                }

            }
        });
    }

    private void createUser(String email, String password) {
        // create user with given email and pass
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setTitle("Creating Account");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.show();

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





}
