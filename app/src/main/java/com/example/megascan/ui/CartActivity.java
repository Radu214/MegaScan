package com.example.megascan.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.megascan.R;
import com.example.megascan.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView textTotalAmount;
    private Button buttonCheckout;

    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_view_cart);
        textTotalAmount = findViewById(R.id.text_total_amount);
        buttonCheckout = findViewById(R.id.button_checkout);

        // Example: Pre-populate cart items
        cartItems = new ArrayList<>();
        cartItems.add(new CartItem("Product A", 9.99, 1));
        cartItems.add(new CartItem("Product B", 4.50, 2));
        cartItems.add(new CartItem("Product C", 12.00, 1));

        adapter = new CartAdapter(cartItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateTotal();

        buttonCheckout.setOnClickListener(v -> {
            // Go to checkout activity
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
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

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        textTotalAmount.setText(String.format("$%.2f", total));
    }
}
