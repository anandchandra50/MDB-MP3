package com.ac.mdbsocials;

import java.io.Serializable;

public class Social implements Serializable {
    String id;
    String eventName;
    String photoURL;
    String email;
    String eventDescription;
    int rsvpNum;
    long timestamp;

    public Social(String id, String email, String eventName, String eventDescription, String photoURL, int rsvpNum, long timestamp) {
        this.id = id;
        this.email = email;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.photoURL = photoURL;
        this.rsvpNum = rsvpNum;
        this.timestamp = timestamp;
    }
}
