package com.application.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DisplayItemActivity extends AppCompatActivity {

    private TextView lostOrFoundTV, itemNameTV, itemPhoneTV, itemDescriptionTV, itemLocationTV, itemDateTV;
    private Button removeItemBtn;
    private Item item;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String itemDetails = extras.getString("item");
                item = objectMapper.readValue(itemDetails, Item.class);
                System.out.println(objectMapper.writeValueAsString(item));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        lostOrFoundTV = findViewById(R.id.lostOrFoundTV);
        itemNameTV = findViewById(R.id.itemNameTV);
        itemPhoneTV = findViewById(R.id.itemPhoneTV);
        itemDescriptionTV = findViewById(R.id.itemDescriptionTV);
        itemLocationTV = findViewById(R.id.itemLocationTV);
        itemDateTV = findViewById(R.id.itemDateTV);
        removeItemBtn = findViewById(R.id.removeItemBtn);

        lostOrFoundTV.setText(item.getLostOrFound());
        itemNameTV.setText(item.getName());
        itemPhoneTV.setText(String.valueOf(item.getNumber()));
        itemDescriptionTV.setText(item.getDescription());
        itemLocationTV.setText(item.getLocation());
        itemDateTV.setText(item.getDate());

        removeItemBtn.setOnClickListener(view -> {
            if (DatabaseHelper.getInstance().deleteItem(item.getId())) {
                openLostAndFoundItemsActivity();
            } else {
                Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void openLostAndFoundItemsActivity() {
        Intent intent = new Intent(this, LostAndFoundItemsActivity.class);
        startActivity(intent);
    }

}