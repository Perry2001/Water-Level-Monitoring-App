package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.Utils.DateAndTimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "MyChannel";
    private TriStateToggleButton pump_switch;
    private CircularFillableLoaders waterProgressBar;
    private TextView percentageTextView;
    private FirebaseDatabase firebaseDatabase;
    private Button limiter;
    private int setLimit;
    private boolean alarm = true;
    private boolean isSwitchOn = false;
    private int icon;
    private AppCompatButton historyBtn;


    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        createNotificationChannel();

        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        startService(serviceIntent);



        // Initialize the Switch widget
        pump_switch = findViewById(R.id.pump_switch);

        // Initialize the button limiter
        limiter = findViewById(R.id.limiter);

        // Initialize the ProgressBar
        waterProgressBar = findViewById(R.id.waterProgressBar);

        // Initialize the TextView for percentage
        percentageTextView = findViewById(R.id.percentageTextView);

        // Initialize the Firebase Realtime Database instance
        firebaseDatabase = FirebaseDatabase.getInstance();

        historyBtn = findViewById(R.id.history_Button);

        historyBtn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
        });


        String imageName = "logo";
        icon = getResources().getIdentifier(imageName, "drawable", getPackageName());

        // Set up a listener to monitor changes in the "PumpStatus" value
        firebaseDatabase.getReference("PumpStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve the current value of "PumpStatus"
                Integer pumpStatus = dataSnapshot.getValue(Integer.class);

                // Update the state of the TriStateToggleButton based on the value
                if (pumpStatus != null) {
                    if (pumpStatus == 1) {
                        pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.on);
                    } else {
                        pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.off);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval
                // For simplicity, this example doesn't include error handling
            }
        });

        // Set up a listener to monitor changes in the "WaterLevelPercentage" value
        firebaseDatabase.getReference("WaterLevelPercentage")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int previousWaterLevel = 0;
                        // Retrieve the current value of "WaterLevelPercentage"
                        Integer waterLevelPercentage = dataSnapshot.getValue(Integer.class);

                        // Retrieve the last set limit value from SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        int setLimit = preferences.getInt("SetLimit", 0);

                        // Update the progress of the ProgressBar based on the value
                        if (waterLevelPercentage != null) {
                            // Reverse the progress when reaching 100%
                            int progress = waterLevelPercentage;
                            progress = 100 - progress;

                            waterProgressBar.setProgress(progress);
                            percentageTextView.setText(waterLevelPercentage + "%");

                            //Save water percentage
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("waterPercentageHistory").child(DateAndTimeUtils.getDateAndTimeWithSecID());
                            if (waterLevelPercentage != null &&
                                    waterLevelPercentage != previousWaterLevel ){
                                databaseReference.child("waterPercentage").setValue(waterLevelPercentage);
                                databaseReference.child("date").setValue(DateAndTimeUtils.getCurrentDate());
                                databaseReference.child("time").setValue(DateAndTimeUtils.getCurrentTime());
                                previousWaterLevel = waterLevelPercentage;
                            }


                            if (waterLevelPercentage >= 98) {
                                pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.off);
                                Toast.makeText(DashboardActivity.this, "Water Level is Full", Toast.LENGTH_SHORT).show();
                            }

                            // Check if the water level matches or exceeds the set limit
                            if (waterLevelPercentage >= setLimit) {
                                // If the switch is currently on, turn it off
                                if (isSwitchOn) {
                                    pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.off);
                                    firebaseDatabase.getReference("PumpStatus").setValue(0);
                                    isSwitchOn = false;

                                }


                                playNotificationSound();


                            } else {
                                // If the switch is currently off, turn it on
                                if (!isSwitchOn) {
                                    pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.on);
                                    firebaseDatabase.getReference("PumpStatus").setValue(1);
                                    isSwitchOn = true;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors that occur during data retrieval
                        // For simplicity, this example doesn't include error handling
                    }
                });

        // Set up a listener for the Switch widget state changes
        pump_switch.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                // Assuming you have a reference to the Firebase database and the water level percentage reference
                DatabaseReference waterLevelRef = firebaseDatabase.getReference("WaterLevelPercentage");
                DatabaseReference pumpStatusRef = firebaseDatabase.getReference("PumpStatus");

                waterLevelRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot waterLevelSnapshot) {
                        if (waterLevelSnapshot.exists()) {
                            // Retrieve the water level percentage from the waterLevelSnapshot
                            int waterLevelPercentage = waterLevelSnapshot.getValue(Integer.class);

                            // Fetch pump status data
                            pumpStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot pumpStatusSnapshot) {
                                    if (pumpStatusSnapshot.exists()) {
                                        int valueToSend;
                                        if (waterLevelPercentage >= 98) {
                                            // Water level is already full.
                                            Toast.makeText(getApplicationContext(), "Water level is already full", Toast.LENGTH_SHORT).show();
                                            pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.off);
                                            valueToSend = 0;
                                        } else {
                                            // Water level is not full.
                                            if (booleanToggleStatus) {
                                                pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.on);
                                                valueToSend = 1;
                                            } else {
                                                pump_switch.setToggleStatus(TriStateToggleButton.ToggleStatus.off);
                                                valueToSend = 0;
                                            }
                                        }

                                        // Set the pump status in Firebase
                                        pumpStatusRef.setValue(valueToSend);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle error if needed
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error if needed
                    }
                });
            }
        });


        // Set up a listener for the Button limiter state changes
        // Set an OnClickListener for the button
        limiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show an AlertDialog when the button is clicked
                showLimitWaterDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        stopService(serviceIntent);

    }

    // Method to show the custom water level limiting dialog
    private void showLimitWaterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert, null);

        // Find the views in the custom layout
        SeekBar seekBar = dialogView.findViewById(R.id.seekBar);
        TextView limitValueTextView = dialogView.findViewById(R.id.limitValue);

        // Retrieve the last set limit value and alarm status from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        setLimit = preferences.getInt("SetLimit", 0);
        alarm = preferences.getBoolean("AlarmEnabled", false); // Default: Alarm is disabled

        // Set the initial values for the SeekBar, TextView, and CheckBox
        seekBar.setProgress(setLimit);
        limitValueTextView.setText(String.valueOf(setLimit));

        // Set a listener to update the value when the SeekBar changes
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the current value
                limitValueTextView.setText(String.valueOf(progress));
                // Update the setLimit variable when SeekBar changes
                setLimit = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        builder.setView(dialogView);
        builder.setTitle("Set Water Level Limit");
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the positive button click (e.g., save the water level limit)

                // Save the set limit value and alarm status in SharedPreferences for future use
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putInt("SetLimit", setLimit);
                editor.apply();

                // You can use setLimit and alarm status elsewhere in your code
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alarm = false; // Disable the alarm
                setLimit = 100;
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putInt("SetLimit", setLimit);
                editor.putBoolean("AlarmEnabled", alarm); // Save alarm status as disabled
                editor.apply();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Create a notification channel for Android 8.0 and higher
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Play a notification sound
    private void playNotificationSound() {
        // Use a custom sound file from res/raw or set a default sound
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this, soundUri);
        ringtone.play();

        // Create and show a notification with a unique ID
        int notificationId = 0 /* Generate a unique ID here */;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Water Limit Reached")
                .setContentText("The water limit you set has been reached.")
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

}