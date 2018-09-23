package com.ac.mdbsocials;

import java.io.Serializable;

public class Social implements Serializable {
    String eventName;
    String photoURL;
    String email;
    String eventDescription;
    int rsvpNum;

    public Social(String email, String eventName, String eventDescription, String photoURL, int rsvpNum) {
        this.email = email;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.photoURL = photoURL;
        this.rsvpNum = rsvpNum;
    }
}
