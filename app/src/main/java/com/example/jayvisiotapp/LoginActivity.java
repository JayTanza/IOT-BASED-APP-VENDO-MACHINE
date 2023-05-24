package com.example.jayvisiotapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginEmail, editTextLoginPwd;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";
    private TextToSpeech textToSpeech;
    private boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().setTitle("Login");

        editTextLoginEmail = findViewById(R.id.login_username);
        editTextLoginPwd = findViewById(R.id.login_password);

        //This check if the user is already loging in
        authProfile = FirebaseAuth.getInstance();

        //Visibility password
        editTextLoginPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=editTextLoginPwd.getRight()-editTextLoginPwd.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = editTextLoginPwd.getSelectionEnd();
                        if(passwordVisible){
                            editTextLoginPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24,0,R.drawable.baseline_visibility_24, 0);
                            //for hide password
                            editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            editTextLoginPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24,0,R.drawable.baseline_visibility_off_24, 0);
                            //for hide password
                            editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        editTextLoginPwd.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        //Signup page
        TextView buttonSignup = findViewById(R.id.signupRedirectText);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You are in Signup Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish(); //close login activity
            }
        });
        //ForogotPassword page
        TextView buttonForgotPasword = findViewById(R.id.forgotpasswordRedirectText);
        buttonForgotPasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You are in Forgot Password Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
                finish(); //close login activity
            }
        });
        //login user
        Button buttonLogin = findViewById(R.id.login_button);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    initTextToSpeech4();
                    editTextLoginEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is required");
                    initTextToSpeech4();
                    editTextLoginEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required");
                    initTextToSpeech5();
                    editTextLoginPwd.requestFocus();
                }else{
                    loginUser(textEmail, textPwd);
                }
            }
        });

    }


    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Get instance of the current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    //Check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "You are login now", Toast.LENGTH_SHORT).show();
                        initTextToSpeech3();
                        //Open user profile
                        startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                        finish(); //close login activity
                    }else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); //Signout user
                        showAlertDialog();
                    }
                }else{
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User doesn't exist or no longer valid. Please register again");
                        initTextToSpeech2();
                        editTextLoginEmail.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        editTextLoginPwd.setError("Invalid Password kindly check and try again");
                        initTextToSpeech();
                        editTextLoginPwd.requestFocus();
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void jarvisWelcome(){
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.music1jarvis);
        mediaPlayer.start();
    }
    private void showAlertDialog() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        //Open email apps if user clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);//Entry point for application
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//To email app in new window and not within our app
                startActivity(intent);
            }
        });
        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();
    }

    //Check if user is already logged in.
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Already logged in!", Toast.LENGTH_SHORT).show();
            //Start the userProfileActivity
            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
            finish(); //close login activity
            initTextToSpeech6();
        }else{
            Toast.makeText(LoginActivity.this, "You can login now!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak(String msg) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void initTextToSpeech(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(LoginActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }else{
                    speak("Invalid Password kindly check and try again");
                }
            }
        });
    }

    private void initTextToSpeech2(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(LoginActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }else{
                    speak("User doesn't exist or no longer valid. Please register");
                }
            }
        });
    }

    private void initTextToSpeech3(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(LoginActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }else{
                    speak("You are login now");
                }
            }
        });
    }

    private void initTextToSpeech4(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(LoginActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }else{
                    speak("Valid Email is required");
                }
            }
        });
    }

    private void initTextToSpeech5(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(LoginActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }else{
                    speak("Please enter your password");
                }
            }
        });
    }

    private void initTextToSpeech6(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(LoginActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }else{
                    //speak("Welcome back sir, your are logging in now");
                    jarvisWelcome();
                }
            }
        });
    }
}