package com.example.jayvisiotapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

public class MessageFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View myView = inflater.inflate(R.layout.fragment_message, container, false);

       WebView myWebView = myView.findViewById(R.id.webview1);
       myWebView.loadUrl("https://jayvisiotvendo.netlify.app/");
       WebSettings webSettings = myWebView.getSettings();
       webSettings.setJavaScriptEnabled(true);
       return myView;
    }
}