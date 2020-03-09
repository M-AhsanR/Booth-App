package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AboutBoothActivity extends FragmentActivity implements OnMapReadyCallback {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    GoogleMap mMap;
    String longitude, latitude;
    LatLng user_location;
    LinearLayout map_layout;
    String addressLine;
    TextView title,user_name,address,description;

    @Override
    protected void onStop() {
        super.onStop();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

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
            setContentView(R.layout.activity_about_booth);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            Bundle bundle = getIntent().getExtras();
            latitude = bundle.getString("Latitude");
            longitude = bundle.getString("Longitude");
            addressLine = bundle.getString("Address");
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            initilizeViews();
            aboutBooth();
            if (!latitude.equals("") && !longitude.equals("")){
                map_layout.setVisibility(View.VISIBLE);
            }else {
                map_layout.setVisibility(View.GONE);
            }
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void initilizeViews() {
        title = findViewById(R.id.title);
        user_name = findViewById(R.id.user_name);
        address =findViewById(R.id.address);
        description = findViewById(R.id.myDescription);
        map_layout = findViewById(R.id.map_layout);
    }

    private void aboutBooth(){
        Bundle bundle = getIntent().getExtras();
        title.setText(bundle.getString("BOOTH_NAME"));
        user_name.setText(bundle.getString("BOOTH_USERNAME"));
        address.setText(bundle.getString("BOOTH_ADDRESS"));
        description.setText(bundle.getString("BOOTH_DESCRIPTION"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.

        if (!latitude.equals("") && !longitude.equals("")){
            user_location = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        }else {
            user_location = new LatLng(0.0, 0.0);
        }

        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(user_location);
        markerOptions.title(addressLine);
        googleMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(user_location)        // Sets the center of the map to Mountain View
                .zoom(17)              // Sets the zoom
                .bearing(90)           // Sets the orientation of the camera to east
                .tilt(0)               // Sets the tilt of the camera to 30 degrees
                .build();              // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
