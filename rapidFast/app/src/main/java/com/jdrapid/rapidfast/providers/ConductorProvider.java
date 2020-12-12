package com.jdrapid.rapidfast.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdrapid.rapidfast.models.Cliente;
import com.jdrapid.rapidfast.models.Conductor;

public class ConductorProvider {

    DatabaseReference reference;

    public  ConductorProvider() {
        reference= FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Conductores");
    }
    public Task<Void> create(Conductor conductor){
        return reference.child(conductor.getId()).setValue(conductor);
    }
    public DatabaseReference getConductor(String idConductor){
        return reference.child(idConductor);
    }
}
