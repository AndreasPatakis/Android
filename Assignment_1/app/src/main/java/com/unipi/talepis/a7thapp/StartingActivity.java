package com.unipi.talepis.a7thapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class StartingActivity extends AppCompatActivity {

    EditText username;
    Button login;
    SharedPreferences preferences;

    //Checks if user already exists
    private boolean exists(String username, Map<String,?> users){
        for(Map.Entry<String,?> user:users.entrySet()){
            if(username.equals(user.getKey().toString())){ return true;}
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        username = findViewById(R.id.enterNameText);
        login = findViewById(R.id.loginButtonStarting);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Map<String,?> users = preferences.getAll();
        Intent intent_newUser = new Intent(this, NewUser.class);
        Intent intent_main = new Intent(this, MainActivity.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!username.getText().toString().matches("")){
                    boolean user_exists = exists(username.getText().toString(),users);
                    if(user_exists){
                        intent_main.putExtra("username",username.getText().toString());
                        startActivity(intent_main);
                    }else{
                        intent_newUser.putExtra("username",username.getText().toString());
                        startActivity(intent_newUser);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter a name.",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}