package com.example.megascan.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.megascan.R;
import com.example.megascan.model.PromoItem;

import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.PromoViewHolder> {

    private List<PromoItem> promoItems;
    private Context context;

    public PromoAdapter(List<PromoItem> promoItems, Context context) {
        this.promoItems = promoItems;
        this.context = context;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        PromoItem item = promoItems.get(position);
        holder.name.setText(item.getDenumire());
        holder.originalPrice.setText(String.format("%.2f lei", item.getPret()));
        holder.discountedPrice.setText(String.format("%.2f lei", item.getDiscountedPrice()));
        holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return promoItems.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView name, originalPrice, discountedPrice;
        ImageView image;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_item_name);
            originalPrice = itemView.findViewById(R.id.text_original_price);
            discountedPrice = itemView.findViewById(R.id.text_discounted_price);
            image = itemView.findViewById(R.id.image_item);
        }
    }
}
