package com.application.lostandfound;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class NewAdvertActivity extends AppCompatActivity {

    private RadioButton lostRadioButton, foundRadioButton;
    private EditText editTextName, editTextPhone, editTextDescription, editTextDate, editTextLocation;
    private Button getCurrLocationButton, saveButton;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double lat, lon;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advert);

        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY");

            Places.initialize(this, apiKey);
            placesClient = Places.createClient(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        lostRadioButton = findViewById(R.id.lostRadioButton);
        foundRadioButton = findViewById(R.id.foundRadioButton);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        getCurrLocationButton = findViewById(R.id.getCurrLocationButton);
        saveButton = findViewById(R.id.saveButton);
        
        // Initialize the date picker dialog
        dateSetListener = (view, year, month, dayOfMonth) -> {
            month += 1;
            String monthString;
            if (month < 10) {
                monthString = "0" + month;
            } else {
                monthString = String.valueOf(month);
            }
            String dateString = dayOfMonth + "/" + monthString + "/" + year;
            editTextDate.setText(dateString);
        };

        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        getLocationPermission();

        // Because setOnClickListener does not seem to get triggered when the user clicks on the location EditText widget
        editTextLocation.setOnFocusChangeListener((v, event) -> {
            if (event) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                startAutocomplete.launch(intent);
                editTextLocation.clearFocus();
            }
        });

        editTextDate.setOnFocusChangeListener((v, event) -> {
            if (event) {
                datePickerDialog.show();
                editTextDate.clearFocus();
            }
        });

        lostRadioButton.setOnClickListener(v -> {
            if (lostRadioButton.isChecked()) {
                foundRadioButton.setChecked(false);
            }
        });

        foundRadioButton.setOnClickListener(v -> {
            if (foundRadioButton.isChecked()) {
                lostRadioButton.setChecked(false);
            }
        });

        getCurrLocationButton.setOnClickListener(v -> {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Use the location coordinates to retrieve place information
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (!addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String currentLocation = address.getAddressLine(0);

                                    editTextLocation.setText(currentLocation);
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                    System.out.println("Latitude: " + lat + " Longitude: " + lon);
                                } else {
                                    Toast.makeText(this, "Error retrieving current location", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            } else {
                System.out.println("Location permission not granted");
            }

        });

        saveButton.setOnClickListener(v -> {
            if (validateEntries()) {
                // Save advert
                if (DatabaseHelper.getInstance().insertItem(
                        editTextName.getText().toString(),
                        lostRadioButton.isChecked() ? "Lost" : "Found",
                        editTextPhone.getText().toString(),
                        editTextDescription.getText().toString(),
                        editTextLocation.getText().toString(),
                        lat,
                        lon,
                        editTextDate.getText().toString()
                    )) {
                    openMainActivity();
                } else {
                    // Show error message
                    Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    Boolean validateEntries() {
        boolean check = true;
        if (editTextName.getText().toString().isEmpty()) {
            editTextName.setError("Please enter your name");
            check = false;
        }
        if (editTextPhone.getText().toString().isEmpty()) {
            editTextPhone.setError("Please enter your phone number");
            check = false;
        }
        if (editTextDescription.getText().toString().isEmpty()) {
            editTextDescription.setError("Please enter a description");
            check = false;
        }
        if (editTextDate.getText().toString().isEmpty()) {
            editTextDate.setError("Please enter a date");
            check = false;
        }
        if (editTextLocation.getText().toString().isEmpty() || lat == null || lon == null) {
            editTextLocation.setError("Please enter a location");
            check = false;
        }
        return check;
    }

    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i(TAG, String.format("Place: %s, %s", place.getName(), place.getId()));
                        editTextLocation.setText(place.getName());
                        System.out.println(place.getLatLng().toString());
                        lat = Objects.requireNonNull(place.getLatLng()).latitude;
                        lon = Objects.requireNonNull(place.getLatLng()).longitude;
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void openMainActivity() {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

}