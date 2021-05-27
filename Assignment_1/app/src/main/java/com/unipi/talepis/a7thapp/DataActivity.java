package com.unipi.talepis.a7thapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class DataActivity extends AppCompatActivity {
    Button deleteButton;
    ListView listView;
    TextView limitTextView,recordsTextView;
    SharedPreferences preferences;
    SQLiteDatabase db;
    String currentUser;
    int currentLimit;
    final String DEFAULT_OPTION = "All";



    //Returns data according to option selected (All, Last week, Last month ,e.t.c.)
    private ArrayList<Trajectory> getFilteredData(String option){
        String today = getCurrentDate();
        ArrayList<Trajectory> list = DBtoList();
        ArrayList<Trajectory> new_list = new ArrayList<Trajectory>();

        for (Trajectory record:list) {
            String datetime = epochToDate(Long.valueOf(record.getTimestamp()));
            String[] date = datetime.split(" ",-1);
            LocalDate d1 = LocalDate.parse(date[0], DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate d2 = LocalDate.parse(today, DateTimeFormatter.ISO_LOCAL_DATE);
            Duration diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
            long diffDays = diff.toDays();

            if(option.matches("All")){
                if(((int)record.getSpeed()) >= currentLimit){
                    new_list.add(record);
                }
            }else if(option.matches("Last week")){
                if(diffDays<7){
                    if(((int)record.getSpeed()) >= currentLimit){
                        new_list.add(record);
                    }

                }
            }else if(option.matches("Last month")){
                if(diffDays<30){
                    if(((int)record.getSpeed()) >= currentLimit){
                        new_list.add(record);
                    }
                }
            }
        }
        return new_list;
    }


    //Returns today's day in format yyyy-MM-dd
    private String getCurrentDate() {
        String date,day,month,year;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if(day.length() == 1){day = "0"+day;}
        month = String.valueOf(Integer.valueOf(calendar.get(Calendar.MONTH))+1);
        if(month.length() == 1){month = "0"+month;}
        year = String.valueOf(calendar.get(Calendar.YEAR));
        date = year + "-"+month+"-"+day;

        return date;
    }

    //Converts epoch timestamp to date object
    private String epochToDate(Long epoch){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(epoch*1000));
        return dateString;
    }

    //Refreshes this activity(when a change is made for example)
    private void refreshDataActivity(String option){
        findUser(getIntent().getStringExtra("username"));
        ArrayList<Trajectory> list = getFilteredData(option);
        limitTextView.setText("Data greater than your speed limit. "+ "\nCurrent Limit: "+currentLimit+" km/h.");
        if(list.size()<=0){ Toast.makeText(getApplicationContext(),"No Data Found :(",Toast.LENGTH_LONG).show();}
        printListView(list);
        recordsTextView.setText(String.valueOf(list.size()));
    }

    //Delete ALL data of the current user
    private void deleteData(String option){
        ArrayList<Trajectory> list = getFilteredData(option);
        if(list.size() > 0){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Delete Data?");
            alert.setMessage("Do you want to all the data for the user: "+currentUser+"?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.execSQL("DELETE FROM LocationData WHERE username = '"+currentUser+"'");
                    refreshDataActivity(option);
                    Toast.makeText(getApplicationContext(),"Data Deleted Successfully!",Toast.LENGTH_LONG).show();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //pass
                }
            });
            alert.create().show();
        }
    }

    //Finds user and retrieves his speed limit
    private void findUser(String name){
        Map<String,Integer> keys = (Map<String, Integer>) preferences.getAll();
        for(Map.Entry<String,Integer> user : keys.entrySet()) {
            if (user.getKey().matches(name)) {
                currentUser = user.getKey();
                currentLimit = user.getValue();
            }
        }
    }

    //Reads our DB and returns all the data to a list
    private ArrayList<Trajectory> DBtoList(){
        ArrayList<Trajectory> list = new ArrayList<Trajectory>();
            Cursor data = db.rawQuery("SELECT * FROM LocationData WHERE username = '"+currentUser+"'",null);
            if(data.getCount() == 0){
                return list;
            }else{
                while (data.moveToNext()){
                    Trajectory temp_trajectory = new Trajectory();

                    temp_trajectory.setUsername(data.getString(0));
                    temp_trajectory.setTimestamp(data.getInt(1));
                    temp_trajectory.setLongitude(data.getString(2));
                    temp_trajectory.setLatitude(data.getString(3));
                    temp_trajectory.setSpeed(data.getFloat(4));

                    list.add(temp_trajectory);
                    temp_trajectory = null;
                }
            }
        return list;
    }

    //Fills listView with the according data
    private void printListView(ArrayList<Trajectory> list) {
        String date,longitude,latitude,speed,name;
        ArrayList<String> records = new ArrayList<String>();
        String record = "";
        for (Trajectory trajectory : list) {
            date ="Date & Time: "+epochToDate(Long.valueOf(trajectory.getTimestamp()));
            longitude = "Longitude: "+trajectory.getLongitude();
            latitude = "Latitude: "+trajectory.getLatitude();
            speed = "Speed: "+trajectory.getSpeed();
            record+=date+"\n"+speed+"\n"+longitude+"\n"+latitude;
            records.add(record);
            record = "";
        }
        ListAdapter listadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, records);
        listView.setAdapter(listadapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        listView = findViewById(R.id.dataListViewData);
        deleteButton = findViewById(R.id.deleteButtonData);
        limitTextView = findViewById(R.id.limitTextViewData);
        recordsTextView = findViewById(R.id.recordTextViewData);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Set spinner
        Spinner spinner = (Spinner)findViewById(R.id.spinnerData);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.spinnerOptions));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        //End setting spinner

        //DATABASE
        db = openOrCreateDatabase("LocationData", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS LocationData(username TEXT,timestamp INTEGER ,long TEXT, lat TEXT, speed REAL)");
        //DATABASE


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(DEFAULT_OPTION);
            }
        });

        //Called when user selects different search-by option(All, month ,week, etc)
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshDataActivity(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                refreshDataActivity(DEFAULT_OPTION);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}