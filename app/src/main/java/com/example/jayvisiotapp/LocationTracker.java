package com.example.jayvisiotapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationTracker extends Fragment {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    TextView address, city, country, longitude, latitude;
    Button getLocation;

    WebView getViewLocation;
    private final static int REQUEST_CODE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_location_tracker, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check if the app has permission to access the user's location

        address = (TextView) myView.findViewById(R.id.editText1);
        city = (TextView) myView.findViewById(R.id.editText2);
        country = (TextView) myView.findViewById(R.id.editText3);
        latitude = (TextView) myView.findViewById(R.id.editText4);
        longitude = (TextView) myView.findViewById(R.id.editText5);
        getLocation = (Button) myView.findViewById(R.id.btn4);
        getViewLocation = (WebView) myView.findViewById(R.id.LocationView);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getLastLocation();
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    // Get the latitude and longitude
                    double latitude1 = location.getLatitude();
                    double longitude1 = location.getLongitude();
                    // Use Geocoder to get the address line
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude1, longitude1, 1);
                        if (addresses != null && addresses.size() > 0) {
                            latitude.setText("Lagitude :" +addresses.get(0).getLatitude());
                            longitude.setText("Longitude :"+addresses.get(0).getLongitude());
                            address.setText("Address :"+addresses.get(0).getAddressLine(0));
                            city.setText("City :"+addresses.get(0).getLocality());
                            country.setText("Country :"+addresses.get(0).getCountryName());
                            // Display the user's location on a map
                            getViewLocation.getSettings().setJavaScriptEnabled(true);
                            getViewLocation.loadUrl("https://www.google.com/maps/place/University+Of+Cebu+-+Lapu-Lapu+and+Mandaue/@10.3251001,123.9509015,771m/data=!3m2!1e3!4b1!4m6!3m5!1s0x33a9984556417115:0x8578d051dbbdf9e0!8m2!3d10.3250948!4d123.9530902!16s%2Fg%2F1tdz6ymt" + latitude1 + "," + longitude1 + "&zoom=15");
                        } else {
                            Toast.makeText(getActivity(), "Address not available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return myView;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            // Get the latitude and longitude
            double latitude1 = location.getLatitude();
            double longitude1 = location.getLongitude();
            // Use Geocoder to get the address line
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude1, longitude1, 1);
                if (addresses != null && addresses.size() > 0) {
                    latitude.setText("Lagitude :" +addresses.get(0).getLatitude());
                    longitude.setText("Longitude :"+addresses.get(0).getLongitude());
                    address.setText("Address :"+addresses.get(0).getAddressLine(0));
                    city.setText("City :"+addresses.get(0).getLocality());
                    country.setText("Country :"+addresses.get(0).getCountryName());
                    // Display the user's location on a map
                    getViewLocation.getSettings().setJavaScriptEnabled(true);
                    getViewLocation.loadUrl("https://www.google.com/maps/place/University+Of+Cebu+-+Lapu-Lapu+and+Mandaue/@10.3251001,123.9509015,771m/data=!3m2!1e3!4b1!4m6!3m5!1s0x33a9984556417115:0x8578d051dbbdf9e0!8m2!3d10.3250948!4d123.9530902!16s%2Fg%2F1tdz6ymt" + latitude1 + "," + longitude1 + "&zoom=15");
                } else {
                    Toast.makeText(getActivity(), "Address not available", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(getActivity(), "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}