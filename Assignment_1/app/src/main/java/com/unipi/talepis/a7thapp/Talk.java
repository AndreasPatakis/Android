package com.unipi.talepis.a7thapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class Talk {
    private TextToSpeech tts;
    private TextToSpeech.OnInitListener initListener =
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    tts.setLanguage(Locale.forLanguageTag("EN"));
                }
            };
    public Talk(Context context){tts = new TextToSpeech(context,initListener);}
    public void say(String message){tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);}
}
