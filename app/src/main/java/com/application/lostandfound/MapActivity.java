package com.application.lostandfound;

import static com.application.lostandfound.ItemAdapter.context;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private GoogleMap mMap;
    BitmapDescriptor lostIcon, foundIcon;
    private List<Item> lostAndFoundItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Retrieve lost and found items from your database
        lostAndFoundItems = DatabaseHelper.getInstance().getData();

        lostIcon = getResizedBitmapDescriptor(R.drawable.lost_icon, 120, 120);
        foundIcon = getResizedBitmapDescriptor(R.drawable.found_icon, 120, 120);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers for each lost and found item on the map
        for (Item item : lostAndFoundItems) {
            LatLng location = new LatLng(item.getLat(), item.getLon());
            if (item.getLostOrFound().equalsIgnoreCase("lost")) {
                mMap.addMarker(new MarkerOptions().position(location).icon(lostIcon).title(item.getName()));
            } else {
                mMap.addMarker(new MarkerOptions().position(location).icon(foundIcon).title(item.getName()));
            }

        }

        // Move the camera to the first item's location (if available)
        if (!lostAndFoundItems.isEmpty()) {
            Item firstItem = lostAndFoundItems.get(0);
            LatLng firstItemLocation = new LatLng(firstItem.getLat(), firstItem.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstItemLocation, 12));
        }
    }

    private BitmapDescriptor getResizedBitmapDescriptor(int resourceId, int width, int height) {
        Resources resources = getApplicationContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

}