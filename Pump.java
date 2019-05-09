package com.example.BPBuddy;

import java.io.Serializable;

// Object that is generated each pumping session
// Contains the amount of milk pumped and stored, along with a timestamp
public class Pump implements Serializable {
    int ounces_pumped;
    int ounces_stored;
    String time;

    // Constructed with all three values
    Pump(int ozPm, int ozSt, String t)
    {
        ounces_pumped = ozPm;
        ounces_stored = ozSt;
        time = t;
    }
}
