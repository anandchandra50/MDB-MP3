package com.ac.mdbsocials;

public class Social {
    String eventName;
    String photoURL;
    String email;
    int rsvpNum;

    public Social(String eventName, String photoURL, String email, int rsvpNum) {
        this.eventName = eventName;
        this.photoURL = photoURL;
        this.email = email;
        this.rsvpNum = rsvpNum;
    }
}
