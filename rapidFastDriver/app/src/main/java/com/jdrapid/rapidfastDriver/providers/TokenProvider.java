package com.jdrapid.rapidfastDriver.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jdrapid.rapidfastDriver.models.Token;

public class TokenProvider {
    DatabaseReference databaseReference;
    public TokenProvider() {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Tokens");
    }
    public void Crear(String IdUsuario){
        if (IdUsuario==null) return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                Token token=new Token(instanceIdResult.getToken());
                databaseReference.child(IdUsuario).setValue(token);
            }
        });
    }
    public DatabaseReference getToken(String IdUsuario){
        return databaseReference.child(IdUsuario);

    }
    public void deleteToken(String idUser) {
        databaseReference.child(idUser).removeValue();
    }
}
