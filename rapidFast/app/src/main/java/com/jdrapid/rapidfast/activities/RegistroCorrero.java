package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.Cliente;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;

import dmax.dialog.SpotsDialog;

import static com.jdrapid.rapidfast.R.string.CamposVacios;
import static com.jdrapid.rapidfast.R.string.contrasena;

public class RegistroCorrero extends AppCompatActivity {

    Button btnRegistro;
    TextInputEditText tnombre,tcorreo,tcontrase,tconfirmar;
    AlertDialog alertDialog,alert2;
    AuthProvider authProvider;
    ClienteProvider clienteProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_correro);
        ToolBar.mostrar(this,getString(R.string.ToolBarRegistro),true);


        alertDialog=new SpotsDialog.Builder().setContext(RegistroCorrero.this).setMessage("Espere un momento").build();
        alert2=new SpotsDialog.Builder().setContext(RegistroCorrero.this).setMessage("Las contraseñas no coinciden").setCancelable(false).build();

        authProvider=new AuthProvider();
        clienteProvider=new ClienteProvider();

        tnombre=findViewById(R.id.InNombre);
        tcorreo=findViewById(R.id.InEmail);
        tcontrase=findViewById(R.id.InContrasena);
        tconfirmar=findViewById(R.id.InRepetirContrasena);


        btnRegistro=findViewById(R.id.BtnRegistrar);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRegistrarUsuario();
            }
        });
    }


    private void ClickRegistrarUsuario() {
        final String nombre=tnombre.getText().toString();
        final  String correo=tcorreo.getText().toString();
        final  String contrasena=tcontrase.getText().toString();
        final  String repetir=tcontrase.getText().toString();


        if (!nombre.isEmpty() || !correo.isEmpty()){
            alertDialog.show();
            registar(nombre,correo,contrasena);
        }else{
            Toast.makeText(this, CamposVacios,Toast.LENGTH_LONG).show();
        }

    }

    private void registar(String nombre,String correo, String contrasena) {
        authProvider.Registo(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                alertDialog.hide();
                if (task.isSuccessful()){
                    String id= FirebaseAuth.getInstance().getCurrentUser().getUid();

                }else{
                    Toast.makeText(RegistroCorrero.this,"Este usuario ya existe",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void actualizar(Cliente cliente){
        clienteProvider.actualizarRegistro(cliente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(RegistroCorrero.this, MapClienteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegistroCorrero.this, "Error cleinte ya registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}