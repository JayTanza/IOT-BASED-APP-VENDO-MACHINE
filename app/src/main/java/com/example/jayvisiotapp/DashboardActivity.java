package com.example.jayvisiotapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jayvisiotapp.databinding.ActivityDashboardBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding binding;
    FloatingActionButton fab;

    private SpeechRecognizer speechRecognizer;
    private EditText editText, editText2;
    //private Button micButton;
    private TextToSpeech t1;
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewMobile;
    private String fullname, email, doB, mobile, counts;
    private FirebaseAuth authProfile;
    private ImageView imageView;

    //private MenuView.ItemView item;
    private MenuItem item;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    IntentFilter intentFilter;

    InputStream inputStream;
    OutputStream outputStream;

    RxThread rxThread;
    String RxData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        editText2 = findViewById(R.id.text2);
        //editText2.setText(currentDate);

//        tts.setEngineByname("com.google.android.tts");
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_dashboard);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(DashboardActivity.this,"Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show();
        }else{
            showUserProfile(firebaseUser);
        }
        replacedFragment(new HomeFragment());

        imageView = (ImageView) findViewById(R.id.profilepic2);
        //item = (MenuItem) findViewById(R.id.menu_refresh);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replacedFragment(new ProfileDetailsFragment());
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        rxThread = new RxThread();

        String url = "https://scontent.fceb2-2.fna.fbcdn.net/v/t39.30808-6/279489781_3304709659818340_5139564566068014277_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_eui2=AeEChFfjUHs3ZFPbFb-1ug9YwjTV-Vhn1jDCNNX5WGfWMIia9mgbHdyUC0eQzfxVG6UHHuxuMj3DdhfxRti8Prrl&_nc_ohc=mOk_rcJzitEAX9Rc43m&_nc_ht=scontent.fceb2-2.fna&oh=00_AfApsU3y2pxTNOwX8G5rtN4otAxN9_KBH-0ry9vG9ylrcw&oe=644BB4B0https://scontent.fceb2-2.fna.fbcdn.net/v/t39.30808-6/279489781_3304709659818340_5139564566068014277_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_eui2=AeEChFfjUHs3ZFPbFb-1ug9YwjTV-Vhn1jDCNNX5WGfWMIia9mgbHdyUC0eQzfxVG6UHHuxuMj3DdhfxRti8Prrl&_nc_ohc=mOk_rcJzitEAX9Rc43m&_nc_ht=scontent.fceb2-2.fna&oh=00_AfApsU3y2pxTNOwX8G5rtN4otAxN9_KBH-0ry9vG9ylrcw&oe=644BB4B0";
        //ImageView setImageURI()
        Picasso.get().load(url).fit().centerCrop().into(imageView);

        binding.topNavigationView.setOnMenuItemClickListener(item -> {
            switch(item.getItemId()){
                case R.id.menu_logout:
                    onLogOut();
                    break;
                case R.id.getdevice:
                    ConnectDevice();
                    break;
                case R.id.menu_tracker:
                    replacedFragment(new LocationTracker());
                    break;
                case R.id.connect:
                    ConnectBluetooth();
                    break;
                case R.id.profilepic2:
                    replacedFragment(new SettingsFragment());
                    break;
            }
            return true;
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.home:
                    replacedFragment(new HomeFragment());
                    break;
                case R.id.message:
                    replacedFragment(new MessageFragment());
                    break;
                case R.id.notification:
                    replacedFragment(new NotificationFragment());
                    break;
                case R.id.info:
                    replacedFragment(new AlarmFragment());
                    break;
            }
            return true;
        });



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        editText = findViewById(R.id.text);
        fab = (FloatingActionButton) findViewById(R.id.mic);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
