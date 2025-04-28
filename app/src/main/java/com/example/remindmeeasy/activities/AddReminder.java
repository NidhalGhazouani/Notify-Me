package com.example.remindmeeasy.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindmeeasy.DB.RoomDB;
import com.example.remindmeeasy.R;
import com.example.remindmeeasy.model.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddReminder extends AppCompatActivity {

    private EditText reminderName, reminderDate, reminderTime, reminderDescription;
    private Switch btnNotif;
    private Button btnAdd, btnCancel;

    private RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);

        database = RoomDB.getInstance(this);

        reminderName = findViewById(R.id.reminderName);
        reminderDate = findViewById(R.id.reminderDate);
        reminderTime = findViewById(R.id.reminderTime);
        reminderDescription = findViewById(R.id.reminderDescription);
        btnNotif = findViewById(R.id.btnNotif);
        btnAdd = findViewById(R.id.Add);
        btnCancel = findViewById(R.id.Cancel);

        reminderDate.setOnClickListener(v -> openDatePicker());
        reminderTime.setOnClickListener(v -> openTimePicker());
        btnAdd.setOnClickListener(v -> addReminder());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void openDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.DialogTheme,
                (view, year, month, dayOfMonth) -> {
                    // This callback is triggered when the user presses the "OK" button.
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    reminderDate.setText(sdf.format(calendar.getTime())); // Update EditText with selected date
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // Set dialog properties
        datePickerDialog.setCancelable(false);  // Prevent dismissing by clicking outside the dialog
        datePickerDialog.setCanceledOnTouchOutside(false);  // Prevent dismissing by clicking outside

        // Show the date picker dialog
        datePickerDialog.show();
    }



    private void openTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                R.style.DialogTheme,
                (view, hourOfDay, minute) -> {
                    // This callback is triggered when the user presses the "Set" button.
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    reminderTime.setText(sdf.format(cal.getTime())); // Update EditText with selected time
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false  // Use 24-hour format: false for 12-hour, true for 24-hour
        );

        // Set dialog properties
        timePickerDialog.setCancelable(false);  // Prevent dismissing by clicking outside the dialog
        timePickerDialog.setCanceledOnTouchOutside(false);  // Prevent dismissing by clicking outside

        // Show the time picker dialog
        timePickerDialog.show();
    }


    private int getCurrentUserId() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return preferences.getInt("user_id", -1);
    }

    private void addReminder() {
        String name = reminderName.getText().toString().trim();
        String description = reminderDescription.getText().toString().trim();
        String dateStr = reminderDate.getText().toString().trim();
        String timeStr = reminderTime.getText().toString().trim();

        // Ensure all fields are filled
        if (name.isEmpty() || description.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combine the selected date and time into one string
        String dateTime = dateStr + " " + timeStr;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

        Date parsedDateTime = null;
        try {
            // Parse the combined date and time string
            parsedDateTime = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date/time format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (parsedDateTime == null) {
            Toast.makeText(this, "Failed to parse date/time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the repeat status from the switch
        boolean repeat = btnNotif.isChecked();

        // Get the current user's ID (you might have a method for this)
        int userId = getCurrentUserId();

        // Create a new reminder object
        reminder newReminder = new reminder(0, name, description, parsedDateTime, repeat, userId);

        // Insert the new reminder into the database
        Executors.newSingleThreadExecutor().execute(() -> {
            database.reminderDao().insertReminder(newReminder);
            runOnUiThread(() -> {
                // Notify user and finish the activity
                Toast.makeText(AddReminder.this, "Reminder added successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

}
