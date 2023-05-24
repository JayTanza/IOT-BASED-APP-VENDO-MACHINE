package com.example.jayvisiotapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText S_fullname, S_dob, S_email, S_contactnumber, S_password, S_cpassword;
//    private ProgressBar progressBar;
    private DatePickerDialog picker;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //getSupportActionBar().setTitle("Register");

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();
//       progressBar = findViewById(R.id.progressBar);
        S_fullname = findViewById(R.id.signup_name);
        S_dob = findViewById(R.id.signup_DOB);
        S_email = findViewById(R.id.signup_email);
        S_contactnumber = findViewById(R.id.signup_hpnumber);
        S_password = findViewById(R.id.signup_password);
        S_cpassword= findViewById(R.id.signup_confirmpassword);

        //Setting up Datepicker on EditText
        S_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        S_dob.setText(dayOfMonth + "/" + (month + 1) + "/"+year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.signup_button);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //obtaining the entered data
                String textFullname = S_fullname.getText().toString();
                String textDOB = S_dob.getText().toString();
                String textEmail = S_email.getText().toString();
                String textMobile = S_contactnumber.getText().toString();
                String textPassword= S_password.getText().toString();
                String textConfirmpassword = S_cpassword.getText().toString();

                //Validate Mobile number using Matcher ands patterns (Regular Expression)
                String mobileRegex = "[0-6][0-9]{10}"; //First no. can be {6,8,9} and 9 rest nos. can be any no.
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                //Display error when the textbox is empty
                if(TextUtils.isEmpty(textFullname)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    S_fullname.setError("Full name is required");
                    S_fullname.requestFocus();
                }else if(TextUtils.isEmpty(textDOB)){
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    S_dob.setError("Date of birth is required");
                    S_dob.requestFocus();
                }else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    S_email.setError("Email is required");
                    S_email.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "Please enter your contact number", Toast.LENGTH_LONG).show();
                    S_contactnumber.setError("Mobile number is required");
                    S_contactnumber.requestFocus();
                }else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "Please enter your contact number", Toast.LENGTH_LONG).show();
                    S_contactnumber.setError("Mobile number is not valid");
                    S_contactnumber.requestFocus();
                }else if(textMobile.length() != 11){
                    Toast.makeText(RegisterActivity.this, "Please re enter your contact number", Toast.LENGTH_LONG).show();
                    S_contactnumber.setError("Mobile number should be 11 digits");
                    S_contactnumber.requestFocus();
                }else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    S_password.setError("Password is required");
                    S_password.requestFocus();
                }else if(textPassword.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password should be atleast 6 digits", Toast.LENGTH_LONG).show();
                    S_password.setError("Password is too weak");
                    S_password.requestFocus();
                }else if(TextUtils.isEmpty(textConfirmpassword)){
                    Toast.makeText(RegisterActivity.this, "Please enter again your password", Toast.LENGTH_LONG).show();
                    S_cpassword.setError("Password Confirmation is required");
                    S_cpassword.requestFocus();
                }else if(!textPassword.equals(textConfirmpassword)){
                    Toast.makeText(RegisterActivity.this, "Please enter the same password", Toast.LENGTH_LONG).show();
                    S_cpassword.setError("Password doesn't match");
                    S_cpassword.requestFocus();
                    //clear entered passwords
                    S_password.clearComposingText();
                    S_cpassword.clearComposingText();
                }else{
//                    progressBar.setVisibility(View.VISIBLE);
                      registerUser(textFullname, textDOB, textEmail, textMobile, textPassword, textConfirmpassword);
                }
            }
        });
    }

    //Register user using the credentials given
    private void registerUser(String textFullname, String textDOB, String textEmail, String textMobile, String textPassword, String textConfirmpassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Create user profile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
//                  Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Update Display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullname).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter User Data into the firebase realtime database.
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDOB, textMobile);

                    //Extracting User reference from Database for 'Registered users'
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    //child references like. Fullname, DoB, Mobilenumber
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Send Verification Email
                            firebaseUser.sendEmailVerification();
                            Toast.makeText(RegisterActivity.this, "User registered successfully, Please verify your email", Toast.LENGTH_LONG).show();

                            //Open user profile after successful registration
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                            //To prevent user from returning back to register activity on pressing back button after registration
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            //to close the register activity
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, "User registered failed, Please try again", Toast.LENGTH_LONG).show();
                        }
                        }
                    });
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        S_password.setError("Your password is too weak. Kindly use a mix of alphabet, numbers and special characters");
                        S_password.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        S_email.setError("Your email is invalid or already in use. Kindly re enter or use another email");
                        S_email.requestFocus();
                    }catch(FirebaseAuthUserCollisionException e){
                        S_email.setError("User is already registered with this email. User another email.");
                        S_email.requestFocus();
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}