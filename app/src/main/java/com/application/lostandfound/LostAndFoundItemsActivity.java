package com.application.lostandfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class LostAndFoundItemsActivity extends AppCompatActivity {

    RecyclerView itemRV;
    ItemAdapter itemAdapter;
    RecyclerView.LayoutManager itemLayoutManager;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found_items);

        List<Item> items = DatabaseHelper.getInstance().getData();

        // Set recycler views
        itemRV = findViewById(R.id.itemRV);
        itemLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemAdapter = new ItemAdapter(this, items);
        itemRV.setLayoutManager(itemLayoutManager);
        itemRV.setAdapter(itemAdapter);
    }

}