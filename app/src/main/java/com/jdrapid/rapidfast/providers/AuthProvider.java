package com.jdrapid.rapidfast.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {
    FirebaseAuth auth;

    public AuthProvider() {
        auth=FirebaseAuth.getInstance();
    }
    public Task<AuthResult> Registo(String correo, String contrasena){
        return auth.createUserWithEmailAndPassword(correo,contrasena);
    }
    public Task<AuthResult> Login(String correo, String contrasena){
        return auth.signInWithEmailAndPassword(correo,contrasena);
    }
}
