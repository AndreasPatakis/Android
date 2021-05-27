package com.unipi.talepis.fourthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    TextView user,location_x,location_y,record;
    int list_length;
    int current;
    Button next;
    SQLiteDatabase db;
    String name, x,y;

    private List<LocationDataClass> fillDataList(){
        String name, x, y;
        Cursor cursor = db.rawQuery("SELECT * FROM LocationData",null);
        List<LocationDataClass> list = new ArrayList<LocationDataClass>();
        LocationDataClass data;
        if(cursor.getCount()>0){
            while(cursor.moveToNext()) {
                name = cursor.getString(0);
                x = cursor.getString(1);
                y = cursor.getString(2);
                data = new LocationDataClass(name, x, y);
                list.add(data);
            }
        }
        return list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        user = findViewById(R.id.userTextView2);
        location_x = findViewById(R.id.xTextView2);
        location_y = findViewById(R.id.yTextView2);
        next = findViewById(R.id.nextButton2);
        record = findViewById(R.id.recordTextView2);
        db = openOrCreateDatabase("LocationData", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS LocationData(name TEXT, x TEXT, y TEXT)");
        List<LocationDataClass> location_data = fillDataList();

        //Show first data
        list_length = location_data.size();
        current = 0;
        user.setText(location_data.get(current).getName());
        location_x.setText(location_data.get(current).getLocation_X());
        location_y.setText(location_data.get(current).getLocation_Y());
        record.setText(String.valueOf(current));
        Log.d("Name: ",location_data.get(current).getName()+"\nPOS: "+location_data.get(current).getLocation_X());

        //The rest of data will be shown by pressing NEXT button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = (current+1)%list_length;
                user.setText(location_data.get(current).getName());
                location_x.setText(location_data.get(current).getLocation_X());
                location_y.setText(location_data.get(current).getLocation_Y());
                record.setText(String.valueOf(current));
                Log.d("Name: ",location_data.get(current).getName()+"\nPOS: "+location_data.get(current).getLocation_X());
            }
        });

    }
}