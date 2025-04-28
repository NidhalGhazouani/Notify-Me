package com.example.remindmeeasy.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.remindmeeasy.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void  insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUserByUsernameAndPassword(String username, String password);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    // Define other methods for interacting with the User table
    // For example, you can add methods for retrieving all users, updating users, etc.
}

