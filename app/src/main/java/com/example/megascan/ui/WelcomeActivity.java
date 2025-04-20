package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.megascan.R;

public class WelcomeActivity extends AppCompatActivity {

    private Button buttonSignUp;
    private Button buttonGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize buttons
        buttonSignUp = findViewById(R.id.button_sign_up);
        buttonGoToLogin = findViewById(R.id.button_go_to_login);

        // Set click listeners
        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        buttonGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
