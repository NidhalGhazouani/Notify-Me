package com.example.remindmeeasy.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.remindmeeasy.DAO.ReminderDao;
import com.example.remindmeeasy.DAO.UserDao;
import com.example.remindmeeasy.model.User;
import com.example.remindmeeasy.model.reminder;

@Database(entities = {User.class, reminder.class}, version = 12, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class RoomDB extends RoomDatabase {

    // Define a method that returns an instance of the UserDao interface
    public abstract UserDao userDao();

    // Define a method that returns an instance of the reminderDao interface
    public abstract ReminderDao reminderDao();


    // Implement the Singleton pattern
    private static RoomDB instance;

    public static synchronized RoomDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDB.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

