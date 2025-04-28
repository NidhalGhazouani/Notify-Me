package com.example.remindmeeasy.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.remindmeeasy.DB.RoomDB;

import com.example.remindmeeasy.R;
import com.example.remindmeeasy.model.User;
import com.google.android.material.button.MaterialButton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LogIn extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private RoomDB appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        MaterialButton loginButton = findViewById(R.id.login);
        TextView signUpTextView = findViewById(R.id.signup);

        appDatabase = RoomDB.getInstance(this);

        loginButton.setOnClickListener(view -> login());

        signUpTextView.setOnClickListener(view -> startActivity(new Intent(LogIn.this, SignUp.class)));
    }

    @SuppressLint("StaticFieldLeak")
    private void login() {
        String emailInput = emailEditText.getText().toString();
        String passwordInput = passwordEditText.getText().toString();

        // Use AsyncTask to perform database operations asynchronously
        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {
                // Execute database query in a background thread
                return appDatabase.userDao().getUserByEmail(emailInput);
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                if (user != null) {
                    // Hash the input password for comparison
                    String hashedInputPassword = hashPassword(passwordInput);
                    if (hashedInputPassword != null && user.getPassword().equals(hashedInputPassword)) {
                        // Successful login
                        Toast.makeText(LogIn.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Store user ID in SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("user_id", user.getId());
                        editor.apply();

                        // Start the main activity or any other activity after login
                        startActivity(new Intent(LogIn.this, dashBoard.class));
                        finish(); // Close the login activity
                    } else {
                        // Incorrect password
                        Toast.makeText(LogIn.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User not found
                    Toast.makeText(LogIn.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
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