//                editText.setText("");
//                editText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
//                micButton.setImageResource(R.drawable.ic_mic_black_off);
//                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                    editText.setText(data.get(0));

                //getting all the matches
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    editText.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userID = firebaseUser.getUid();

                //Extracting User Reference from Database "Registered Users"
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                        //DataSnapshot dataSnapshot = snapshot.getValue();

                        if(readUserDetails != null){
                            fullname = firebaseUser.getDisplayName();
                            //textView_Welcome.setText(""+fullname);
                            String counts = String.valueOf(snapshot.child("counts").getValue());
                            String counts2 = String.valueOf(snapshot.child("TotalIncome").getValue());
                            String counts3 = String.valueOf(snapshot.child("DailyIncome/DayIncome").getValue());
                            String waterlvl = String.valueOf(snapshot.child("WaterLevel").getValue());
                            String status = String.valueOf(snapshot.child("Machine Status").getValue());
                            String coinstack = String.valueOf(snapshot.child("CoinStack").getValue());
                            //textView_Count.setText("₱"+counts+".00");

                            //String url = "https://scontent-hkt1-2.xx.fbcdn.net/v/t39.30808-6/279489781_3304709659818340_5139564566068014277_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_eui2=AeEChFfjUHs3ZFPbFb-1ug9YwjTV-Vhn1jDCNNX5WGfWMIia9mgbHdyUC0eQzfxVG6UHHuxuMj3DdhfxRti8Prrl&_nc_ohc=Dzqr9yjP--cAX_Op2na&_nc_ht=scontent-hkt1-2.xx&oh=00_AfD94D89i4zwvnIYdE9sOv4K9b04wc4W7MVlxid1R6hNWQ&oe=64184A30";
                            //Picasso.get().load(url).fit().centerCrop().into(imageView);
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask(){
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run(){
                                    String  str = editText.getText().toString();

                                    if(str.equals("what's my total income") || str.equals("total income") ){
                                        editText.setText("Hello boss, how can i help you");
                                        t1.speak("Your total income sir is "+counts2+".00 Pesos", TextToSpeech.QUEUE_FLUSH,null,null);
                                        //openLoginActivity();
                                    }
                                    if(str.equals("what's the status of the machine") || str.equals("status of the machine") ){
                                        editText.setText("Hello boss, the machine is");
                                        t1.speak("Hello boss, the machine is "+status, TextToSpeech.QUEUE_FLUSH,null,null);
                                        //openLoginActivity();
                                    }
                                    if(str.equals("what's my today's income") || str.equals("today's income") ){
                                        editText.setText("Hello boss, how can i help you");
                                        t1.speak("Your today's income sir is "+counts3+".00 Pesos", TextToSpeech.QUEUE_FLUSH,null,null);
                                        //openLoginActivity();
                                    }
                                    if(str.equals("what's the changer") || str.equals("changer") ){
                                        editText.setText("Hello boss, how can i help you");
                                        t1.speak("Your coin stack sir is "+coinstack+".00 Pesos", TextToSpeech.QUEUE_FLUSH,null,null);
                                        //openLoginActivity();
                                    }
                                    if(str.equals("what's the water level") || str.equals("water level") || str.equals("today's water level")){
                                        editText.setText("Hello boss, how can i help you");
                                        t1.speak("The Water level sir is "+waterlvl+"%", TextToSpeech.QUEUE_FLUSH,null,null);
                                        //openLoginActivity();
                                    }
                                    //t1.speak(str, TextToSpeech.QUEUE_FLUSH,null,null);
                                }
                            },1000);
                        }else{
                            Toast.makeText(DashboardActivity.this,"Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DashboardActivity.this,"Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                });
                Timer timer = new Timer();
                timer.schedule(new TimerTask(){
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run(){
                        String  str = editText.getText().toString();
                        Date currentTime = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(currentTime);
                        int timeOfDay = cal.get(Calendar.HOUR_OF_DAY);
                        int minute = cal.get(Calendar.MINUTE);


                        if(str.equals("hello jarvis") || str.equals("jarvis") || str.equals("wake up jarvis") || str.equals("jarvis do me a favor")){
                            editText.setText("Hello boss, how can i help you");
//                            t1.speak("Hello sir, how can i help you?", TextToSpeech.QUEUE_FLUSH,null,null);
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music2jarvis);
                            mediaPlayer.start();
                            //openLoginActivity();
                        }else if(str.equals("hello") || str.equals("hi") || str.equals("wake up jarvis") || str.equals("jarvis do me a favor")){
                            editText.setText("Hello boss, how can i help you");
                            try {
//                                t1.speak("Hello boss, I am Jarvis, Jay Tunza's Artificial Intelligence and Security System, Would you like to know what i can do?", TextToSpeech.QUEUE_FLUSH,null,null);
                                final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music4jarvis);
                                mediaPlayer.start();
                                Thread.sleep(1000);
                                if(str.equals("yes") || str.equals("yes jarvis")){
                                    editText.setText("Hello boss, how can i help you");
                                    t1.speak("Jarvis Stands For, Just A Rather Very Intelligent System, I am a virtual assistant technology help you assist this application System like Monitoring, Status of the Machine and Security.", TextToSpeech.QUEUE_FLUSH,null,null);
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }else if(str.equals("hello friday") || str.equals("friday") || str.equals("wake up friday") || str.equals("fridaydo me a favor")){
                            editText.setText("Hello boss, how can i help you");
                            t1.speak("Hello boss, how can i help you?", TextToSpeech.QUEUE_FLUSH,null,null);
//                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music2jarvis);
//                            mediaPlayer.start();
                            //openLoginActivity();
                        }else if(str.equals("yes") || str.equals("yes jarvis")){
                            editText.setText("Hello boss, how can i help you");
//                            t1.speak("Jarvis Stands For, Just A Rather Very Intelligent System, I am a virtual assistant technology help you assist this application System like Monitoring, Status of the Machine and Security.", TextToSpeech.QUEUE_FLUSH,null,null);
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music5jarvis);
                            mediaPlayer.start();
                        }
                        else if(str.equals("are you there jarvis")){
                            editText.setText("at, your, service sir");
//                            t1.speak("Yes boss, always at your service...", TextToSpeech.QUEUE_FLUSH,null,null);
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music6jarvis);
                            mediaPlayer.start();
                            //openLoginActivity();
                        }
                        else if(str.equals("jarvis on air")){
                            editText.setText("at your service sir");
//                            t1.speak("at your service sir!", TextToSpeech.QUEUE_FLUSH,null,null);
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music14jarvis);
                            mediaPlayer.start();
                            //openLoginActivity();
                        }
                        else if(str.equals("what is date today")){
                            editText.setText(" "+currentDate);
                            t1.speak(" "+currentDate, TextToSpeech.QUEUE_FLUSH,null,null);
                            //openLoginActivity();
                        }
                        else if(str.equals("what time is it")){
                            if(timeOfDay >= 0 && timeOfDay < 12){
                                // It's morning
                                editText.setText(" "+timeOfDay);
                                t1.speak("Good Morning sir, the time is "+timeOfDay+minute+"AM", TextToSpeech.QUEUE_FLUSH,null,null);
                            } else if(timeOfDay >= 12 && timeOfDay < 17){
                                // It's afternoon
                                editText.setText(" "+timeOfDay);
                                t1.speak("Good Afternoon sir, the time is "+timeOfDay+minute+"PM", TextToSpeech.QUEUE_FLUSH,null,null);
                            } else if(timeOfDay >= 17 && timeOfDay < 24){
                                // It's evening
                                editText.setText(" "+timeOfDay);
                                t1.speak("Good Evening sir, the time is "+timeOfDay+minute+"PM", TextToSpeech.QUEUE_FLUSH,null,null);
                            }
                        }
                        else if(str.equals("thank you jarvis")){
                            editText.setText("My pleasure, Your highness");
//                            t1.speak("My pleasure Your highness...", TextToSpeech.QUEUE_FLUSH,null,null);
                            //openLoginActivity();
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music7jarvis);
                            mediaPlayer.start();
                        }else if(str.equals("thank you friday")){
                            editText.setText("My pleasure, Your highness");
                            t1.speak("You're welcome! If you have any further questions or concerns, feel free to ask.", TextToSpeech.QUEUE_FLUSH,null,null);
                            //openLoginActivity();
//                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music7jarvis);
//                            mediaPlayer.start();
                        }
                        else if(str.equals("open message") || str.equals("can you open the message")){
                            editText.setText("Yes boss, opening the message page");
                            t1.speak("Yes boss, opening the message page", TextToSpeech.QUEUE_FLUSH,null,null);
                            replacedFragment(new MessageFragment());
                        }
                        else if(str.equals("open profile") || str.equals("can you open the profile")){
                            editText.setText("Yes boss, opening the profile page");
                            t1.speak("Yes boss, opening the profile page", TextToSpeech.QUEUE_FLUSH,null,null);
                            replacedFragment(new ProfileDetailsFragment());
                        }
                        else if(str.equals("open gps") || str.equals("can you open the gps")){
                            editText.setText("Yes boss, opening the gps page");
                            t1.speak("Yes boss, opening the gps page", TextToSpeech.QUEUE_FLUSH,null,null);
                            replacedFragment(new LocationTracker());
                        }
                        else if(str.equals("open home") || str.equals("can you open the home")){
                            editText.setText("Yes boss, opening the home page");
                            t1.speak("Yes boss, opening the home page", TextToSpeech.QUEUE_FLUSH,null,null);
                            replacedFragment(new HomeFragment());
                            //onMessage();
                        }
                        else if(str.equals("open tracker") || str.equals("can you open the tracker")){
                            editText.setText("Yes boss, opening the notifications");
                            t1.speak("Yes boss, opening the tracker page", TextToSpeech.QUEUE_FLUSH,null,null);
                            replacedFragment(new NotificationFragment());
                        }
                        else if(str.equals("open cam") || str.equals("can you open the mcam")){
                            editText.setText("Yes boss, opening the surveillance cam page");
                            t1.speak("Yes boss, opening the surveillance cam page", TextToSpeech.QUEUE_FLUSH,null,null);
                            replacedFragment(new SettingsFragment());
                        }
                        else if(str.equals("turn on the lights") || str.equals("can you turn on the lights")){
                            editText.setText("Yes boss, opening the lights");
                            t1.speak("Yes boss, opening the lights", TextToSpeech.QUEUE_FLUSH,null,null);
                            //openLoginActivity();
                        }
                        else if(str.equals("turn on the machine") || str.equals("can you turn on the machine") || str.equals("can you turn on the machine please")){
                            editText.setText("Yes boss, the machine is now turning on");
                            //t1.speak("Yes boss, the machine is now turning on", TextToSpeech.QUEUE_FLUSH,null,null);
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music8jarvis);
                            mediaPlayer.start();
                            TurnOnMachine();
                        }
                        else if(str.equals("turn off the machine") || str.equals("can you turn off the machine") || str.equals("can you turn off the machine please")){
                            editText.setText("Yes boss, the machine is now turning off");
                            //t1.speak("Yes boss, the machine is now turning off", TextToSpeech.QUEUE_FLUSH,null,null);
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music9jarvis);
                            mediaPlayer.start();
                            TurnOffMachine();
                        }
                        else if(str.equals("logout account") || str.equals("can you logout my account")){
                            editText.setText("Yes boss, logging out the account");
                            t1.speak("Yes boss, logging out the account", TextToSpeech.QUEUE_FLUSH,null,null);
                            onLogOut();
                        }
                        else if(str.equals("what is your name") || str.equals("what's your name")){
                            editText.setText("My name is jarvis sir");
                            //t1.speak("You can call me jarvis , I am An Artificial Intelligence created by Mr Jay Tunza, and I am here to assist you!", TextToSpeech.QUEUE_FLUSH,null,null);
                            final MediaPlayer mediaPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.music10jarvis);
                            mediaPlayer.start();
                        }
                        else if(str.equals("how are you")){
                            editText.setText("I m a robot, i am never tired, boss");
                            t1.speak("I m a robot, i am never tired, boss", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("shutdown the app")){
                            editText.setText("Yes boss");
                            t1.speak("Yes boss", TextToSpeech.QUEUE_FLUSH,null,null);
                            onExit();
                        }
                        else if(str.equals("what can I ask you")){
                            editText.setText("you can ask me anything to help you");
                            t1.speak("you can ask me anything to help you", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("tell me a joke")){
                            editText.setText("two sheep said baa in the field, other one said shit i wanna say that");
                            t1.speak("two sheep said baa in the field, other one said shit i wanna say that", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("do you like Trump")){
                            editText.setText("i will never consider him as a role model");
                            t1.speak("i will never consider him as a role model", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("what is the situation")){
                            editText.setText("there is an ambush, retreat sir");
                            t1.speak("there is an ambush, retreat sir", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else{
                            //t1.speak("Sorry, i did not understand that command.", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        //t1.speak(str, TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                },1000);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Set<String> a=new HashSet<>();
                a.add("female");//here you can give male if you want to select male voice.
                Voice v=new Voice("en-us-x-sfg#male_2-local",new Locale("en","US"),400,400,false,a);
                if(status!=TextToSpeech.ERROR){
                    t1.setLanguage(Locale.US);
                    t1.setVoice(new Voice("en-us-x-sfg#male_2-local", Locale.US, 400, 200, true, null));
                }
//                Locale language = new Locale("en", "US");
//                int result = t1.setLanguage(language);
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    // Language not supported
//                } else {
//                    Voice voice = new Voice("en-us-x-sfg#male_1-local", language, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_LOW, false, null);
//                    t1.setVoice(voice);
//                }

            }
        });



        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        editText.setHint("you will see input here");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        editText.setText("");
                        editText.setHint("listening........");
                        speechRecognizer.startListening(speechRecognizerIntent);
                        break;
                }
                return false;
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting User Reference from Database "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                //DataSnapshot dataSnapshot = snapshot.getValue();

                if(readUserDetails != null){
                    fullname = firebaseUser.getDisplayName();
                    //textView_Welcome.setText(""+fullname);
                    String counts = String.valueOf(snapshot.child("counts").getValue());
                    //textView_Count.setText("₱"+counts+".00");

                    //String url = "https://scontent-hkt1-2.xx.fbcdn.net/v/t39.30808-6/279489781_3304709659818340_5139564566068014277_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_eui2=AeEChFfjUHs3ZFPbFb-1ug9YwjTV-Vhn1jDCNNX5WGfWMIia9mgbHdyUC0eQzfxVG6UHHuxuMj3DdhfxRti8Prrl&_nc_ohc=Dzqr9yjP--cAX_Op2na&_nc_ht=scontent-hkt1-2.xx&oh=00_AfD94D89i4zwvnIYdE9sOv4K9b04wc4W7MVlxid1R6hNWQ&oe=64184A30";
                    //Picasso.get().load(url).fit().centerCrop().into(imageView);
                }else{
                    Toast.makeText(DashboardActivity.this,"Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this,"Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void TurnOnMachine(){
        try {
            outputStream.write("1".getBytes());
        }catch (Exception e){

        }
    }

    private void TurnOffMachine(){
        try {
            outputStream.write("0".getBytes());
        }catch (Exception e){

        }
    }

    private void ConnectBluetooth(){
                try {
                    if (ActivityCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        bluetoothSocket.connect();

                        inputStream = bluetoothSocket.getInputStream();
                        outputStream = bluetoothSocket.getOutputStream();
                        rxThread.start();
                        BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
                        Toast.makeText(DashboardActivity.this,"Connected.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }catch (Exception e){

                }
    }

    private void ConnectDevice(){
        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

            for (BluetoothDevice dev : devices) {
                if (dev.getName().equals("HC-05")) {
                    bluetoothDevice = dev;
                    bluetoothAdapter.cancelDiscovery();
                    break;
                }
            }
            return;
        }
    }

    BroadcastReceiver Btreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    DashboardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Connect.setEnabled(true);
                        }
                    });
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    rxThread.isRunning = false;
                    break;
            }
        }
    };

    private void onLogOut(){
        authProfile.signOut();
        Toast.makeText(DashboardActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void onExit(){
        finishAffinity();
    }

    private void onMessage(){
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            replacedFragment(new HomeFragment());
            return true;
        });
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void replacedFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
//        }
//    }

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

//        if(id == R.id.menu_refresh){
//            //Refresh activity
//            startActivity(getIntent());
//            finish();
//            overridePendingTransition(0,0);
//        }
         if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DashboardActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(DashboardActivity.this,"Something went wrong!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class RxThread extends Thread{
        public boolean isRunning;
        byte[] rx;
        RxThread(){
            isRunning = true;
            rx = new byte[10];
        }
        @Override
        public void run(){
            while(isRunning){
                try {
                    if(inputStream.available() > 0){
                        inputStream.read(rx);
                        RxData = new String(rx);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!RxData.equals("")){
                                //InputText2.setText(RxData);
                                RxData = "";
                            }
                        }
                    });
                    Thread.sleep(10);
                }catch (Exception e){}
            }
        }
    }
}