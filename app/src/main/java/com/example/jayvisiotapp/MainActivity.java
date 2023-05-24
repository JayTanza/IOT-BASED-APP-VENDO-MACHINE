
package com.example.jayvisiotapp;

import static com.example.jayvisiotapp.R.id;
import static com.example.jayvisiotapp.R.layout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button signupButton;
    private Button signinButton;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        signinButton = (Button) findViewById(id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        signupButton = (Button) findViewById(id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });
        //initTextToSpeech();
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.music11jarvis);
        mediaPlayer.start();
    }
    public void openLoginActivity(){
        Intent intent =  new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void openRegisterActivity(){
        Intent intent =  new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void initTextToSpeech(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(MainActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }else{
                    speak("Hi, Engineer Jay, it's nice to see you again");
                }
            }
        });
    }

    private void speak(String msg) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}