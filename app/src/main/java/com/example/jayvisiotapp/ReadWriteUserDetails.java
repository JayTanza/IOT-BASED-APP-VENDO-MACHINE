package com.example.jayvisiotapp;

public class ReadWriteUserDetails {
    public String Dateofbirth, Fullname, Mobilenumber, Counts;

    //constructor
    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(String textDob, String textMobilenumber){
        this.Dateofbirth = textDob;
        //this.Fullname = textFullname;
        this.Mobilenumber = textMobilenumber;
        //this.Counts = textCounts;
    }
}
