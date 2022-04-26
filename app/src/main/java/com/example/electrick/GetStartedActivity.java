package com.example.electrick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(GetStartedActivity.this,
                RegisterActivity.class);
        startActivity(intent);
    }
}