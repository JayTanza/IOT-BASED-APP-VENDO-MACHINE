package com.example.jayvisiotapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class AlarmFragment extends Fragment {
    Button Turnonbtn, Turnonbtn2;
    private FirebaseAuth authProfile;
    private FirebaseDatabase database;
    private StorageReference storageReference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_alarm, container, false);
        Turnonbtn = (Button) myView.findViewById(R.id.btn4);
        Turnonbtn2 = (Button) myView.findViewById(R.id.btn3);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        Turnonbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.alarm1);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // First audio has finished playing, play the second audio file
                        final MediaPlayer mediaPlayer2 = MediaPlayer.create(getActivity(), R.raw.alarmvoice);
                        mediaPlayer2.start();
                        // Release the first MediaPlayer object to free up system resources
                        mediaPlayer.release();
                        sendDataAlarm(firebaseUser);
                    }
                });
                // Start playing the first audio file
                mediaPlayer.start();
            }
        });

        Turnonbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.alarm1);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // First audio has finished playing, play the second audio file
                        final MediaPlayer mediaPlayer2 = MediaPlayer.create(getActivity(), R.raw.alarm2voice);
                        mediaPlayer2.start();
                        // Release the first MediaPlayer object to free up system resources
                        mediaPlayer.release();
                    }
                });
                // Start playing the first audio file
                mediaPlayer.start();
            }
        });
        return myView;
    }

    private void sendDataToFirebase(){
        // Send data to Firebase here
        // Example for sending a string value to Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseRef.child("myData").setValue("Data sent from app");
    }

    private void sendDataAlarm(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    String Alarm = "Under Attack";
                    snapshot.child("Machine Status").getRef().setValue(Alarm); // Set the value of Machine Status to "Under Attack"
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snapshot.child("Machine Status").getRef().setValue("Good Condition"); // Set the value of Machine Status to "Good Condition" after 1 minute
                        }
                    }, 60000);
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

}