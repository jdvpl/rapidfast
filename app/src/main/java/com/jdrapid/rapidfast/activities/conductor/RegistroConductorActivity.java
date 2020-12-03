
package com.jdrapid.rapidfast.activities.conductor;

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
import com.jdrapid.rapidfast.models.Conductor;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;

import dmax.dialog.SpotsDialog;

import static com.jdrapid.rapidfast.R.string.CamposVacios;
import static com.jdrapid.rapidfast.R.string.seiscaracteres;

public class RegistroConductorActivity extends AppCompatActivity {



    Button btnRegistro;
    TextInputEditText tnombre,tcorreo,tontrase,tcedula,tvehculo,tplaca;
    AlertDialog alertDialog;
    AuthProvider authProvider;
    ConductorProvider conductorProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_conductor);
        ToolBar.mostrar(this,getString(R.string.RegistrodeConducto),true);


        alertDialog=new SpotsDialog.Builder().setContext(RegistroConductorActivity.this).setMessage("Espere un momento").build();

        authProvider=new AuthProvider();
        conductorProvider=new ConductorProvider();

        tnombre=findViewById(R.id.InNombre);
        tcorreo=findViewById(R.id.InEmail);
        tontrase=findViewById(R.id.InContrasena);
        tcedula=findViewById(R.id.InCedula);
        tvehculo=findViewById(R.id.InMarca);
        tplaca=findViewById(R.id.InPlaca);


        btnRegistro=findViewById(R.id.BtnRegistrar);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRegistrarUsuario();
            }
        });
    }


    private void ClickRegistrarUsuario() {
        final String cedula=tcedula.getText().toString();
        final String nombre=tnombre.getText().toString();
        final  String correo=tcorreo.getText().toString();
        final  String vehiculo=tvehculo.getText().toString();
        final  String placa=tplaca.getText().toString();
        final String contrasena=tontrase.getText().toString();

        if (!nombre.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty() || !cedula.isEmpty() || !vehiculo.isEmpty() || !placa.isEmpty()){
            if(contrasena.length()>=6){
                alertDialog.show();
                registar(cedula,nombre,correo,vehiculo,placa,contrasena);
            }else{
                Toast.makeText(this, seiscaracteres,Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, CamposVacios,Toast.LENGTH_LONG).show();
        }

    }

    private void registar(String cedula,String nombre,String correo, String vehiculo,String placa,String contrasena) {
        authProvider.Registo(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                alertDialog.hide();
                if (task.isSuccessful()){
                    String id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Conductor conductor=new Conductor(id,cedula,nombre,correo,vehiculo,placa);
                    create(conductor);
                }else{
                    Toast.makeText(RegistroConductorActivity.this,"Este correo ya existe",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void create(Conductor conductor){
        conductorProvider.create(conductor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(RegistroConductorActivity.this, MapConductorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegistroConductorActivity.this, "Error cleinte ya registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}