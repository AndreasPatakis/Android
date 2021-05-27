package com.unipi.talepis.Assignment2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    public static final String PHONE_NUMBER = "13033";
    public static final String KEY_FIRSTNAME = "Firstname";
    public static final String KEY_LASTNAME = "Lastname";
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_REGION = "Region";
    private static final int REC_RESULT = 653;
    ListView listview;
    SQLiteDatabase db;
    TextView username, message1,message2;
    String currentUserUID;
    Button send;
    ImageButton settingsBtn,recognitionBtn;
    FirebaseFirestore firestore;
    private FusedLocationProviderClient locationProviderClient;
    ArrayList<String> acceptedWords = new ArrayList<>();

    //Setting accepted messages from voice recognition
    private void setCurrentMessages(){
        acceptedWords.clear();
        String word;
        ArrayList<Message> messages = DB_Messages_toList();
        for(Message message : messages){
            word = "code "+String.valueOf(message.getNum()+" send");
            acceptedWords.add(word);
            word = "κωδικός "+String.valueOf(message.getNum()+" αποστολή");
            acceptedWords.add(word);
        }

    }

    //Handles the sms send action
    public void sendSMS(String message){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},5434);
        }else {
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(PHONE_NUMBER, null, message, null, null);
                Toast.makeText(this, "Message Sent!", Toast.LENGTH_LONG).show();
            }
    }

    //Displays selected message on users screen. If bool send is true, then it also sends the message
    private void showMessage(String selectedItem,boolean send){
        DocumentReference docRef = firestore.collection("usersInfo").document(currentUserUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        String messageOutput = "";
                        String[] arr = selectedItem.split(" ",-1);
                        messageOutput+=arr[0]+" "+doc.getString(KEY_FIRSTNAME)+" "+doc.getString(KEY_LASTNAME);
                        message1.setText(messageOutput);
                        messageOutput = " "+doc.getString(KEY_ADDRESS)+" "+doc.getString(KEY_REGION);
                        message2.setText(messageOutput);
                        if(send){
                            messageReadytoSend();
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    //Gets the position of selected message from listView, and then calls showMessage with the String message as a parameter
    private void showMessage(int pos,boolean send){
        String message;
        Log.d("Message: ",String.valueOf(pos));
        Cursor cursor = db.rawQuery("SELECT * FROM Messages WHERE code = '"+pos+"'",null);
        if(cursor.getCount() == 0){
            Toast.makeText(getApplicationContext(),"No message with that code was found",Toast.LENGTH_LONG).show();
        }else{
            while(cursor.moveToNext()){
                message = cursor.getString(0)+" "+cursor.getString(1);
                showMessage(message,send);
                break;
            }
        }
    }

    //Requests gps permission
    private void enableGPS() {
        if (ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    //Gets last GPS location. That would be longitude and latitude
    private void getLocationGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableGPS();
            return;
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(MenuActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String currentLocation = "";
                long  timestamp;
                if(location!=null){
                    currentLocation+="Lat: "+String.valueOf(location.getLatitude());
                    currentLocation+="\nLong: "+String.valueOf(location.getLongitude());

                }else{
                    currentLocation = "null";
                }
                timestamp = System.currentTimeMillis();
                saveUsersTimeLoc(currentLocation,String.valueOf(timestamp));
            }
        });
    }

    //Saves current user's timestamp and location data to Firabase Databse
    private void saveUsersTimeLoc(String Location, String Timestamp){
        Map<String, String> user = new HashMap<>();
        user.put("Location",Location);
        user.put("Timestamp",Timestamp);

        //GETTING DOCUMENTS TO READ THE ID OF LAST RECORD
        DocumentReference dR1 = firestore.collection("usersData").document(currentUserUID);
        dR1.collection("Data").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot doc = task.getResult();
                    int newDocId = doc.size()+1;
                    //SETTING NEW DOCUMENT WITH INCREMENTED ID
                    DocumentReference dR2 = firestore.collection("usersData").document(currentUserUID).collection("Data").document(String.valueOf(newDocId));
                    dR2.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"onSuccess: Timestamp and Location saved for "+currentUserUID);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"Error adding document"+e);
                                }
                            });
                }else{
                    Log.d("Error: ","Error getting documents");
                }
            }
        });
    }


    //Updates the listView
    private void updateListView(){
        ArrayList<Message> messages;
        ArrayList<String> formattedMessages = new ArrayList<>();
        messages = DB_Messages_toList();
        Collections.sort(messages);
        String record = "";
        for(Message message : messages){
            record += message.getNum();
            record +=" "+message.getWhat_for();
            formattedMessages.add(record);
            record = "";
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,formattedMessages);
        listview.setAdapter(arrayAdapter);

    }

    //Retrieves users data from Firebase, using his unique id (uID)
    private void getCurrentUser(String uID){
        DocumentReference docRef = firestore.collection("usersInfo").document(uID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User u = new User();
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()){
                                username.setText(doc.getString(KEY_FIRSTNAME));
                                Log.d("Message:","User successfully retrieved.");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    //Retrieves available messages from SQL database, and returns the results in a form a list
    private ArrayList<Message> DB_Messages_toList(){
        ArrayList<Message> messagesList = new ArrayList<Message>();
        Cursor data = db.rawQuery("SELECT * FROM Messages",null);
        if(data.getCount() == 0){
            Log.d("Error: ","Empty messages table");
        }else{
            while (data.moveToNext()){
                Message m = new Message();
                m.setNum(data.getInt(0));
                m.setWhat_for(data.getString(1));

                messagesList.add(m);
            }
        }
        return messagesList;
    }

    //Handles all the actions required in order to send the message
    private void messageReadytoSend(){
        String messageToSed = message1.getText().toString()+message2.getText().toString();
        if(messageToSed.matches("")){
            Toast.makeText(getApplicationContext(),"Choose message to send",Toast.LENGTH_LONG).show();
        }else{
            Log.d("Message Sent: ",messageToSed);
            getLocationGPS();
            sendSMS(messageToSed);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = openOrCreateDatabase("AppData", Context.MODE_PRIVATE,null);
        setContentView(R.layout.activity_menu);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        firestore = FirebaseFirestore.getInstance();
        setCurrentMessages();
        listview = findViewById(R.id.listViewMenu);
        username = findViewById(R.id.userTextViewMenu);
        message1 = findViewById(R.id.message1TextViewMenu);
        message2 = findViewById(R.id.message2TextViewMenu);
        send  =findViewById(R.id.sendButtonMenu);
        settingsBtn = findViewById(R.id.settingsButtonMenu);
        recognitionBtn = findViewById(R.id.recognitionButtonMenu);
        currentUserUID = getIntent().getStringExtra("uID");
        getCurrentUser(currentUserUID);
        updateListView();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               showMessage((String) parent.getItemAtPosition(position),false);
            }
        });


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                settingsIntent.putExtra("uID", currentUserUID);
                startActivity(settingsIntent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageReadytoSend();
            }
        });

        recognitionBtn.setOnClickListener(new View.OnClickListener() {
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
                for (String message : acceptedWords) {
                    if (matches.contains(message)) {
                        String[] arrOfMatches = message.split(" ",-1);
                        int pos = Integer.valueOf(arrOfMatches[1]);
                        showMessage(pos,true);
                    }
                }
        }
    }

    public void recognize(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            enableGPS();
        }else{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Πες 'Κωδικος Χ αποστολή' για να σταλθεί το μήνυμα σου!");

            startActivityForResult(intent, REC_RESULT);
        }

    }


    //END VOICE RECOGNITION

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Exit application");
        alert.setMessage("Do you want to exit application?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
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