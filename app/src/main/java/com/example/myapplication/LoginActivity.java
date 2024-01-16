package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView btnSignup;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));


            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users").child(FirebaseAuth.getInstance().getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //If user is blocked, user will return in login activity
                    if (snapshot.hasChild("isBlock")){
                        boolean isBlock = (boolean) snapshot.child("isBlock").getValue();
                        if (isBlock){
                            Toast.makeText(getApplicationContext(), "User blocked", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            finish();

                        }
                        else {
                            // User is already logged in, redirect to the main activity
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {



                }
            });


        }
    }




    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

       else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginActivity.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }
       else {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                        .getReference("users").child(FirebaseAuth.getInstance().getUid());

                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //If user is blocked, user will return in login activity
                                        boolean isBlock = (boolean) snapshot.child("isBlock").getValue();
                                        if (isBlock){
                                            Intent intent = new Intent(LoginActivity.this, reviewActivity.class);
                                            startActivity(intent);

                                        }
                                        else {

                                            // Login successful

                                            Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                            // Proceed to the main activity or any other desired screen
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {



                                    }
                                });

                            } else {
                                // Login failed
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Login failed. " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


}
