package com.example.BPBuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.BPBuddy.MainActivity.pumpMap;


public class PumpMenu extends AppCompatActivity implements RecycleListAdapter.OnEntryListener {

    // Global variables for RecyclerView
    ArrayList<String> timestamps = new ArrayList<>();
    ArrayList<String> pumpYields = new ArrayList<>();
    ArrayList<String> freezerAdds = new ArrayList<>();

    // Globals for DatePicker selection and RecyclerView handler
    static String datePickerSelectedDate;
    static int lastEntryClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_menu);

        // Initialization sort for entry timestamps
        pumpMap.sortDailySessions();

        // Dashboard and Add pump entry buttons
        Button dashBtn = findViewById(R.id.dash_button);
        Button addBtn = findViewById(R.id.add_button);

        // Deletion context items
        final LinearLayout delBox = findViewById(R.id.edit_entry_menu);
        Button delBtn = findViewById(R.id.delete_button);
        Button cancelBtn = findViewById(R.id.cancel_button);

        // Date header, DatePicker, and roaming date holder
        final TextView listDateText = findViewById(R.id.list_date_text);
        final DatePicker listDatePicker = findViewById(R.id.list_date_picker);
        String currDate;

       // Gets current date for header info
        Calendar.getInstance();
        Date today_date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String realDay = dateFormat.format(today_date);

        // Initializes the currently viewed day based on the selection of the DatePicker
        if (datePickerSelectedDate != null){
            currDate = datePickerSelectedDate;
            pumpMap.changeDate(currDate);
        } else {
            currDate = pumpMap.currentDate;
        }

        // Header date display
        // TODO: Enumerable Month/Day display
        if(currDate.equals(realDay)){
            listDateText.setText("Today, " + currDate);
        } else{
            listDateText.setText(currDate);
        }

        // Loads RecyclerView
        populateList();

        // Event handlers for Dashboard, Add entry, Delete, and Cancel buttons
        // Returns to the dashboard for the current day
        dashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PumpMenu.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Opens the data entry activity
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PumpMenu.this, AddSession.class);
                startActivity(i);
            }
        });

        // Removes the selected timestamp, updates the model, and refreshes the activity
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DEBUG
                Log.d("Entries", "Removing " +pumpMap.todayPumps.get(lastEntryClicked).time + " from " + pumpMap.currentDate);

                // Removes the entry at the clicked timestamp and saves user data
                pumpMap.todayPumps.remove(lastEntryClicked);
                pumpMap.saveDay(pumpMap, PumpMenu.this);

                // Dismisses the dialog
                delBox.setVisibility(View.GONE);

                // Prompts user of success
                Toast.makeText(PumpMenu.this, "Entry Deleted", Toast.LENGTH_SHORT).show();

                // Refreshes the activity
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });

        // Dismisses the deletion dialog
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delBox.setVisibility(View.GONE);
            }
        });

        // Event handlers for DatePicker display and selection
        listDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDatePicker.setVisibility(View.VISIBLE);
            }
        });

        listDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                // Formats date into (MM-dd-yyyy) to match HashMap date keys
                String dateChange = formatDatePicker(monthOfYear, dayOfMonth, year);

                // Saves user data, changes day and session list
                pumpMap.saveDay(pumpMap, PumpMenu.this);
                pumpMap.currentDate = dateChange;
                pumpMap.changeDate(dateChange);

                // Changes header to new date, (may not be necessary with activity refresh)
                listDateText.setText(pumpMap.currentDate);

                // Sets global date
                datePickerSelectedDate = dateChange;

                // Hides DatePicker
                listDatePicker.setVisibility(View.GONE);

                // Refreshes activity with new date and entries list
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });
    }


    // Formats the output of the DatePicker to match the HashMap date keys
    protected String formatDatePicker(int month, int day, int year)
    {
        DatePicker listDateText = findViewById(R.id.list_date_picker);
        String dateChange = ""; // Buffer for date

        // Adds month and corrects zero-index for viewing/comparing
        if(listDateText.getMonth() + 1 < 10){
            dateChange += "0" + (month +1) ;
        } else{
            dateChange += (month +1);
        }

        dateChange += "-";

        // Adds day to date with leading zero for single digits to match key pattern
        if(day < 10) {
            dateChange += "0" + day;
        } else{
            dateChange += day;
        }

        // Adds year
        dateChange += "-" + year;

        // Returns the formatted DatePicker output
        return dateChange;
    }

    // Populates adapter for RecyclerView
    private void populateList() {
        if (pumpMap.todayPumps != null) {
            for (Pump p : pumpMap.todayPumps) {
                timestamps.add(p.time);
                pumpYields.add(String.valueOf(p.ounces_pumped));
                freezerAdds.add(String.valueOf(p.ounces_stored));
            }
        }

        Log.d("Entries", "Timestamps in RV: " + (timestamps.size()));

        initRecyclerView();
    }

    // Adapter for RecyclerView
    private void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.pumped_list);
        RecycleListAdapter rvAdapter = new RecycleListAdapter( timestamps, pumpYields, freezerAdds, this, this);
        rv.setAdapter(rvAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    // Event handler for RecyclerView items
    // Opens deletion dialog and prompts user for deletion confirmation
    @Override
    public void OnEntryClick(int position) {
        // Finds deletion dialog container and text field
        LinearLayout del_box = findViewById(R.id.edit_entry_menu);
        TextView delText = findViewById(R.id.deletion_text);

        // Creates prompt with entry timestamp
        String delMessage = "Do you want to delete the entry from " + timestamps.get(position) + "?";

        // Updates the global variable to be used with the button event handlers
        lastEntryClicked = position;

        // Displays the deletion dialog
        del_box.setVisibility(View.VISIBLE);
        delText.setText(delMessage);

        // DEBUG
        Log.d("Entries", "Clicked on: " + timestamps.get(position));
    }
}
