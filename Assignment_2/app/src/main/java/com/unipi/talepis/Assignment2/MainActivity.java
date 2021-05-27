package com.unipi.talepis.Assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Button login,register;
    EditText email,password;


    //Checks if EditViews are empty
    private boolean gapsCheck(){
        if(email.getText().toString().matches("") || password.getText().toString().matches("")){
            Toast.makeText(getApplicationContext(),"Please fill the gaps to login!",Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    //Returns a map with the default codes and messages
    private Map<Integer,String> currentMessages(){
        Map<Integer,String> messages = new HashMap<Integer,String>();
        messages.put(1,"Μετάβαση σε φαρμακείο ή επίσκεψη στον γιατρό, εφόσον αυτό συνιστάται μετά από σχετική επικοινωνία");
        messages.put(2,"Μετάβαση σε εν λειτουργία κατάστημα προμηθειών αγαθών πρώτης ανάγκης, όπου δεν είναι δυνατή η αποστολή τους");
        messages.put(3,"Μετάβαση σε δημόσια υπηρεσία ή τράπεζα στο μέτρο που δεν είναι δυνατή η ηλεκτρονική συναλλαγή");
        messages.put(4,"Κίνηση για παροχή βοήθειας σε ανθρώπους που βρίσκονται σε ανάγκη ή συνοδεία ανηλίκων μαθητών από/προς το σχολείο");
        messages.put(5,"Μετάβαση σε τελετή κηδείας υπό τους όρους που προβλέπει ο νόμος ή μετάβαση διαζευγμένων γονέων ή γονέων που τελούν σε διάσταση που είναι αναγκαία για τη διασφάλιση της επικοινωνίας γονέων και τέκνων, σύμφωνα με τις κείμενες διατάξεις");
        messages.put(6,"Σωματική άσκηση σε εξωτερικό χώρο ή κίνηση με κατοικίδιο ζώο, ατομικά ή ανά δύο άτομα, τηρώντας στην τελευταία αυτή περίπτωση την αναγκαία απόσταση 1,5 μέτρου");

        return messages;
    }

    //If there are no messages in the database, we insert the default ones (using currentMessages)
    private void checkMessages(){
        db = openOrCreateDatabase("AppData", Context.MODE_PRIVATE,null);
        String count = "SELECT count(*) FROM Messages";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount==0){
            Map<Integer,String> messages = currentMessages();
            for(Map.Entry<Integer,String> message : messages.entrySet()){
                db.execSQL("INSERT INTO Messages VALUES('" + message.getKey() + "','" + message.getValue() +"')");
            }
        }
    }


    //Handles the sign in task in Firebase, using email and password authentication
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("Message:","signInWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            Intent intentMenu = new Intent(getApplicationContext(),MenuActivity.class);
                            intentMenu.putExtra("uID",currentUser.getUid());
                            startActivity(intentMenu);
                        }else{
                            Log.d("Message:","signInWithEmail:failure");
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        //CREATING SQLITE DB
        db = openOrCreateDatabase("AppData", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Messages(code INTEGER,what_for TEXT)");
        //CREATING SQLITE DB
        checkMessages();
        email = findViewById(R.id.emailEditTextMain);
        password = findViewById(R.id.passwordEditTextMain);
        login = findViewById(R.id.loginButtonMain);
        register = findViewById(R.id.registerButtonMain);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gapsCheck()){
                    signIn(email.getText().toString(),password.getText().toString());
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(getApplicationContext(),signUpActivity.class);
                startActivity(signupIntent);

            }
        });

    }

}