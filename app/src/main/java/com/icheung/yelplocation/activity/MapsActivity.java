package com.icheung.yelplocation.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icheung.yelplocation.R;
import com.icheung.yelplocation.model.Business;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String KEY_DATA = "data";

    private GoogleMap mMap;
    private ArrayList<Business> mBusinesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mBusinesses = (ArrayList<Business>) getIntent().getSerializableExtra("data");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        if(mBusinesses.size() > 0) {
            for(Business business : mBusinesses) {
                LatLng coord = new LatLng(business.getLocation().getCoordinate().getLatitude(),
                        business.getLocation().getCoordinate().getLongitude());
                mMap.addMarker(new MarkerOptions().position(coord).title(business.getName()));
            }
        }

        // Center on first business
        if(mBusinesses.size() > 0) {
            LatLng coord = new LatLng(mBusinesses.get(0).getLocation().getCoordinate().getLatitude(),
                    mBusinesses.get(0).getLocation().getCoordinate().getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 12));
        }
        // Center on US
        else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.77476949, -97.3828125), 4));
        }
    }
}
