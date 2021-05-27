package com.unipi.talepis.Assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    ListView listView;
    String currentUserUID;

    //Updates ListView
    private void updateListView(){
       ArrayList<String> options = new ArrayList<>();
        options.add("User's Information");
        options.add("Manage Messages");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,options);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        listView = findViewById(R.id.listViewSettings);
        currentUserUID = getIntent().getStringExtra("uID");
        updateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent usersInfoIntent = new Intent(getApplicationContext(),UsersInfoActivity.class);
                    usersInfoIntent.putExtra("uID",currentUserUID);
                    startActivity(usersInfoIntent);
                }else if(position == 1){
                    Intent newMessageIntent = new Intent(getApplicationContext(),NewMessageActivity.class);
                    startActivity(newMessageIntent);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent menuIntent = new Intent(getApplicationContext(),MenuActivity.class);
        menuIntent.putExtra("uID",currentUserUID);
        startActivity(menuIntent);
    }
}