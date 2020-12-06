package com.jdrapid.rapidfast.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference mDatabse;
    private GeoFire mGeofire;

    public GeofireProvider(){
        mDatabse= FirebaseDatabase.getInstance().getReference().child("Conductores_Activos");
        mGeofire=new GeoFire(mDatabse);
    }
    public void guardarUbicacion(String IdConductor, LatLng latLng){
        mGeofire.setLocation(IdConductor,new GeoLocation(latLng.latitude,latLng.longitude));
    }
    public void EliminarUbicacion(String IdConductor){
        mGeofire.removeLocation(IdConductor);
    }
}
