package com.jdrapid.rapidfast.activities.cliente;

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
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.conductor.MapConductorActivity;
import com.jdrapid.rapidfast.activities.conductor.RegistroConductorActivity;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.Cliente;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;

import dmax.dialog.SpotsDialog;

import static com.jdrapid.rapidfast.R.string.CamposVacios;
import static com.jdrapid.rapidfast.R.string.seiscaracteres;

public class RegistroClienteActivity extends AppCompatActivity {
    //    preferencia para pasar datos
    SharedPreferences preferences;

    Button btnRegistro;
    TextInputEditText tnombre,tcorreo,tontrase;
    AlertDialog alertDialog;
    AuthProvider authProvider;
    ClienteProvider clienteProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ToolBar.mostrar(this,getString(R.string.ToolBarRegistro),true);

//        para obtener si es usuario o conductor
        preferences=getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);

        alertDialog=new SpotsDialog.Builder().setContext(RegistroClienteActivity.this).setMessage("Espere un momento").build();

        authProvider=new AuthProvider();
        clienteProvider=new ClienteProvider();

        tnombre=findViewById(R.id.InNombre);
        tcorreo=findViewById(R.id.InEmail);
        tontrase=findViewById(R.id.InContrasena);

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
        final String contrasena=tontrase.getText().toString();

        if (!nombre.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty()){
            if(contrasena.length()>=6){
                alertDialog.show();
                    registar(nombre,correo,contrasena);
                }else{
                Toast.makeText(this, seiscaracteres,Toast.LENGTH_LONG).show();
            }
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
                    String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Cliente cliente=new Cliente(id,nombre,correo);
                    create(cliente);
                }else{
                    Toast.makeText(RegistroClienteActivity.this,"Este correo ya existe",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void create(Cliente cliente){
        clienteProvider.create(cliente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(RegistroClienteActivity.this, MapClienteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegistroClienteActivity.this, "Error cleinte ya registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
/*
    private void guardarUsuario(String Id,String nombre,String correo) {
        String eleccion=preferences.getString("user","");
        Usuario usuario=new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);

        if (eleccion.equals("conductor")){
            reference.child("Usuarios").child("Conductores").child(Id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistroActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(RegistroActivity.this,"Registro No exitoso",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }else if (eleccion.equals("cliente")){
            reference.child("Usuarios").child("Clientes").child(Id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistroActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(RegistroActivity.this,"Registro No exitoso",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

 */
}