package com.unipi.talepis.fourthapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {
    SharedPreferences preferences;
    EditText name;
    TextView x,y;
    Button save,showSaved,enableGPS,deleteData;
    LocationManager locationManager;
    SQLiteDatabase db;



    private void DeleteData(View view){

        Cursor c = db.rawQuery("SELECT * FROM LocationData",null);
        if(c.moveToFirst()){
            db.execSQL("DELETE FROM " +"name");
            db.execSQL("DELETE FROM " +"x");
            db.execSQL("DELETE FROM " +"y");
        }
        Toast.makeText(this, "Database Is Empty", Toast.LENGTH_LONG).show();
    }

    public void ActivateGPS(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                this);
        //locationManager.removeUpdates(this);
    }


    private void SaveData(View view){
        String X = x.getText().toString();
        String Y = y.getText().toString();
        String c_name = name.getText().toString();
        db.execSQL("INSERT INTO LocationData VALUES('"+c_name+"','"+X+"','"+Y+"')");
        Toast.makeText(this, "Data saved!", Toast.LENGTH_LONG).show();
    }

    private void ShowSaved(View view){
        Cursor cursor = db.rawQuery("SELECT * FROM LocationData",null);
        if(cursor.moveToFirst()){
            Intent intent = new Intent(this,MainActivity2.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "No Data Found!!", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        name = (EditText)findViewById(R.id.nameEditText);
        x = findViewById(R.id.longTextView);
        y = findViewById(R.id.latTextView);
        save = (Button)findViewById(R.id.saveButton);
        showSaved = (Button)findViewById(R.id.savedButoon);
        enableGPS = (Button)findViewById(R.id.permissionButton);
        //deleteData = (Button)findViewById(R.id.deleteButton);
        db = openOrCreateDatabase("LocationData", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS LocationData(name TEXT, x TEXT, y TEXT)");


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"Enable GPS To Continue",Toast.LENGTH_LONG).show();
                }else if(x.getText().toString().matches("") && y.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(),"Enable GPS To Continue",Toast.LENGTH_LONG).show();
                }
                else if(name.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(),"Please Fill in Username",Toast.LENGTH_LONG).show();
                }else{
                    SaveData(v);
                }

            }
        });

        showSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,?> keys = preferences.getAll();
                if(keys.isEmpty()){
                    Toast.makeText(getApplicationContext(),"No Data Found",Toast.LENGTH_LONG).show();
                }else{
                    ShowSaved(v);
                }

            }
        });

        enableGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivateGPS(v);
                Toast.makeText(getApplicationContext(),"GPS is Enabled",Toast.LENGTH_LONG).show();
            }
        });

        /*deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteData(v);
                Toast.makeText(getApplicationContext(),"Data Cleared Successfully!",Toast.LENGTH_LONG).show();
            }
        });*/
    }


    @Override
    public void onLocationChanged(Location location) {
        double dx = location.getLongitude();
        double dy = location.getLatitude();
        x.setText(String.valueOf(dx));
        y.setText(String.valueOf(dy));
        Log.d("POS:",String.valueOf(dx)+ " "+ String.valueOf(dy));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}