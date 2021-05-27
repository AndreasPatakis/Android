package com.unipi.talepis.a7thapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class TrackSpeedActivity extends AppCompatActivity implements LocationListener {
    SQLiteDatabase db;
    Talk toSay;
    SharedPreferences preferences;
    LocationManager locationManager;
    TextView speedText, usernameText,limitText,trackingMessage;
    String currentUser;
    int currentLimit;
    Button startButton, stopButton, reactButton;
    Chronometer simpleChronometer;
    boolean tracking_state = false;
    long last_said;
    private static final int REC_RESULT = 653;
    private long lastPause = 0;



    private void say(String message) {
        toSay.say(message);
    }

    //Saves data to SQLite db
    private void saveCurrentData(String user, long timestamp, String lon, String lat, float currentSpeed) {
        System.out.println("\nUSER: " + user + " \nTIMESTAMP: " + timestamp + "\nLONGITUDE: "
                + lon + "\nLATITUDE: " + lat + "\nSPEED: " + String.valueOf(currentSpeed));
        db.execSQL("INSERT INTO LocationData VALUES('" + user + "','" + timestamp + "','" + lon + "','" + lat + "','" + currentSpeed + "')");

    }

    //Matches given user with its speed limit
    private void matchUser(String username) {
        Map<String, Integer> keys = (Map<String, Integer>) preferences.getAll();
        for (Map.Entry<String, Integer> user : keys.entrySet()) {
            if (user.getKey().matches(username)) {
                currentUser = user.getKey();
                currentLimit = user.getValue();
            }
        }
    }


    //Things to-do when user crosses limit
    private void limitCrossed(long timestamp, Location location, float currentSpeed) {
        String lon = String.valueOf(location.getLongitude());
        String lat = String.valueOf(location.getLatitude());
        Date date = new Date(timestamp * 1000);
        saveCurrentData(currentUser, timestamp, lon, lat, currentSpeed);
        if (last_said == 0) {
            say("You have crossed your speed limit");
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            last_said = timestamp;
        }
        if (timestamp - last_said > 10) {
            say("You have crossed your speed limit");
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#501515"));
            last_said = timestamp;
        }
    }

    //Activated GPS
    public void ActivateGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 234);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                this);
    }

    //Starts the speed tracking process
    private void startTracking() {
        ActivateGPS();
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            tracking_state = true;
            trackingMessage.setText("Tracking Started");
            startButton.setText("Tracking");
            if(lastPause != 0){
                simpleChronometer.setBase(simpleChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                startButton.setText("start");
            }else{simpleChronometer.setBase(SystemClock.elapsedRealtime());}
            simpleChronometer.start();
        }
    }

    //Stops the tracking process
    private void stopTracking(){
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        last_said = 0;
        trackingMessage.setText("Resume Tracking");
        lastPause = SystemClock.elapsedRealtime();
        startButton.setText("resume");
        simpleChronometer.stop();
        tracking_state = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);
        toSay = new Talk(this);
        //DATABASE INIT
        db = openOrCreateDatabase("LocationData", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS LocationData(username TEXT,timestamp INTEGER ,long TEXT, lat TEXT, speed REAL)");
        //DATABASE INIT
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        matchUser(getIntent().getStringExtra("username"));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
        speedText = findViewById(R.id.speedTextViewTrack);
        usernameText = findViewById(R.id.userTextViewTrack);
        usernameText.setText(currentUser);
        trackingMessage = findViewById(R.id.trackingTextTrack);
        limitText = findViewById(R.id.limitTextTrack);
        limitText.setText(String.valueOf(currentLimit)+"km/h");
        startButton = findViewById(R.id.startButtonTrack);
        stopButton = findViewById(R.id.stopButtonTrack);
        reactButton = findViewById(R.id.reactButtonTrack);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });

        reactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    recognize(v);
                }
            });


        }



    //VOICE RECOGNITION
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.contains("start"))
                startTracking();
            if (matches.contains("stop"))
                stopTracking();
        }

    }

    public void recognize(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivateGPS();
        }else{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say 'start' to start tracking\n or 'stop' to stop tracking!");
            startActivityForResult(intent, REC_RESULT);
        }

    }


    //END VOICE RECOGNITION

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(tracking_state == true){
            System.out.println("Counting...");
            float currentSpeed;
            String lon, lat;
            long  timestamp;
            timestamp = System.currentTimeMillis()/1000;
            currentSpeed = location.getSpeed();
            speedText.setText(String.valueOf(currentSpeed));
            if(currentSpeed>Float.valueOf(currentLimit)){
                limitCrossed(timestamp,location,currentSpeed);
            }else{
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            }
        }else{
            speedText.setText("0.0");
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}