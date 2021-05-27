package com.unipi.talepis.a7thapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    String currentUser;
    int currentLimit;
    EditText textLimit;
    Button save;

    //Find user and retrieves his speed limit
    private void findUser(String name){
        Map<String,Integer> keys = (Map<String, Integer>) preferences.getAll();
        for(Map.Entry<String,Integer> user : keys.entrySet()){
            if(user.getKey().matches(name)){
                currentUser = user.getKey();
                currentLimit = user.getValue();
            }
        }
    }

    //Saves new data of user(speedLimit)
    private void save_user(String name, String limit){
        int int_limit;
        if(limit.length() != 0) {
            int_limit = Integer.parseInt(limit);
            if (int_limit >= 0) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(name, int_limit);
                editor.apply();
                Toast.makeText(getApplicationContext(),"Changes saved!",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        textLimit = findViewById(R.id.limitSettings);
        findUser(getIntent().getStringExtra("username"));
        save = findViewById(R.id.saveButtonSettings);
        textLimit.setText(String.valueOf(currentLimit), TextView.BufferType.EDITABLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_user(currentUser,textLimit.getText().toString());
            }
        });
    }
}