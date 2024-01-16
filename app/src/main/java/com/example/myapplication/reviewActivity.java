package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class reviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accoununderreview);

        // Add any additional setup code or delay if needed
        new Handler().postDelayed(() -> {
            // Start the main activity or any other activity after the splash screen
            Intent intent = new Intent(reviewActivity.this, LoginActivity.class);
            startActivity(intent);
            FirebaseAuth.getInstance().signOut();
            finish();

        }, 3000); // Adjust the delay time as needed (in milliseconds)
    }
}
