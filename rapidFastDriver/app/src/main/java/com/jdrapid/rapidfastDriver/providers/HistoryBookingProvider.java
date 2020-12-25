package com.jdrapid.rapidfastDriver.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jdrapid.rapidfastDriver.models.HistoryBooking;

import java.util.HashMap;
import java.util.Map;

public class HistoryBookingProvider {

    private DatabaseReference databaseReference;
    public HistoryBookingProvider() {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("HistorialViajes");
    }

    public Task<Void> Crear(HistoryBooking historyBooking){
        return databaseReference.child(historyBooking.getIdHistorialSolicitud()).setValue(historyBooking);
    }

    public Task<Void> ActualizarCalificacionCliente(String idHistory,float CalificacionCleinte){
        Map<String,Object> map=new HashMap<>();
        map.put("calificacionCliente",CalificacionCleinte);
        return databaseReference.child(idHistory).updateChildren(map);
    }
    public DatabaseReference getHistorialReserva(String idHistory){
        return databaseReference.child(idHistory);
    }
    public Task<Void> ActualizarCalificacionConducotor(String idHistory,float CalificacionConductor){
        Map<String,Object> map=new HashMap<>();
        map.put("calificacionConductor",CalificacionConductor);
        return databaseReference.child(idHistory).updateChildren(map);
    }

}
