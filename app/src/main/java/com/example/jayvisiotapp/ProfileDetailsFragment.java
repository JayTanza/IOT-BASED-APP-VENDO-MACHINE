package com.example.jayvisiotapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.squareup.picasso.Picasso;

public class ProfileDetailsFragment extends Fragment {
    private ImageView imageView;
    private RelativeLayout rl1;

    private FirebaseAuth authProfile;
    private TextView fullname, email, doB, mobile;
    private String fullname1, email2, doB3, mobile4;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);

        fullname = (TextView) view.findViewById(R.id.profile_name);
        email = (TextView) view.findViewById(R.id.profile_email);
        doB = (TextView) view.findViewById(R.id.profile_DOB);
        mobile = (TextView) view.findViewById(R.id.profile_hpnumber);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        imageView = (ImageView) view.findViewById(R.id.ProfilePic1);
        rl1 = (RelativeLayout) view.findViewById(R.id.bgphoto1);

        String url = "https://scontent.fceb2-2.fna.fbcdn.net/v/t39.30808-6/279489781_3304709659818340_5139564566068014277_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_eui2=AeEChFfjUHs3ZFPbFb-1ug9YwjTV-Vhn1jDCNNX5WGfWMIia9mgbHdyUC0eQzfxVG6UHHuxuMj3DdhfxRti8Prrl&_nc_ohc=jocXScPwOK4AX96DfSv&_nc_ht=scontent.fceb2-2.fna&oh=00_AfDNPfMS8itbheM6tn9E_-44zb1mn5KeFsTwt7MDoWYcSQ&oe=6437EE30";
        Picasso.get().load(url).fit().centerCrop().into(imageView);

        String url2 = "https://scontent.fceb2-1.fna.fbcdn.net/v/t39.30808-6/289638934_3353104721645500_4293305658402288516_n.jpg?_nc_cat=101&ccb=1-7&_nc_sid=e3f864&_nc_eui2=AeGeds29X19gT10ST4AAByQuCYdmEhNGICcJh2YSE0YgJ7aaTuGVe5aKNEXoOB_1Q_V-Epgj1fqlKPv3saXxR77T&_nc_ohc=DRpbDkgB8rEAX-DN4xx&_nc_ht=scontent.fceb2-1.fna&oh=00_AfD7x0DuxLxG5ezvETvaW-EY_L8u49P5niXTCGMtOAZCXw&oe=641A4B5A";
        //Picasso.get().load(url2).fit().centerCrop().into(rl1);
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
                if(readUserDetails != null){
                    fullname1 = firebaseUser.getDisplayName();
                    email2 = firebaseUser.getEmail();
                    doB3 = readUserDetails.Dateofbirth;
                    mobile4 = readUserDetails.Mobilenumber;

                    fullname.setText(""+fullname1);
                    email.setText(""+email2);
                    mobile.setText(""+mobile4);
                    doB.setText(""+doB3);

                    String url = "https://scontent.fceb2-2.fna.fbcdn.net/v/t39.30808-6/279489781_3304709659818340_5139564566068014277_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_eui2=AeEChFfjUHs3ZFPbFb-1ug9YwjTV-Vhn1jDCNNX5WGfWMIia9mgbHdyUC0eQzfxVG6UHHuxuMj3DdhfxRti8Prrl&_nc_ohc=mOk_rcJzitEAX9Rc43m&_nc_ht=scontent.fceb2-2.fna&oh=00_AfApsU3y2pxTNOwX8G5rtN4otAxN9_KBH-0ry9vG9ylrcw&oe=644BB4B0";
                    Picasso.get().load(url).fit().centerCrop().into(imageView);

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