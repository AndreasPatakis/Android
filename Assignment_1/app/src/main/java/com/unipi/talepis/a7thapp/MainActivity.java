package com.unipi.talepis.a7thapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView username;
    String currentUser ="";
    int currentLimit;
    SharedPreferences preferences;
    Button trackButton,dataButton,settingsButton;


    //Searches shared preferences for given user, and retrieves speed limit
    private void findUser(String name){
        Map<String,Integer> keys = (Map<String, Integer>) preferences.getAll();
        for(Map.Entry<String,Integer> user : keys.entrySet()){
            if(user.getKey().matches(name)){
                currentUser = user.getKey();
                currentLimit = user.getValue();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.usernameMenu);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent intent_track = new Intent(this,TrackSpeedActivity.class);
        Intent intent_data = new Intent(this,DataActivity.class);
        Intent intent_settings = new Intent(this,SettingsActivity.class);
        findUser(getIntent().getStringExtra("username"));
        username.setText(currentUser);
        trackButton = findViewById(R.id.trackButtonMenu);
        dataButton = findViewById(R.id.speedDataButtonMenu);
        settingsButton = findViewById(R.id.settingButtonMenu);
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_track.putExtra("username",currentUser);
                startActivity(intent_track);
            }
        });

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_data.putExtra("username",currentUser);
                startActivity(intent_data);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_settings.putExtra("username",currentUser);
                startActivity(intent_settings);
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent intent_starting = new Intent(this,StartingActivity.class);
        startActivity(intent_starting);

    }

}