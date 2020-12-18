package com.jdrapid.rapidfast.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdrapid.rapidfast.models.Cliente;
import com.jdrapid.rapidfast.models.Conductor;

import java.util.HashMap;
import java.util.Map;

public class ConductorProvider {

    DatabaseReference reference;

    public  ConductorProvider() {
        reference= FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Conductores");
    }
    public Task<Void> create(Conductor conductor){
        return reference.child(conductor.getId()).setValue(conductor);
    }
    public Task<Void> actualizar(Conductor conductor){
        Map<String,Object> map=new HashMap<>();
        map.put("nombre",conductor.getNombre());
        map.put("marcaVehiculo",conductor.getMarcaVehiculo());
        map.put("placaVehiculo",conductor.getPlacaVehiculo());
        map.put("imagen",conductor.getImagen());
        return reference.child(conductor.getId()).updateChildren(map);
    }
    public DatabaseReference getConductor(String idConductor){
        return reference.child(idConductor);
    }
}
