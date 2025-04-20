package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.megascan.R;
import com.example.megascan.model.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageProfilePicture;
    private TextView textEmail;
    private TextView textFullName;
    private RecyclerView recyclerViewOrders;
    private OrdersAdapter ordersAdapter;

    private OkHttpClient client;
    private static final String BASE_URL = "http://10.200.20.238:3000"; //Hardcoded

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageProfilePicture = findViewById(R.id.image_profile_picture);
        textEmail = findViewById(R.id.text_email);
        textFullName = findViewById(R.id.text_full_name);
        recyclerViewOrders = findViewById(R.id.recycler_view_orders);

        client = new OkHttpClient();

        // Retrieve the email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("USER_EMAIL", null);

        if (userEmail != null) {
            fetchUserProfile(userEmail);
        } else {
            Toast.makeText(this, "User email not found! Please log in again.", Toast.LENGTH_SHORT).show();

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
                        imageProfilePicture.setImageResource(R.drawable.baseline_account_circle_24);

                        // After fetching user info, fetch order history
                        fetchOrderHistory(email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchOrderHistory(String email) {
        String url = BASE_URL + "/orders/" + email;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(ProfileActivity.this, "Failed to load orders: " + e.getMessage(), Toast.LENGTH_SHORT).show()
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
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<Order> orders = objectMapper.readValue(responseData, new TypeReference<List<Order>>() {});


                        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                        ordersAdapter = new OrdersAdapter(orders);
                        recyclerViewOrders.setAdapter(ordersAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing order data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
