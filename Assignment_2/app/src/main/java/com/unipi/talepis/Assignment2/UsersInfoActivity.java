package com.unipi.talepis.Assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UsersInfoActivity extends AppCompatActivity {
    public static final String KEY_FIRSTNAME = "Firstname";
    public static final String KEY_LASTNAME = "Lastname";
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_REGION = "Region";
    public static final String TAG = "TAG";
    SQLiteDatabase db;
    TextView firstname,lastname,address,region;
    Button saveButton;
    String currentUserUID;
    FirebaseFirestore firestore;


    //Updates user's information in our Firebase Database
    private void updateUsersInfo(){
        Map<String, String> user = new HashMap<>();
        user.put(KEY_FIRSTNAME,firstname.getText().toString());
        user.put(KEY_LASTNAME,lastname.getText().toString());
        user.put(KEY_ADDRESS,address.getText().toString());
        user.put(KEY_REGION,region.getText().toString());

        DocumentReference dR = firestore.collection("usersInfo").document(currentUserUID);
        dR.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","onSuccess: user profile created for "+currentUserUID);
                Toast.makeText(getApplicationContext(),"Changes saved successfully!",Toast.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG","Error adding document"+e);
                        Toast.makeText(getApplicationContext(),"Failed to save data",Toast.LENGTH_LONG).show();
                    }
                });
    }

    //Sets user's current information to application's EditViews
    private void fillUsersInfo(){
        DocumentReference docRef = firestore.collection("usersInfo").document(currentUserUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User u = new User();
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        firstname.setText(doc.getString(KEY_FIRSTNAME));
                        lastname.setText(doc.getString(KEY_LASTNAME));
                        address.setText(doc.getString(KEY_ADDRESS));
                        region.setText(doc.getString(KEY_REGION));
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }


    //Handles all save new information actions
    private void saveUsersInfo(){
        if(firstname.getText().toString().matches("") || lastname.getText().toString().matches("")
                || address.getText().toString().matches("") || region.getText().toString().matches("")){
            Toast.makeText(getApplicationContext(),"All gaps must be filled!",Toast.LENGTH_LONG).show();
        }else{
            updateUsersInfo();
            onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersinfo);
        db = openOrCreateDatabase("AppData", Context.MODE_PRIVATE,null);
        currentUserUID = getIntent().getStringExtra("uID");
        Log.d("MESSAGE: ",currentUserUID);
        firestore = FirebaseFirestore.getInstance();
        firstname = findViewById(R.id.firstnameEditSettings);
        lastname = findViewById(R.id.lastnameEditSettings);
        address = findViewById(R.id.addressEditSettings);
        region = findViewById(R.id.regionEditSettings);
        saveButton = findViewById(R.id.saveButtonSettings);
        fillUsersInfo();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsersInfo();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent settingsIntent = new Intent(getApplicationContext(),SettingsActivity.class);
        settingsIntent.putExtra("uID",currentUserUID);
        startActivity(settingsIntent);
    }
}