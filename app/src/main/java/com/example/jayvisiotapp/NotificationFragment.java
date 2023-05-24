package com.example.jayvisiotapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


public class NotificationFragment extends Fragment {

    private TextView textView_Welcome, textView_Changer, textView_TotalIncome,textView_DailyIncome,  textView_WaterLevel, textViewStatus;
    private String fullname, email, doB, mobile, count;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private FirebaseDatabase database;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        textView_DailyIncome = view.findViewById(R.id.counts);
        textViewStatus = view.findViewById(R.id.machinestatus);
        textView_TotalIncome = view.findViewById(R.id.counts2);
        textView_Changer = view.findViewById(R.id.changer);
        textView_WaterLevel = view.findViewById(R.id.waterlevel);
        textView_Welcome = view.findViewById(R.id.textviewWelcome2);
        //imageView = (ImageView) view.findViewById(R.id.imageViewProfile);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(getActivity(),"Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show();
        }else{
            showUserProfile(firebaseUser);
        }
        return view;

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
                    textView_Welcome.setText(""+fullname);
                    String counts = String.valueOf(snapshot.child("TotalIncome").getValue());
                    textView_TotalIncome.setText("₱"+counts+".00");
                    String counts2 = String.valueOf(snapshot.child("DailyIncome/DayIncome").getValue());
                    textView_DailyIncome.setText("₱"+counts2+".00");
                    String counts3 = String.valueOf(snapshot.child("CoinStack").getValue());
                    textView_Changer.setText("₱"+counts3+".00");
                    String waterlvl = String.valueOf(snapshot.child("WaterLevel").getValue());
                    textView_WaterLevel.setText(waterlvl+" %");
                    String machine_status = String.valueOf(snapshot.child("Machine Status").getValue());
                    textViewStatus.setText(machine_status);
                    //String url = "https://scontent-hkt1-2.xx.fbcdn.net/v/t39.30808-6/279489781_3304709659818340_5139564566068014277_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_eui2=AeEChFfjUHs3ZFPbFb-1ug9YwjTV-Vhn1jDCNNX5WGfWMIia9mgbHdyUC0eQzfxVG6UHHuxuMj3DdhfxRti8Prrl&_nc_ohc=Dzqr9yjP--cAX_Op2na&_nc_ht=scontent-hkt1-2.xx&oh=00_AfD94D89i4zwvnIYdE9sOv4K9b04wc4W7MVlxid1R6hNWQ&oe=64184A30";
                    //Picasso.get().load(url).fit().centerCrop().into(imageView);
                }else{
                    Toast.makeText(getActivity(),"Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }
}