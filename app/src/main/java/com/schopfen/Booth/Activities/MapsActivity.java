package com.schopfen.Booth.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.schopfen.Booth.Adapters.CheckOutForAddressRVAdapter;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.GpsTracker;
import com.schopfen.Booth.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private MapView mapView;
    private GoogleMap gmap;
    double lt, ln;
    public static double lat = 0.0, lng = 0.0;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    Button confirmBtn;
    TextView Address;
    LinearLayout search;
    GpsTracker gpsTracker;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String city = "";
    BaseClass baseClass;
    LinearLayout main;
    public static final int AUTOCOMPLETE_REQUEST_CODE = 5;

    public static String AddressLine;
    public static String cityLine;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()){
            setContentView(R.layout.activity_maps);
            // Initialize Places.
            Places.initialize(getApplicationContext(), "AIzaSyCrYoU_luhraLgDpRs6upjx0xSlNJptdMw");
            initializeiews();
            initializeClickListeners();

            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            }

            gpsTracker = new GpsTracker(MapsActivity.this);
            baseClass = new BaseClass(this);
            if (gpsTracker.canGetLocation()) {
            } else {
                gpsTracker.showSettingsAlert();
            }


            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Address.getText().toString().equals("No Address")) {
                        Toast.makeText(MapsActivity.this, "Select a Valid Address First!", Toast.LENGTH_SHORT).show();
                    } else {
                        AddressLine = Address.getText().toString();
                        cityLine = city;
                        finish();
                    }
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onClick(View v) {
        if (v == search) {
            search();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable()){
            mapView.onResume();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gmap.setMyLocationEnabled(true);
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            lt = location.getLongitude();
            ln = location.getLatitude();
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);


        try {
            LatLng location1 = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate latLngZoom = CameraUpdateFactory.newLatLngZoom(location1, 12);
            gmap.animateCamera(latLngZoom);
        } catch (NullPointerException e) {
            LatLng location1 = new LatLng(Double.parseDouble("23.8859"), Double.parseDouble("45.0792"));
            CameraUpdate latLngZoom = CameraUpdateFactory.newLatLngZoom(location1, 12);
            gmap.animateCamera(latLngZoom);
            Log.e("testing", e.toString());

        }
        Log.e("testing", String.valueOf(lt));


        gmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                Log.e("centerLat", String.valueOf(cameraPosition.target.latitude));
                Log.e("centerLong", String.valueOf(cameraPosition.target.longitude));

                Address.setText(getCompleteAddressString(cameraPosition.target.latitude, cameraPosition.target.longitude));
                lat = cameraPosition.target.latitude;
                lng = cameraPosition.target.longitude;
                Log.e("address", getCompleteAddressString(cameraPosition.target.latitude, cameraPosition.target.longitude));

            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("place", place.getId() + place.toString());
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<android.location.Address> addresses = null;
                try {
                    addresses = gcd.getFromLocationName(place.getName(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null) {
                    if (addresses.size() > 0) {
                        Log.e("place", addresses.toString());
                        if (addresses.get(0).getCountryName().equals("Saudi Arabia") || addresses.get(0).getCountryName().equals("السعودية")) {
                            Address.setText(addresses.get(0).getAddressLine(0));
                            lat = addresses.get(0).getLatitude();
                            lng = addresses.get(0).getLongitude();
                            city = addresses.get(0).getLocality();
                        } else {
                            LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                            CameraUpdate latLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                            gmap.animateCamera(latLngZoom);
                            Address.setText(getCompleteAddressString(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
                        }
                    }
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("status", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
//                    Address.setText(place.getAddress().toString());
//                    lat = place.getLatLng().latitude;
//                    lng = place.getLatLng().longitude;
                city = addresses.get(0).getLocality();

                Log.e("testingerr", strAdd);

//                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
//                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Address.setText(getResources().getString(R.string.noaddress));
            Log.e("testingerr", e.toString() + "  " + e.getLocalizedMessage());
//            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            lt = location.getLongitude();
            ln = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();


        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            LatLng location1 = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate latLngZoom = CameraUpdateFactory.newLatLngZoom(location1, 12);
            gmap.animateCamera(latLngZoom);
        } catch (NullPointerException e) {
            LatLng location1 = new LatLng(Double.parseDouble("23.8859"), Double.parseDouble("45.0792"));
            CameraUpdate latLngZoom = CameraUpdateFactory.newLatLngZoom(location1, 12);
            gmap.animateCamera(latLngZoom);
            Log.e("testing", e.toString());

        }
    }

    private void initializeiews() {
        Address = findViewById(R.id.adress);
        mapView = findViewById(R.id.map_view);
        search = findViewById(R.id.search);
        confirmBtn = findViewById(R.id.confirmBtn);
        main = findViewById(R.id.main);
    }

    private void initializeClickListeners() {
        search.setOnClickListener(this);
    }

    private void search() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCrYoU_luhraLgDpRs6upjx0xSlNJptdMw");
        }
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(MapsActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }
}
