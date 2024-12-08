package com.example.megascan.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.megascan.R;
import com.example.megascan.model.PromoItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PromoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PromoAdapter adapter;
    private OkHttpClient client;
    private static final String BASE_URL = "http://10.200.20.238:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        recyclerView = findViewById(R.id.recycler_view_promo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        client = new OkHttpClient();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("USER_EMAIL", null);

        if (userEmail != null) {
            fetchRecommendations(userEmail);
        } else {
            Toast.makeText(this, "No user logged in. Please log in first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRecommendations(String email) {
        String url = BASE_URL + "/recommendations/" + email;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(PromoActivity.this, "Failed to load recommendations: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(PromoActivity.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    JSONArray recommendationsArray = json.getJSONArray("recommendations");
                    List<PromoItem> promoItems = new ArrayList<>();

                    for (int i = 0; i < recommendationsArray.length(); i++) {
                        JSONObject rec = recommendationsArray.getJSONObject(i);
                        String productId = rec.getString("productId");
                        double rating = rec.getDouble("rating");
                        String name = rec.getString("name");
                        double price = rec.getDouble("price");
                        double discount = rec.getDouble("discount");

                        // Create a PromoItem using the data from the server
                        PromoItem item = new PromoItem(
                                productId,
                                price,// cod
                                name,      // denumire from server
                                "Recommended", // firma placeholder
                                0,         // plus18 placeholder if needed
                                "",        // imageUrl placeholder

                                price-price*discount/100  // new discount field
                        );
                        promoItems.add(item);
                    }

                    runOnUiThread(() -> setPromotionalItems(promoItems));
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(PromoActivity.this, "Error parsing recommendations.", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    public void setPromotionalItems(List<PromoItem> items) {
        adapter = new PromoAdapter(items, this);
        recyclerView.setAdapter(adapter);
    }
}
