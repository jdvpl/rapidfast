package com.jdrapid.rapidfast.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jdrapid.rapidfast.models.HistoryBooking;

import java.util.HashMap;
import java.util.Map;

public class HistoryBookingProvider {

    private final DatabaseReference databaseReference;
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
    public Task<Void> ActualizarCalificacionConducotor(String idHistory,float CalificacionConductor,String mensajeConductor){
        Map<String,Object> map=new HashMap<>();
        map.put("calificacionConductor",CalificacionConductor);
        map.put("mensajeConductor",mensajeConductor);
        return databaseReference.child(idHistory).updateChildren(map);
    }


}
