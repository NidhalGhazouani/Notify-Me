package com.example.remindmeeasy.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.remindmeeasy.DAO.UserDao;
import com.example.remindmeeasy.DB.RoomDB;
import com.example.remindmeeasy.R;
import com.example.remindmeeasy.model.User;
import com.google.android.material.button.MaterialButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUp extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText password;

    private RoomDB appDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        MaterialButton submitButton = findViewById(R.id.submit);
        TextView loginTextView = findViewById(R.id.login);

        appDatabase = RoomDB.getInstance(this);

        RoomDB roomDB = Room.databaseBuilder(getApplicationContext(),
                        RoomDB.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();

        UserDao userDao = roomDB.userDao();

        submitButton.setOnClickListener(view -> {
            String userName = username.getText().toString();
            String Email = email.getText().toString();
            String Password = password.getText().toString();

            // Check if a user with the entered email already exists in the database asynchronously
            new AsyncTask<Void, Void, User>() {
                @Override
                protected User doInBackground(Void... voids) {
                    return appDatabase.userDao().getUserByEmail(Email);
                }

                @Override
                protected void onPostExecute(User existingUser) {
                    if (existingUser != null) {
                        // User with the same email already exists, display error message
                        Toast.makeText(SignUp.this, "An account with this email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
                    } else {
                        // No user with the same email exists, proceed with sign-up process asynchronously
                        new AsyncTask<Void, Void, Void>() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            protected Void doInBackground(Void... voids) {
                                // Hash the password and insert the new user into the database
                                String hashedPassword = hashPassword(Password);
                                User newUser = new User(userName, Email, hashedPassword);
                                appDatabase.userDao().insert(newUser);
                                return null;
                            }

                            @SuppressLint("StaticFieldLeak")
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                // Show success message and redirect to login page
                                Toast.makeText(SignUp.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this, LogIn.class));
                                finish(); // Close the sign-up activity
                            }
                        }.execute();
                    }
                }
            }.execute();
        });


        loginTextView.setOnClickListener(view -> startActivity(new Intent(SignUp.this, LogIn.class)));
    }

    private class SignUpAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        public SignUpAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Show success message on the main thread
            Toast.makeText(SignUp.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignUp.this, LogIn.class));
            finish();
        }
    }








    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            // Convert byte array to hexadecimal string
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
