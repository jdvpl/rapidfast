package com.jdrapid.rapidfast.models;

import com.google.android.gms.maps.model.LatLng;

public class ConductorLocation {
    String id;
    LatLng latLng;

    public ConductorLocation() {
    }

    public ConductorLocation(String id, LatLng latLng) {
        this.id = id;
        this.latLng = latLng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
