package com.unipi.talepis.Assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signUpActivity extends AppCompatActivity {
    public static final String KEY_FIRSTNAME = "Firstname";
    public static final String KEY_LASTNAME = "Lastname";
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_REGION = "Region";
    private FirebaseAuth mAuth;
    SharedPreferences languagePref;
    FirebaseUser currentUser;
    EditText firstname,lastname,email,password,address,region;
    Button register;
    FirebaseFirestore firestoreDB;


    //Adds user's register data to our Firebase Database by using his unique id provided by signUp Authentication
    private void addUserToDB(User currentUser){
        Map<String, String> user = new HashMap<>();
        user.put(KEY_FIRSTNAME,currentUser.getFirstname());
        user.put(KEY_LASTNAME,currentUser.getLastname());
        user.put(KEY_ADDRESS,currentUser.getAddress());
        user.put(KEY_REGION,currentUser.getRegion());

        DocumentReference dR = firestoreDB.collection("usersInfo").document(currentUser.getuID());
        dR.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","onSuccess: user profile created for "+currentUser.getuID());
                Toast.makeText(getApplicationContext(),"Data saved successfully!",Toast.LENGTH_LONG).show();
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

    //Checks if all gaps are filled properly
    private boolean gapsCheck(String firstname,String lastname,String email,String password,String address,String region)
    {
        if(firstname.matches("") || lastname.matches("") || email.matches("") || password.matches("") || address.matches("") || region.matches("")){
            Toast.makeText(getApplicationContext(),"Please fill all the gaps to register!",Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;

        }
    }

    //Creates new user in Firebase using email and password, and also gives each one a unique id (uID)
    private void signUp(String firstname,String lastname,String email, String password,String address,String region){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user  = new User();
                            Log.d("Message:","createUserWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            user.setuID(mAuth.getUid());
                            user.setFirstname(firstname);
                            user.setLastname(lastname);
                            user.setAddress(address);
                            user.setRegion(region);
                            addUserToDB(user);
                            Intent intentMenu = new Intent(getApplicationContext(),MenuActivity.class);
                            intentMenu.putExtra("uID",currentUser.getUid());
                            startActivity(intentMenu);
                        }else{
                            Log.d("Message:","createUserWithEmail:failure");
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firestoreDB = FirebaseFirestore.getInstance();
        languagePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        firstname = findViewById(R.id.firstnameEditSignUp);
        lastname = findViewById(R.id.lastnameEditSignUp);
        email = findViewById(R.id.emailEditSignUp);
        password = findViewById(R.id.passwordEditSignUp);
        address = findViewById(R.id.addressEditSignUp);
        region = findViewById(R.id.regionEditSignUp);
        register = findViewById(R.id.registerButtonSignUp);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gapsCheck(firstname.getText().toString(),lastname.getText().toString(),email.getText().toString(),password.getText().toString(),address.getText().toString(),region.getText().toString())){
                    signUp(firstname.getText().toString(),lastname.getText().toString(),email.getText().toString(),password.getText().toString(),address.getText().toString(),region.getText().toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}