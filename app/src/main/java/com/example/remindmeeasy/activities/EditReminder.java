package com.example.remindmeeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindmeeasy.R;
import com.example.remindmeeasy.model.reminder;
import com.example.remindmeeasy.DAO.ReminderDao;
import com.example.remindmeeasy.DB.RoomDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditReminder extends AppCompatActivity {

    private EditText reminderNameEditText;
    private EditText reminderDateEditText;
    private EditText reminderTimeEditText;
    private EditText reminderDescriptionEditText;
    private Switch btnNotifSwitch;
    private Button saveButton;
    private Button cancelButton;

    private reminder originalReminder;
    private ReminderDao reminderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_reminder);

        // Initialize views
        reminderNameEditText = findViewById(R.id.reminderName);
        reminderDateEditText = findViewById(R.id.reminderDate);
        reminderTimeEditText = findViewById(R.id.reminderTime);
        reminderDescriptionEditText = findViewById(R.id.reminderDescription);
        btnNotifSwitch = findViewById(R.id.btnNotif);
        saveButton = findViewById(R.id.Save);
        cancelButton = findViewById(R.id.Cancel);

        // Get reminderDao instance from RoomDB (using Singleton)
        RoomDB database = RoomDB.getInstance(getApplicationContext());
        reminderDao = database.reminderDao();

        // Check if intent has extra data
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("reminder")) {
            originalReminder = (reminder) intent.getSerializableExtra("reminder");
            if (originalReminder != null) {
                // Populate EditText fields with reminder data
                reminderNameEditText.setText(originalReminder.getName());
                reminderDateEditText.setText(formatDate(originalReminder.getDateTime()));
                reminderTimeEditText.setText(formatTime(originalReminder.getDateTime()));
                reminderDescriptionEditText.setText(originalReminder.getDescription());
                btnNotifSwitch.setChecked(originalReminder.isRepeat());
            }
        }

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReminder();
            }
        });

        // Set click listener for cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Simply finish the activity to cancel the operation
            }
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(date);
    }

    private void updateReminder() {
        // Get updated data from EditText fields
        String name = reminderNameEditText.getText().toString();
        String description = reminderDescriptionEditText.getText().toString();
        boolean repeat = btnNotifSwitch.isChecked();

        // Update original reminder object
        originalReminder.setName(name);
        originalReminder.setDescription(description);
        originalReminder.setRepeat(repeat);

        // Update reminder in the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                reminderDao.updateReminder(originalReminder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditReminder.this, "Reminder updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after updating the reminder
                    }
                });
            }
        }).start();
    }
}
