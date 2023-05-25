package com.application.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button lostAndFoundBtn, newAdvertBtn, showMapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise the database
        DatabaseHelper.init(this);

        lostAndFoundBtn = findViewById(R.id.lostAndFoundBtn);
        newAdvertBtn = findViewById(R.id.newAdvertBtn);
        showMapBtn = findViewById(R.id.showMapButton);

        lostAndFoundBtn.setOnClickListener(v -> {
            // Go to lost and found activity
            openLostAndFoundActivity();
        });

        newAdvertBtn.setOnClickListener(v -> {
            // Go to new advert activity
            openNewAdvertActivity();
        });

        showMapBtn.setOnClickListener(v -> {
            // Go to map activity
            openMapActivity();
        });
    }

    public void openNewAdvertActivity() {
        Intent intent = new Intent(this, NewAdvertActivity.class);
        startActivity(intent);
    }

    public void openLostAndFoundActivity() {
        Intent intent = new Intent(this, LostAndFoundItemsActivity.class);
        startActivity(intent);
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}