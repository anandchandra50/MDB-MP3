package com.ac.mdbsocials;

import java.io.Serializable;

public class Social implements Serializable {
    String id;
    String eventName;
    String photoURL;
    String email;
    String eventDescription;
    int rsvpNum;
    int day;
    int month;
    int year;
    long timestamp;

    public Social(String id, String email, String eventName,
                  String eventDescription, String photoURL,
                  int rsvpNum, int day, int month, int year, long timestamp) {
        this.id = id;
        this.email = email;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.photoURL = photoURL;
        this.rsvpNum = rsvpNum;
        this.day = day;
        this.month = month;
        this.year = year;
        this.timestamp = timestamp;
    }
}
