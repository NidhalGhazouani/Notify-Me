package com.example.remindmeeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmeeasy.R;
import com.example.remindmeeasy.adapter.ReminderAdapter;
import com.example.remindmeeasy.model.reminder;
import com.example.remindmeeasy.DAO.ReminderDao;
import com.example.remindmeeasy.DB.RoomDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.room.Delete;

import java.util.List;

public class dashBoard extends AppCompatActivity {

    private ReminderDao reminderDao;
    private RecyclerView reminderRecyclerView;
    private ReminderAdapter adapter;
    private int userId; // Variable to store user ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Retrieve user ID from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1); // Default value -1 if not found

        // Initialize views
        reminderRecyclerView = findViewById(R.id.recycler);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReminderAdapter(this);
        reminderRecyclerView.setAdapter(adapter);

        // Get reminderDao instance from RoomDB (using Singleton)
        RoomDB database = RoomDB.getInstance(getApplicationContext());
        reminderDao = database.reminderDao();

        // Set up FloatingActionButton click listener for adding reminders
        FloatingActionButton addButton = findViewById(R.id.add_plan_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open activity to create a new reminder
                Intent intent = new Intent(dashBoard.this, AddReminder.class);
                startActivity(intent);
            }
        });
        // Set up the delete button click listener for the adapter
        adapter.setOnDeleteClickListener(new ReminderAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                // Handle delete button click here
                deleteReminder(adapter.getReminderAt(position));
            }
        });

        adapter.setOnUpdateClickListener(new ReminderAdapter.OnUpdateClickListener() {
            @Override
            public void onUpdateClick(int position) {
                // Handle delete button click here
                updateReminder(adapter.getReminderAt(position));
            }
        });


        // Set up logout button click listener
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }



    public void updateReminder(reminder clickedReminder) {
        // Open activity to edit the selected reminder
        Intent intent = new Intent(dashBoard.this, EditReminder.class);
        intent.putExtra("reminder", clickedReminder);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the reminders when the activity is resumed
        loadRemindersFromDatabase();
    }

    private void loadRemindersFromDatabase() {
        // Fetch reminders specific to the user ID in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<reminder> reminders = reminderDao.getAllRemindersByUserId(userId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setReminders(reminders); // Update adapter with reminders
                    }
                });
            }
        }).start();
    }

    // Method to delete a reminder
    private void deleteReminder(reminder reminder) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Delete reminder from the database
                reminderDao.deleteReminder(reminder);
                // Reload reminders from the database
                loadRemindersFromDatabase();
            }
        }).start();
    }

    private void logout() {

        // Clear SharedPreferences and navigate to login activity
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(dashBoard.this, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

