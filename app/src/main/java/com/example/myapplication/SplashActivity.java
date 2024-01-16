package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Add any additional setup code or delay if needed
        new Handler().postDelayed(() -> {
            checkUserLoginStatus();
        }, 3000); // Adjust the delay time as needed (in milliseconds)
    }

    private void checkUserLoginStatus() {
        // Check if the user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, go to MainActivity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // User is not logged in, go to LoginActivity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish(); // Finish the SplashActivity to prevent it from being in the back stack
    }
}
