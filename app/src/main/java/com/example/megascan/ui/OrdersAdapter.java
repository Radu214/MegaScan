package com.example.megascan.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.megascan.R;
import com.example.megascan.model.Order;
import com.example.megascan.model.OrderItem;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrdersAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.textOrderId.setText("Order ID: " + order.getOrderId());
        holder.textCreatedAt.setText("Date: " + order.getCreatedAt());

        // Calculate total for the order
        double total = 0;
        StringBuilder itemsBuilder = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            total += item.getQuantity() * item.getPrice();
            itemsBuilder.append(item.getProductCode())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append(" = ")
                    .append(item.getPrice() * item.getQuantity())
                    .append("lei\n");
        }

        holder.textItems.setText(itemsBuilder.toString().trim());
        holder.textTotal.setText(String.format("Total: $%.2f", total));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId;
        TextView textCreatedAt;
        TextView textItems;
        TextView textTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textCreatedAt = itemView.findViewById(R.id.text_created_at);
            textItems = itemView.findViewById(R.id.text_items);
            textTotal = itemView.findViewById(R.id.text_total);
        }
    }
}
