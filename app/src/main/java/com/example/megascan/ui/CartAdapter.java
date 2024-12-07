package com.example.megascan.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.megascan.R;
import com.example.megascan.model.Cart;
import com.example.megascan.model.CartItem;
import com.example.megascan.model.Produs;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>  {

    public interface OnCartItemChangeListener {
        void onQuantityChanged();
        void onItemRemoved(CartItem item);
    }

    private Cart cart = Cart.getInstance();
    private OnCartItemChangeListener listener;

    public CartAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cart.setItemList(cartItems);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cart.getItemList().get(position);
        holder.textProductName.setText(item.getDenumire());
        holder.textProductPrice.setText(String.format("$%.2f", item.getPret()));
        holder.textQuantity.setText(String.valueOf(item.getQuantity()));

        holder.buttonIncrement.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (listener != null) listener.onQuantityChanged();
        });

        holder.buttonDecrement.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                if (listener != null) listener.onQuantityChanged();
            }
        });

        holder.buttonRemove.setOnClickListener(v -> {
            cart.getItemList().remove(position);

            if (cart.getItemList().isEmpty()) {
                // If the cart becomes empty, notifyDataSetChanged() to prevent crashes
                notifyDataSetChanged();
            } else {
                // Notify the adapter about the item removed
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cart.getItemList().size());
            }

            if (listener != null) listener.onItemRemoved(item);
        });

    }

    @Override
    public int getItemCount() {
        return cart.getItemList().size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textProductName, textProductPrice, textQuantity;
        Button buttonIncrement, buttonDecrement, buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            buttonIncrement = itemView.findViewById(R.id.button_increment);
            buttonDecrement = itemView.findViewById(R.id.button_decrement);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }
    }
}
