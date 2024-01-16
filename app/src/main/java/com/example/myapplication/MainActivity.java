package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private LinearLayout dashboard;
    private LinearLayout logout;
    private LinearLayout settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dashboard = findViewById(R.id.dashboard);
        settings = findViewById(R.id.settings);
        logout = findViewById(R.id.logout);


        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDashboardClicked();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingsClicked();
            }
        });




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        checkUserBlockStatus();

    }


    public void onDashboardClicked() {
        // Launch DashboardActivity
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    public void onSettingsClicked() {
        // Launch SettingsActivity
        Intent intent = new Intent(MainActivity.this, UserListActivity.class);
        startActivity(intent);
    }




    private void logout() {
        FirebaseAuth.getInstance().signOut();

        // Redirect to login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void checkUserBlockStatus() {
        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference("users").child(FirebaseAuth.getInstance().getUid());

        // Add a ValueEventListener to monitor changes in the user's data
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Check if the user is blocked
                if (snapshot.exists() && snapshot.hasChild("isBlock")) {
                    boolean isBlock = (boolean) snapshot.child("isBlock").getValue();
                    if (isBlock == true) {
                        // If user is blocked, log out the user
                        FirebaseAuth.getInstance().signOut();


                        // Redirect to login activity
                        Intent intent = new Intent(MainActivity.this, reviewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error if needed
            }
        });
    }





}



