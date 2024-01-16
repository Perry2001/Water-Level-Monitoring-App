package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etPassword;
    private Button btnSignup;
    private TextView btnLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.tv_login);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));

            }
        });


    }

    private void signUp() {
        final String name = etName.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        // Check if any field is blank
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the email is valid
        if (!isValidEmail(email)) {
            Toast.makeText(SignupActivity.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email is already registered
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                                // Email is already registered
                                Toast.makeText(SignupActivity.this, "Email is already registered.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Email is not registered, proceed with user registration
                                createUserWithEmailAndPassword(name, phone, email, password);

                            }
                        } else {
                            // Error occurred while checking email registration
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(SignupActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void createUserWithEmailAndPassword(String name, String phone, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registration successful
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserRef = mDatabase.child("users").child(userId);
                            currentUserRef.child("name").setValue(name);
                            currentUserRef.child("phone").setValue(phone);
                            currentUserRef.child("email").setValue(email);
                            currentUserRef.child("isBlock").setValue(true);
                            currentUserRef.child("userUID").setValue(FirebaseAuth.getInstance().getUid());



                            Toast.makeText(SignupActivity.this, "Registration successful, wait for confirmation.", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            // Registration failed
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(SignupActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        // Perform email validation here
        // You can use a regular expression or Android's built-in Patterns class to validate the email format

        // Example using Patterns class
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}