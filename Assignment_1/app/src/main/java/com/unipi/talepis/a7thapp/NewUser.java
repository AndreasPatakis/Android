package com.unipi.talepis.a7thapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class NewUser extends AppCompatActivity {

    TextView username;
    EditText speed;
    Button submit;
    SharedPreferences preferences;

    //Saves user's name and speed limit to Shared Preferences
    private void save_user(String name, String limit){
        int int_limit = Integer.parseInt(limit);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name,int_limit);
        editor.apply();
    }

    //Checks if speed limit's value is legit
    private boolean get_speedLimit(String speed){
        if(speed.matches("")){
            Toast.makeText(getApplicationContext(),"Please add speed limit.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = findViewById(R.id.userNewUser);
        username.setText(getIntent().getStringExtra("username"));
        submit = findViewById(R.id.submitNewUser);
        speed = findViewById(R.id.speedEditTextNewUser);
        Intent intent_main_activity = new Intent(this, MainActivity.class);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(get_speedLimit(speed.getText().toString())){
                    save_user(username.getText().toString(),speed.getText().toString());
                    intent_main_activity.putExtra("username",username.getText().toString());
                    startActivity(intent_main_activity);
                }
            }
        });
    }
}