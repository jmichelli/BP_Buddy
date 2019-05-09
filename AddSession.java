package com.example.BPBuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.BPBuddy.MainActivity.pumpMap;

public class AddSession extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        //DEBUG
        Log.d("Entries", "Adding entry to " + pumpMap.currentDate);

        // Gets current date for auto-population of time fields
        Date today_date = Calendar.getInstance().getTime();
        SimpleDateFormat getHour = new SimpleDateFormat("kk");
        SimpleDateFormat getMin = new SimpleDateFormat("mm");
        int hour = Integer.parseInt(getHour.format(today_date));
        int mins = Integer.parseInt(getMin.format(today_date));

        // Storing views and controls from layout
        final EditText pumpedHour = findViewById(R.id.pumped_hour);
        final EditText pumpedMin = findViewById(R.id.pumped_minute);
        final EditText ozPumpedField = findViewById(R.id.oz_pumped_field);
        final Spinner pumpedAMPM = findViewById(R.id.pumped_am_pm_spinner);
        CheckBox storageToggle = findViewById(R.id.stored_toggle);
        final TextView storedLabel = findViewById(R.id.stored_label);
        final LinearLayout storedInfo = findViewById(R.id.stored_entries);
        final EditText ozStoredField = findViewById(R.id.oz_stored_field);
        Button pumpedSubmit = findViewById(R.id.pumped_submit);

        // Sets up AM/PM dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.am_pm, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pumpedAMPM.setAdapter(adapter);

        // Formats time fields to 12 hour clock and sets am/pm toggle
        if (hour >= 12) {
            pumpedAMPM.setSelection(1);

            if (hour == 24)
                pumpedAMPM.setSelection(0);
            if (hour > 12)
                hour -= 12;
        }

        // Populates the time fields with current time
        pumpedHour.setText(String.valueOf(hour), TextView.BufferType.EDITABLE);

        // Adds a leading zero to single digit integers for time format
        if (mins >= 10) {
            pumpedMin.setText(String.valueOf(mins), TextView.BufferType.EDITABLE);
        } else {
            String minsFormat = "0" + mins;
            pumpedMin.setText(minsFormat, TextView.BufferType.EDITABLE);
        }

        // TODO: add to manifest
        ozPumpedField.setText(R.string.pumped_field_val);

        // Listener for frozen storage tracking
        // Shows entry labels and fields if checked
        // Clears ounces stored field and hides storage text and fields if unchecked
        storageToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    storedLabel.setVisibility(View.VISIBLE);
                    storedInfo.setVisibility(View.VISIBLE);
                } else {
                    storedLabel.setVisibility(View.GONE);
                    storedInfo.setVisibility(View.GONE);
                    ozStoredField.setText("0");
                }
            }
        });

        // Adds the current pumping session info to the list containing the current day
        pumpedSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Converts user fields into integer ounce values, and a string time value
                int ozPumped = Integer.parseInt(String.valueOf(ozPumpedField.getText()));
                int ozStored = Integer.parseInt(String.valueOf(ozStoredField.getText()));
                String userHour = String.valueOf(pumpedHour.getText());
                String userMins = String.valueOf(pumpedMin.getText());
                String time = userHour + ":" + userMins
                        + " " + (pumpedAMPM.getSelectedItem());

                // Data entry validity/reasonableness checks
                if (Integer.parseInt(userHour) > 12 || Integer.parseInt(userHour) < 1) {
                    Toast.makeText(AddSession.this, "Invalid Hour Entry", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(userMins) > 59 || Integer.parseInt(userMins) < 0) {
                    Toast.makeText(AddSession.this, "Invalid Minutes Entry", Toast.LENGTH_SHORT).show();
                } else if (ozPumped < 0 || ozPumped > 50) {
                    Toast.makeText(AddSession.this, "Invalid Ounces Pumped Entry", Toast.LENGTH_SHORT).show();
                } else if (ozStored > ozPumped || ozStored < 0) {
                    Toast.makeText(AddSession.this, "Cannot Store More Ounces than Pumped", Toast.LENGTH_SHORT).show();
                } else {
                    // Adds values to a Pump object for storage in a list
                    Pump currPump = new Pump(ozPumped, ozStored, time);

                    // Clears a duplicate timestamped pump (for fixing entries)
                    for (Pump p : pumpMap.todayPumps) {
                        Log.d("Values", p.time);
                        if (p.time.equals(time)) {
                            pumpMap.todayPumps.remove(p);
                            pumpMap.saveDay(pumpMap, AddSession.this);
                        }
                    }

                    // Ordered insertion no longer necessary, sort function works, implemented pre-display
                    // Adds the pump session entry to the daily list
                    // Updates the model and saves to file
                    pumpMap.todayPumps.add(currPump);
                    pumpMap.saveDay(pumpMap, AddSession.this);

                    Toast.makeText(AddSession.this, "Data Stored", Toast.LENGTH_SHORT).show();

                    // Transition to pumped list view
                    Intent i = new Intent(AddSession.this, PumpMenu.class);
                    startActivity(i);

                    // DEBUG:
                    Log.d("Entries", currPump.time);
                    Log.d("Entries", String.valueOf(currPump.ounces_pumped));
                    Log.d("Entries", String.valueOf(currPump.ounces_stored));
                    Log.d("Entries", "Entry index: " + pumpMap.todayPumps.indexOf(currPump));
                } // else - successful process
            } // onClick
        });
    } // onCreate

} // class

