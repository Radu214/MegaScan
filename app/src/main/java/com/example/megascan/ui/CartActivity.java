package com.example.megascan.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.megascan.R;
import com.example.megascan.model.Cart;
import com.example.megascan.model.CartItem;
import com.example.megascan.model.Produs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import android.widget.Toast;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull; // If using JetBrains annotations
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {


    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView textTotalAmount;
    private Button buttonCheckout;
    private Cart cart = Cart.getInstance();

    private TextView textEmptyCartMessage;
    private View checkoutContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        textEmptyCartMessage = findViewById(R.id.text_empty_cart_message);
        recyclerView = findViewById(R.id.recycler_view_cart);
        textTotalAmount = findViewById(R.id.text_total_amount);
        buttonCheckout = findViewById(R.id.button_checkout);
        checkoutContainer = findViewById(R.id.checkout_container);

        // Example: Populate cart items
        Cart cart = Cart.getInstance();
//        cartItems.add(new CartItem("Product A", 9.99, 1));
//        cartItems.add(new CartItem("Product B", 4.50, 2));
//        cartItems.add(new CartItem("Product C", 12.00, 1));

        adapter = new CartAdapter(cart.getItemList(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateTotal();

        buttonCheckout.setOnClickListener(v -> {
            if(cart.getPlus18()>0 && !cart.isEmployeeChecked())
            {
                runOnUiThread(() -> {
                    new AlertDialog.Builder(CartActivity.this)
                            .setTitle("Additional confiramtion needed")
                            .setMessage("You have added an age restricted item.\nPlease find an employee to confirm you are 18 or older.")
                            .setPositiveButton("Go to Scan", (dialog, which) -> {
                                dialog.dismiss();
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                }, 2000);
                                Intent intent = new Intent(CartActivity.this, ScanBarcodeActivity.class);
                                startActivity(intent);
                            })
                            .setCancelable(false)
                            .show();

                });

            }
            else {
                sendCheckoutRequest();
            }
        });
    }

    @Override
    public void onQuantityChanged() {
        updateTotal();
    }

    @Override
    public void onItemRemoved(CartItem item) {

        updateTotal();
    }

    private void sendCheckoutRequest() {
        OkHttpClient client = new OkHttpClient();

        Cart cart = Cart.getInstance();
        List<CartItem> cartItems = cart.getItemList();

        // Retrieve the email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("USER_EMAIL", null);

        // Build JSON payload
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userEmail", userEmail);

            JSONArray itemsArray = new JSONArray();
            for (CartItem item : cartItems) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("cod", item.getCod());   // Make sure CartItem has getCod() method
                itemObj.put("quantity", item.getQuantity());
                itemObj.put("pret", item.getPret());
                itemsArray.put(itemObj);
            }
            jsonObject.put("items", itemsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://10.200.20.238:3000/checkout") // Adjust your server URL
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    new AlertDialog.Builder(CartActivity.this)
                            .setTitle("Checkout Failed")
                            .setMessage("Could not complete checkout: " + e.getMessage())
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    final String errorMsg = response.body().string();
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(CartActivity.this)
                                .setTitle("Checkout Error")
                                .setMessage("Server error: " + errorMsg)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    });
                    return;
                }

                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    // Successfully saved cart data to DB
                    // Clear cart if desired
                    Cart.getInstance().clearCart();

                    // Navigate to CheckoutActivity
                    Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    private void updateTotal() {
        double total = 0;
        cart.setPlus18(0);
        for (CartItem item : cart.getItemList()) {
            total += item.getPret() * item.getQuantity();
            if(item.getPLUS18()>0)
                cart.setPlus18(cart.getPlus18()+item.getQuantity());
        }


        if (cart.getItemList().isEmpty()) {
            // Show empty state
            textEmptyCartMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            checkoutContainer.setVisibility(View.GONE);
        } else {
            // Show cart
            textEmptyCartMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            checkoutContainer.setVisibility(View.VISIBLE);
            textTotalAmount.setText(String.format("$%.2f", total));
        }
    }
}
