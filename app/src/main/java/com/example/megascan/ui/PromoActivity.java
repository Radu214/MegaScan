package com.example.megascan.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.megascan.R;
import com.example.megascan.model.PromoItem;

import java.util.ArrayList;
import java.util.List;

public class PromoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PromoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        recyclerView = findViewById(R.id.recycler_view_promo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example data
        List<PromoItem> promoItems = new ArrayList<>();
        promoItems.add(new PromoItem("codabc", 12.5, "Product A", "Brand A", "https://www.shutterstock.com/image-photo/closeup-person-eating-spaghetti-fork-260nw-2481135071.jpg", 9.99));
        promoItems.add(new PromoItem("codabc", 12.5, "Product B", "Brand B", "https://www.shutterstock.com/image-photo/closeup-person-eating-spaghetti-fork-260nw-2481135071.jpg", 9.99));
        promoItems.add(new PromoItem("codabc", 12.5, "Product C", "Brand C", "https://www.shutterstock.com/image-photo/closeup-person-eating-spaghetti-fork-260nw-2481135071.jpg", 9.99));

        setPromotionalItems(promoItems);
    }

    public void setPromotionalItems(List<PromoItem> items) {
        adapter = new PromoAdapter(items, this);
        recyclerView.setAdapter(adapter);
    }
}
