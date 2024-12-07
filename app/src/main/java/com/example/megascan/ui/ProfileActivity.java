package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.megascan.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageProfilePicture;
    private TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageProfilePicture = findViewById(R.id.image_profile_picture);
        textEmail = findViewById(R.id.text_email);

        // If you want to dynamically set the email or picture:
        // textEmail.setText("user@example.com");
        // imageProfilePicture.setImageResource(R.drawable.ic_default_profile);
    }
}
