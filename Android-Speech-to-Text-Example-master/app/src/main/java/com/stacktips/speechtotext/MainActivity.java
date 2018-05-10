package com.stacktips.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.system.Os;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;
    private DBHelper mydb ;

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String NAME = "name";

    private Speaker speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Change this to match your
                    // locale
                    tts.setLanguage(Locale.US);
                    tts.speak("Hello", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        preferences = getApplicationContext().getSharedPreferences("pref", getApplicationContext().MODE_PRIVATE);
        editor = preferences.edit();

        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        mydb = new DBHelper(this);

    }


    private void checkTTS() {
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if(mydb.insertSpeech(result.get(0)))
                    {
                        Toast.makeText(getApplicationContext(), "text stored ", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "text not stored", Toast.LENGTH_SHORT).show();
                    }

                    if (result.get(0).equalsIgnoreCase("Hello"))
                    {
                        mVoiceInputTv.setText("Hello, What is your name?");
                        tts.speak("Hello, What is your name?",TextToSpeech.QUEUE_FLUSH, null);

                    }
                    else if (result.get(0).equalsIgnoreCase("what is your name"))
                    {

                        mVoiceInputTv.setText("My name is Cortana. How can I help you?");
                        tts.speak("My name is Cortana. How can I help you?",TextToSpeech.QUEUE_FLUSH, null);
                    }

                    else if (result.get(0).contains("name is")) {
                        String name = result.get(0).split("is")[1];
                        editor.putString(NAME, name).apply();
                        mVoiceInputTv.setText("Hello " + preferences.getString(NAME, "name") + ". I am your personal assistant");
                        tts.speak("Hello " + preferences.getString(NAME, "name") + ". I am your personal assistant",TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if (result.get(0).contains("thank you")) {
                        mVoiceInputTv.setText("Thank you too " + preferences.getString(NAME, "name"));
                        tts.speak("Thank you too " + preferences.getString(NAME, "name"),TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if (result.get(0).contains("what") && result.get(0).contains("time")) {
                        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
                        Date date = new Date();
                        String[] strDate = sdfDate.format(date).split(":");
                        if (strDate[1].contains("00"))
                            strDate[1] = "o'clock";
                        mVoiceInputTv.setText("The time is:" + sdfDate.format(date));
                        tts.speak("The time is:" + sdfDate.format(date),TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if(result.get(0).equalsIgnoreCase("Get all speech")) {
                        List<String> speechList =  new ArrayList<String>();
                        speechList = mydb.getAllSpeech();
                        StringBuilder builder = new StringBuilder();
                        for(String str: speechList){
                            builder.append(str).append("/n");
                        }
                        mVoiceInputTv.setText(builder.toString());
                        tts.speak(builder.toString(),TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else if (result.get(0).contains("you are awesome")) {
                        mVoiceInputTv.setText("I am glad Thank you " + preferences.getString(NAME, "name"));
                        tts.speak("Thank you too " + preferences.getString(NAME, "name"),TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else {
                        mVoiceInputTv.setText("I am sorry! I did not get that.");
                        tts.speak("I am sorry! I did not get that.",TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                }

            }
        }
    }
}