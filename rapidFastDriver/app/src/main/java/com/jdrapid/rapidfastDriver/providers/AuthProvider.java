package com.jdrapid.rapidfastDriver.providers;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

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
    public void EnviarCodigoverificacion(String telefono, Context context, PhoneAuthProvider.OnVerificationStateChangedCallbacks callback){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                telefono,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                callback
        );
    }
    public Task<AuthResult> IniciarTelefono(String verificacionId,String codigo){
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificacionId,codigo);
        return  auth.signInWithCredential(credential);
    }
    public void logout(){
        auth.signOut();
    }

    public String getId(){

        return auth.getCurrentUser().getUid();
    }
    public boolean existeSesion(){
        boolean existe=false;
        if (auth.getCurrentUser()!=null){
            existe=true;
        }
        return existe;
    }
}
