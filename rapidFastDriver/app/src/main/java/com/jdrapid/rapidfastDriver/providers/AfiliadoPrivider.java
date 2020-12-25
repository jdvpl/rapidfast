package com.jdrapid.rapidfastDriver.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdrapid.rapidfastDriver.models.Afiliados;

import java.util.HashMap;
import java.util.Map;

public class AfiliadoPrivider {

    DatabaseReference reference;
    public AfiliadoPrivider() {
        reference= FirebaseDatabase.getInstance().getReference().child("Afiliados");
    }
    public Task<Void> create(String IdConductor,Afiliados afiliados){
        return reference.child(IdConductor).child(afiliados.getIdAfiliado()).setValue(afiliados);
    }
    public Task<Void> actualizarHistoryBooking(String IdSolicitudCliente){
        String idpush=reference.push().getKey();
        Map<String,Object> map=new HashMap<>();
        map.put("idHistorialSolicitud",idpush);
        return reference.child(IdSolicitudCliente).updateChildren(map);
    }

}
