package com.jdrapid.rapidfast.activities.conductor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.jdrapid.rapidfast.R;

public class MapConductorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap nMap;
    private SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_conductor);

        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap=googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}