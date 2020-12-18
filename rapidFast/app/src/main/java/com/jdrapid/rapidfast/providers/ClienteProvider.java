package com.jdrapid.rapidfast.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdrapid.rapidfast.models.Cliente;

import java.util.HashMap;
import java.util.Map;

public class ClienteProvider {

    DatabaseReference reference;

    public ClienteProvider() {
        reference= FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Clientes");
    }
    public Task<Void> create(Cliente cliente){
        Map<String,Object> map=new HashMap<>();
        map.put("Nombre",cliente.getNombre());
        map.put("Correo",cliente.getCorreo());
        return reference.child(cliente.getId()).setValue(map);
    }
    public Task<Void> actualizar(Cliente cliente){
        Map<String,Object> map=new HashMap<>();
        map.put("Nombre",cliente.getNombre());
        map.put("imagen",cliente.getImagen());
        return reference.child(cliente.getId()).updateChildren(map);
    }
    public DatabaseReference getCliente(String idCliente){
        return reference.child(idCliente);
    }
}
