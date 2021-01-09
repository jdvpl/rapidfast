package com.jdrapid.rapidfastDriver.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdrapid.rapidfastDriver.models.ClientBooking;

import java.util.HashMap;
import java.util.Map;

public class ClienteReservaProvider {
    private DatabaseReference databaseReference;
    public ClienteReservaProvider() {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("SolicitudesCliente");


    }
    public Task<Void> Crear(ClientBooking clientBooking){
        return databaseReference.child(clientBooking.getIdCliente()).setValue(clientBooking);
    }
    public Task<Void> actualizarEstado(String IdSolicitudCliente,String estado){
        Map<String,Object> map=new HashMap<>();
        map.put("estado",estado);
        return databaseReference.child(IdSolicitudCliente).updateChildren(map);
    }
    public Task<Void> actualizarEstadoandIdDriver(String IdSolicitudCliente,String estado,String idConductor){
        Map<String,Object> map=new HashMap<>();
        map.put("estado",estado);
        map.put("idConductor",idConductor);
        return databaseReference.child(IdSolicitudCliente).updateChildren(map);
    }

    public Task<Void> actualizarHistoryBooking(String IdSolicitudCliente){
        String idpush=databaseReference.push().getKey();
        Map<String,Object> map=new HashMap<>();
        map.put("idHistorialSolicitud",idpush);
        return databaseReference.child(IdSolicitudCliente).updateChildren(map);
    }
    public Task<Void> actualizarPrecio(String IdSolicitudCliente,double precio){
        Map<String,Object> map=new HashMap<>();
        map.put("precio",precio);
        return databaseReference.child(IdSolicitudCliente).updateChildren(map);
    }

    public DatabaseReference getEstado(String idSolicitudCliente){
        return databaseReference.child(idSolicitudCliente).child("estado");
    }
    public DatabaseReference getClienteSolicitud(String idSolicitudCliente){
        return databaseReference.child(idSolicitudCliente);
    }
    public Task<Void> borrar(String idCLienteSolicitud){
        return databaseReference.child(idCLienteSolicitud).removeValue();
    }

}
