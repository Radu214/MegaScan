package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.megascan.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmailSignUp;
    private EditText editTextPasswordSignUp;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmailSignUp = findViewById(R.id.edit_text_email_sign_up);
        editTextPasswordSignUp = findViewById(R.id.edit_text_password_sign_up);
        buttonRegister = findViewById(R.id.button_register);

        buttonRegister.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String email = editTextEmailSignUp.getText().toString().trim();
        String password = editTextPasswordSignUp.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_fields_required, Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real application, call your registration API here.
        // For demonstration, just show a success message:
        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();

        // Perhaps finish the activity or redirect to LoginActivity:
        // finish();
    }
}
