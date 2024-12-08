package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.megascan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageProfilePicture;
    private TextView textEmail;
    private TextView textFullName;

    private OkHttpClient client;
    private static final String BASE_URL = "http://10.200.20.238:3000"; // Replace with your server IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageProfilePicture = findViewById(R.id.image_profile_picture);
        textEmail = findViewById(R.id.text_email);
        textFullName = findViewById(R.id.text_full_name);

        client = new OkHttpClient();

        // Retrieve the email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("USER_EMAIL", null);

        if (userEmail != null) {
            fetchUserProfile(userEmail);
        } else {
            Toast.makeText(this, "User email not found! Please log in again.", Toast.LENGTH_SHORT).show();
            // Optionally redirect the user to the login screen
        }
    }


    private void fetchUserProfile(String email) {
        String url = BASE_URL + "/user/" + email;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(ProfileActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(ProfileActivity.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                final String responseData = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject userJson = new JSONObject(responseData);
                        String fullName = userJson.getString("firstName") + " " + userJson.getString("lastName");
                        String email = userJson.getString("email");

                        textFullName.setText(fullName);
                        textEmail.setText(email);
                        // Optionally set a placeholder image
                        imageProfilePicture.setImageResource(R.drawable.baseline_account_circle_24);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
