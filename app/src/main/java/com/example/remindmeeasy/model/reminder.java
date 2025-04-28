package com.example.remindmeeasy.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "reminders")
public class reminder implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "dateTime")
    private Date dateTime;

    @ColumnInfo(name = "repeat")
    private boolean repeat;

    @ColumnInfo(name = "user_id")
    private int userId;
    // Constructor


    public reminder(int id, String name, String description, Date dateTime, boolean repeat, int userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.repeat = repeat;
        this.userId = userId;
    }

    // Getter and setter methods
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public boolean isRepeat() {
        return repeat;
    }

//    public reminder() {
//        return;
//    }


}

