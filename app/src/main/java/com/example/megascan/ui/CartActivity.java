package com.example.megascan.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
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
