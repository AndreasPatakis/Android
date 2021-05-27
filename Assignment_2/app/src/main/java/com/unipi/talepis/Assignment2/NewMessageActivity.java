package com.unipi.talepis.Assignment2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class NewMessageActivity extends AppCompatActivity {
    private static final int REC_RESULT = 653;
    ListView listView;
    Button addBtn,deleteBtn,modifyBtn;
    ImageButton recognizeBtn;
    EditText code, message;
    SQLiteDatabase db;
    String selectedMessage;

    private Integer tryParseInt(String value){
        try{
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e){
            return 0;
        }
    }

    //Updates listview
    private void updateListView(){
        if(listView.getSelectedItem() == null){
            modifyBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            selectedMessage = "";
        }
        String message_string;
        ArrayList<String> formattedMessages = new ArrayList<>();
        ArrayList<Message> messages = getMessagesFromDB();
        Collections.sort(messages);

        for(Message message : messages){
            message_string = String.valueOf(message.getNum());
            message_string += " "+message.getWhat_for();
            formattedMessages.add(message_string);
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,formattedMessages);
        listView.setAdapter(adapter);
    }


    //Retrieves available messages from SQL database and returns them as a list
    private ArrayList<Message> getMessagesFromDB(){
        ArrayList<Message> messages = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Messages",null);
        if(cursor.getCount() == 0){
            Log.d("Message: ","No messages found in db");
            Toast.makeText(getApplicationContext(),"No message was found.",Toast.LENGTH_LONG).show();
        }else{
            while(cursor.moveToNext()){
                messages.add(new Message(cursor.getInt(0),cursor.getString(1)));
            }
        }
        return messages;
    }

    //Sets code and message TextView with the respective values
    private void setChosenMessage(String message){
        String[] arrOfMessage = message.split(" ",2);
        code.setText(arrOfMessage[0]);
        this.message.setText(arrOfMessage[1]);
    }

    //All actions need to be taken when the user presses the Add button
    private void addBtnProcess(int newCode, String newMessage){
        boolean exists = false;
        if(String.valueOf(newCode).matches("0") || newMessage.matches("")){
            Toast.makeText(getApplicationContext(),"Please fill both code and message",Toast.LENGTH_LONG).show();
        }else{
            ArrayList<Message> currentMessages = getMessagesFromDB();
            for(Message message : currentMessages){
                if(message.getNum() == newCode){
                    exists = true;
                    ContentValues cv = new ContentValues();
                    cv.put("what_for",newMessage);
                    db.update("Messages",cv,"code = '"+newCode+"'",null);
                    break;
                }
            }
            for(Message message : currentMessages) {
                if(exists){break;}
                else{
                    if (message.getWhat_for().matches(newMessage)) {
                        exists = true;
                        ContentValues cv = new ContentValues();
                        cv.put("code", newCode);
                        db.update("Messages", cv, "what_for = '" + newMessage + "'", null);
                        break;
                    }
                }
            }
            if(!exists){
                db.execSQL("INSERT INTO Messages VALUES('" + newCode + "','" + newMessage +"')");
                System.out.println("NEW ADDITION");
            }
            updateListView();
            clearEditViews();
            Toast.makeText(getApplicationContext(),"Changes saved successfully!",Toast.LENGTH_LONG).show();
        }
    }

    //All actions need to be taken when the user presses the Delete button
    private void deleteBtnProcess(String message){
        String[] arrOfStr = message.split("",2);
        if(message.matches("")){
            Toast.makeText(getApplicationContext(),"No message selected",Toast.LENGTH_LONG).show();
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Delete Message");
            alert.setMessage("Do you want to delete message with code "+arrOfStr[0]+" ?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int code = Integer.valueOf(arrOfStr[0]);
                    if(db.delete("Messages","code = '"+code+"'",null)>0){
                        Toast.makeText(getApplicationContext(),"Message Deleted",Toast.LENGTH_LONG).show();
                        updateListView();
                    }else{
                        Toast.makeText(getApplicationContext(),"Code given doesn't exist",Toast.LENGTH_LONG).show();
                    }
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

    //All actions need to be taken when the user presses the Modify button
    private void modifyBtnProcess(String message){
        if(message.matches("")){
            Toast.makeText(getApplicationContext(),"Please select message",Toast.LENGTH_LONG).show();
        }else{
            setChosenMessage(message);
            modifyBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
    }

    private void clearEditViews(){
        code.setText("");
        message.setText("");
    }

    //VOICE RECOGNITION
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String answer = matches.get(0);
            String[] arrOfanswers = answer.split(" ",3);
            String action = arrOfanswers[0];
            if(action.matches("προσθήκη") && arrOfanswers.length >= 3){
                int codeInt = tryParseInt(arrOfanswers[1]);
                if(codeInt != 0){
                    addBtnProcess(codeInt,arrOfanswers[2]);
                }
            }else if(action.matches("διαγραφή")&& arrOfanswers.length >= 2){
                deleteBtnProcess(arrOfanswers[1]);
            }else{
                Toast.makeText(getApplicationContext(),"Action not found.",Toast.LENGTH_LONG).show();
            }

        }
    }

    public void recognize(View view){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Πες 'Προσθήκη Χ Αιτιολογία' για να προσθέσεις εναν καινούριο κωδικό" +
                            "\nΉ 'Διαγραφη Χ' για να διαγράψεις καποίον που ήδη υπάρχει!");

            startActivityForResult(intent, REC_RESULT);

    }


    //END VOICE RECOGNITION
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        db = openOrCreateDatabase("AppData", Context.MODE_PRIVATE,null);
        code = findViewById(R.id.codeEditNewMessage);
        message = findViewById(R.id.messageEditNewmessage);
        addBtn = findViewById(R.id.addButtonNewMessage);
        deleteBtn = findViewById(R.id.deleteButtonNewMessages);
        modifyBtn = findViewById(R.id.modifyButtonNewMessage);
        listView = findViewById(R.id.listViewNewMessages);
        recognizeBtn = findViewById(R.id.recognizeButtonNewMessages);
        updateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearEditViews();
                modifyBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                selectedMessage = (String)parent.getItemAtPosition(position);
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(code.getText())){
                    int codeInt = tryParseInt(code.getText().toString());
                    String messageStr = message.getText().toString();
                    addBtnProcess(codeInt,messageStr);
                }else{
                    Toast.makeText(getApplicationContext(),"Please fill all the gaps.",Toast.LENGTH_LONG).show();
                }

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBtnProcess(selectedMessage);
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyBtnProcess(selectedMessage);
            }
        });

        recognizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognize(v);
            }
        });

    }

}