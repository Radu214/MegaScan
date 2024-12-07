package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.megascan.R;
import com.example.megascan.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonLogin = findViewById(R.id.button_login);

        // Set click listener for the login button
        buttonLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_fields_required, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object for demonstration (in real scenario: verify with server or local DB)
        User user = new User(email, password);

        // Dummy check for credentials
        if (isValidCredentials(user)) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            // Proceed to next activity or main screen
            // startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, R.string.error_invalid_credentials, Toast.LENGTH_SHORT).show();
        }
    }

    // For demonstration, simply checks if email contains "@" and password length > 3
    private boolean isValidCredentials(User user) {
        return user.getEmail().contains("@") && user.getPassword().length() > 3;
    }
}
