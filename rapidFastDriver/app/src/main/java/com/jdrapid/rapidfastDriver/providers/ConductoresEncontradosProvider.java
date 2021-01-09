package com.jdrapid.rapidfastDriver.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jdrapid.rapidfastDriver.models.ConductorEncontrado;


public class ConductoresEncontradosProvider {
    DatabaseReference databaseReference;

    public ConductoresEncontradosProvider() {
//        es para saber a que coneductores le enviamos la solicitud
        databaseReference= FirebaseDatabase.getInstance().getReference().child("ConductoresEcontrados");
    }
    public Task<Void> Crear(ConductorEncontrado conductorEncontrado){
        return  databaseReference.child(conductorEncontrado.getIdConductor()).setValue(conductorEncontrado);
    }
    //si un conductor ya recibio o esta recibiendo la notificacion
    public Query ObtnerConductorEncontradoByID(String iDConductor){
        return  databaseReference.orderByChild("idConductor").equalTo(iDConductor);
    }
    public Task<Void> Borrar(String IdCondutor){
        return databaseReference.child(IdCondutor).removeValue();
    }
}
