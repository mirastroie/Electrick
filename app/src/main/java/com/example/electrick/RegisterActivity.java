package com.example.electrick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private AppCompatEditText emailTextView, passwordTextView;
    private AppCompatButton button;
    private FirebaseAuth mAuth;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        button = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progressBar);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("tag1","Clicked on sign up button!");
                signUp();
            }
        });
    }
    public void goToLogin(View view){
        Intent intent = new Intent(RegisterActivity.this,
                LoginActivity.class);
        startActivity(intent);
    }
    private void signUp() {
        // put the progressBar in loading state
        Log.i("tag2","In sign up function!");
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // The email and password are required
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                    "The email is required",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                    "The password is required",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // sign up the user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("myTag","Completed request!");
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Sign up successful!",
                                    Toast.LENGTH_LONG).show();
                            // hide the progress bar
                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(RegisterActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Sign up error. Try again later.",
                                    Toast.LENGTH_LONG).show();
                            // hide the progress bar
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}