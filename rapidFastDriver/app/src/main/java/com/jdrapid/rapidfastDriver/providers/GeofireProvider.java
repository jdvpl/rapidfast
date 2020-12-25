package com.jdrapid.rapidfastDriver.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference mDatabse;
    private GeoFire mGeofire;

    public GeofireProvider(String reference){
        mDatabse= FirebaseDatabase.getInstance().getReference().child(reference);
        mGeofire=new GeoFire(mDatabse);
    }
    public void guardarUbicacion(String IdConductor, LatLng latLng){
        mGeofire.setLocation(IdConductor,new GeoLocation(latLng.latitude,latLng.longitude));
    }
    public void EliminarUbicacion(String IdConductor){
        mGeofire.removeLocation(IdConductor);
    }

    public GeoQuery ConductoresActivos(LatLng latLng,double Radio){
        GeoQuery geoQuery=mGeofire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude), Radio);
        geoQuery.removeAllListeners();
        return geoQuery;
    }
    public DatabaseReference obtenerUbicacionConductor(String idConductor){
        return mDatabse.child(idConductor).child("l");
    }

    public DatabaseReference obteneConductor(String idConductor){
        return mDatabse.child(idConductor);
    }

    public DatabaseReference ConductoresTrabajando(String idConductor){
        return FirebaseDatabase.getInstance().getReference().child("Conductores_trabajando").child(idConductor);
    }
    public Task<Void> BorrarConductoresTrabajando(String idConductor){
        return FirebaseDatabase.getInstance().getReference().child("Conductores_trabajando").child(idConductor).removeValue();
    }
}
