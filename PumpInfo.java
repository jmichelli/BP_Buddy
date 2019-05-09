package com.example.BPBuddy;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Global data model to hold user information
// Tracks the current date, a list of the day's sessions, and a log of all previous logged days
// Contains methods to save/load from a file, modify the day, total pumped and stored amounts,
//      and total freezer stash
public class PumpInfo implements Serializable {
    String currentDate;
    HashMap<String, ArrayList<Pump>> days;
    ArrayList<Pump> todayPumps;

    PumpInfo() {
        currentDate = "Uninitialized Date";
        days = new HashMap<>();
        todayPumps = new ArrayList<>();
    }

    PumpInfo(String d) {
        currentDate = d;
        days = new HashMap<>();
        todayPumps = new ArrayList<>();
    }

    // Totals amount of milk pumped on the current day
    protected int countDailyPumped() {
        int totalPumped = 0;

        for (Pump p : todayPumps) {
            totalPumped += p.ounces_pumped;
        }

        return totalPumped;
    }

    // Totals amount of milk stored on the current day
    protected int countDailyStored() {
        int totalStored = 0;

        for (Pump p : todayPumps) {
            totalStored += p.ounces_stored;
        }

        return totalStored;
    }

    // Determines total freezer stash amount based on all session entries
    protected int totalFreezerStash() {
        int freezerTotal = 0;

        // Loops through all day entries for lists of sessions, then loops through all sessions
        // Totals all session storage
        for (Map.Entry<String, ArrayList<Pump>> entry : days.entrySet()) {
            for (Pump p : entry.getValue()) {
                freezerTotal += p.ounces_stored;
            }
        }

        return freezerTotal;
    }

    // Uses a tree to sort the timestamps for ordered display
    protected void sortDailySessions(){
        // TreeMap to hold a numeric timestamp and a pump object for sorting
        TreeMap<Integer, Pump> temp = new TreeMap<>();

        // Iterates over all pump sessions in the daily log
        for (Pump p : todayPumps){
            int numeric; // Numeric timestamp value
            String buffer = ""; // Holds text timestamp as it is formatted and parsed

            // Pattern to extract digits from string
            Pattern patt = Pattern.compile("\\d+");
            Matcher mat = patt.matcher(p.time);

            // Iterates over string, finding digits and adding them to the buffer
            while(mat.find()) {
                buffer += mat.group();
            }

            // Parse sanitized buffer into integer form
            numeric = Integer.parseInt(buffer);

            // Converts PM to 24hr calues
            if(p.time.contains("PM")){
                numeric += 1200;

                if(numeric >= 2400){
                    numeric -= 1200;
                }
            } else if(numeric > 1200){
                numeric -= 1200;
            }

            // Adds the numeric representation of the timestamp as a key
            // Adds the original Pump object as the value
            // The tree automatically sorts these entries as they come in
            temp.put(numeric, p);
        }

        // Clears unordered list and replaces with that sorted by the tree
        todayPumps.clear();
        todayPumps.addAll(temp.values());

    }


    // Saves current day's sessions to the hashmap and serializes the hashmap to a file
    protected void saveDay(PumpInfo uData, Context context) {
        Log.d("Entries", "Trying to save");

        // Add current day sessions to map
        days.put(currentDate, todayPumps);

        // Storage location for user data
        String filename = "user_data.bpb";

        // Writes PumpInfo object to a file
        try {
            FileOutputStream outputStream;
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objOut = new ObjectOutputStream(outputStream);
            objOut.writeObject(uData);
            outputStream.close();

            Log.d("Entries", "Saving");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Processes a date change including the swapping of daily session lists
    // ALWAYS USE THE saveDay METHOD BEFORE CALLING THIS OR THE CURRENT LIST COULD BE LOST
    protected void changeDate(String date) {
        Log.d("Entries", currentDate + " = " + date + " ? " + days.containsKey(date));
            if (days.containsKey(date)) {
                currentDate = date;
                todayPumps = new ArrayList<>();
                todayPumps.addAll(days.get(date));

                // DEBUG
                Log.d("Entries", "Date changed to " + date);
                Log.d("Entries", "Number of sessions in day: " + todayPumps.size());
            } else {
                todayPumps = new ArrayList<>();

                // DEBUG
                Log.d("Entries", "Emptying array");
            }
    }

    // Bugfix tool to remove duplicate timestamps on a day
    // Not as useful since the implementation of touch deletion
    protected void clearDuplicates() {
        int numEntries = todayPumps.size();

        for (int i = 0; i < numEntries; i++) {
            String currTimestamp = todayPumps.get(i).time;
            for (int j = 1; j < numEntries; j++) {
                if(currTimestamp.equals(todayPumps.get(j).time)){
                    todayPumps.remove(j);
                }
            }
        }
    }

    // Bugfix to quickly reset the model's date
    protected void fixDate(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sDF = new SimpleDateFormat("MM-dd-YYYY");
        currentDate = sDF.format(today);
    }

    // Loads model object from file
    protected static PumpInfo loadData(Context context) {
        PumpInfo loadedInfo;

        try {
            FileInputStream fileInputStream = context.openFileInput("user_data.bpb");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            loadedInfo = (PumpInfo) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

            Log.d("Entries", "User file loaded");
            Log.d("Entries", "Number of days saved:" + loadedInfo.days.size());

            return loadedInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Entries", "File not opened");

        return null;
    }

    @Override
    public String toString() {
        String output;

        output = "\nCurrent Date: " + currentDate + "\nNumber of Days: " + days.size()
                + "\nSessions Today: " + todayPumps.size();

        return output;
    }
}

