package com.example.BPBuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    // Global data model used between activities
    static PumpInfo pumpMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Finds section containers
        TextView dashDate = findViewById(R.id.dash_date);
        LinearLayout topRow = findViewById(R.id.top_row_container);
        LinearLayout botRow = findViewById(R.id.bottom_row_container);


        // Gets current date for auto-population of time fields and for hashmap date keys
        Calendar today = Calendar.getInstance();
        Date today_date = today.getTime();


        // Key for PumpInfo HashMap
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String currDay = dateFormat.format(today_date);

        SimpleDateFormat timeFormat = new SimpleDateFormat("kk");
        int currHour = Integer.parseInt(timeFormat.format(today_date));


        // Sets the header text
        // TODO: Enumerable text Month, Day
        //dashDate.setText("Today, " + currDay);
        if(currHour >= 5 && currHour < 12){
            dashDate.setText("Good Morning!");
        }
        else if(currHour >= 12 && currHour < 17){
            dashDate.setText("Good Afternoon!");
        }
        else if(currHour >= 17 && currHour < 21){
            dashDate.setText("Good Evening!");
        }
        else{
            dashDate.setText("Hello, Night Owl!");
        }


        // Attempts to load savefile data
        if (pumpMap == null) {
            pumpMap = PumpInfo.loadData(this);
            if (pumpMap == null) { // If the load is unsuccessful or first run
                pumpMap = new PumpInfo(currDay); // Creates a new model object
            }
        } else {
            pumpMap.saveDay(pumpMap, MainActivity.this); // Saves and potentially leftover data
            pumpMap.changeDate(currDay); // Loads or initializes data for current day
        }

        // Initializes the PumpInfo data structure if one does not exist
        if (!pumpMap.days.isEmpty()) {
            if (!pumpMap.currentDate.equals(currDay)) {  // Checks for current date parity
                pumpMap.saveDay(pumpMap, MainActivity.this); // Saves stored day to map
                pumpMap.changeDate(currDay); // Changes date and daily array

                // DEBUG
                Log.d("Entries", "(Main) Changing date to: " + pumpMap.currentDate);
            }
        }

        // Bugfix
        if(pumpMap.currentDate == null){
            pumpMap.fixDate();
        }

        // Event handlers for dashboard content clicks
        topRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDailyList();
            }
        });
        botRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDailyList();
            }
        });
    }

    // Starts entry list activity, when dashboard content is clicked
    private void viewDailyList() {
        Intent i = new Intent(MainActivity.this, PumpMenu.class);
        startActivity(i);
    }

    // Calculates and displays dashboard stats:
    // Daily pumped total, daily stored total, time of last pump, total freezer storage
    @Override
    protected void onStart() {
        super.onStart();

        // Finds dashboard text fields
        TextView dashOzPumped = findViewById(R.id.dash_oz_pumped);
        TextView dashOZStored = findViewById(R.id.dash_oz_stored);
        TextView dashFreezerTot = findViewById(R.id.dash_freezer_total);
        TextView dashLastSession = findViewById(R.id.dash_last_time);

        // Daily totals not needed for freezer storage
        String freezerTot = String.valueOf(pumpMap.totalFreezerStash()); // All-time freezer stash level
        dashFreezerTot.setText(freezerTot);

        // Calculates dashboard category values
        if(!pumpMap.todayPumps.isEmpty()) {
            // Collects information on daily pumped, stored, and over freezer totals
            String dayPumpTot = String.valueOf(pumpMap.countDailyPumped()); // Daily pumped total
            String dayStorTot = String.valueOf(pumpMap.countDailyStored()); // Daily stored total

            // Time of last pump session, for use with a reminder notification
            String latestFeed = String.valueOf(pumpMap.todayPumps.get(pumpMap.todayPumps.size() - 1).time);

            // Displays dash info based on the info contained in the map and current daily list
            dashOzPumped.setText(dayPumpTot);
            dashOZStored.setText(dayStorTot);

            dashLastSession.setText(latestFeed);
        }
        else{
            dashLastSession.setText("Yesterday"); // TODO: add to manifest
        }
    }
}