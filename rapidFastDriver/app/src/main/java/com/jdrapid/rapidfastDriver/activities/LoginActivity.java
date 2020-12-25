package com.jdrapid.rapidfastDriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdrapid.rapidfastDriver.R;

import com.jdrapid.rapidfastDriver.includes.ToolBar;

//strings
import dmax.dialog.SpotsDialog;

import static com.jdrapid.rapidfastDriver.R.string.EmailContaseñaobligatorio;
import static com.jdrapid.rapidfastDriver.R.string.LoginActivityemailYpassworsIncorectos;
import static com.jdrapid.rapidfastDriver.R.string.passwordseis;

public class LoginActivity extends AppCompatActivity {
//    variable para forebase
    FirebaseAuth auth;
    DatabaseReference mDatabase;
//    viarable del activyti
    TextInputEditText InEmail,InContrasena;
    Button BtnLogin;

//    Alert
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InEmail=findViewById(R.id.txtInputCorrero);
        InContrasena=findViewById(R.id.txtInputContrasena);
        BtnLogin=findViewById(R.id.BtnLogin);
//        toolbar
        ToolBar.mostrar(this,getString(R.string.ToolBarLogin),true);

//        hacer instancia
        auth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
//        alert instance
        alertDialog=new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();

        
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

    }

    private void Login() {
        String correo=InEmail.getText().toString();
        String contrasena=InContrasena.getText().toString();
        if (!correo.isEmpty() && !contrasena.isEmpty()){
            if (contrasena.length()>=6){
                alertDialog.show();
                auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent=new Intent(LoginActivity.this, MapConductorActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, LoginActivityemailYpassworsIncorectos,Toast.LENGTH_LONG).show();
                        }
                        alertDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(LoginActivity.this, passwordseis,Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(LoginActivity.this, EmailContaseñaobligatorio,Toast.LENGTH_LONG).show();
        }
    }
}