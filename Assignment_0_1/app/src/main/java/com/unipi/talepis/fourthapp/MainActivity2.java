package com.unipi.talepis.fourthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    TextView user,location,record;
    int list_length;
    int current;
    Button next;

    private List<LocationDataClass> fillDataList(Map<String,?> keys){
        List<LocationDataClass> list = new ArrayList<LocationDataClass>();
        for(Map.Entry<String,?> entry:keys.entrySet()){
            String value = entry.getValue().toString();
            String[] arr = value.split("_");
            LocationDataClass anonymousObj = new LocationDataClass(entry.getKey(),arr[0],arr[1]);
            list.add(anonymousObj);
        }
        return list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        user = findViewById(R.id.userTextView2);
        location = findViewById(R.id.locationTextView2);
        next = findViewById(R.id.nextButton2);
        record = findViewById(R.id.recordTextView2);

        Map<String, ?> keys = (Map<String, ?>) getIntent().getSerializableExtra("map");
        List<LocationDataClass> location_data = fillDataList(keys);
        list_length = location_data.size();

        //Show first data
        current = 0;
        user.setText(location_data.get(current).getName());
        location.setText(location_data.get(current).getLocation());
        record.setText(location_data.get(current).getKey());
        Log.d("Key: ",location_data.get(current).getKey()+"\nPOS: "+location_data.get(current).getLocation());

        //The rest of data will be shown by pressing NEXT button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = (current+1)%list_length;
                user.setText(location_data.get(current).getName());
                location.setText(location_data.get(current).getLocation());
                record.setText(location_data.get(current).getKey());
                Log.d("Key: ",location_data.get(current).getKey()+"\nPOS: "+location_data.get(current).getLocation());
            }
        });

    }
}