
package com.jdrapid.rapidfastDriver.activities;

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
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.includes.ToolBar;
import com.jdrapid.rapidfastDriver.models.Conductor;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.ConductorProvider;

import dmax.dialog.SpotsDialog;

import static com.jdrapid.rapidfastDriver.R.string.CamposVacios;
import static com.jdrapid.rapidfastDriver.R.string.seiscaracteres;

public class RegistroConductorActivity extends AppCompatActivity {



    Button btnRegistro;
    TextInputEditText tnombre,tcorreo,tontrase,tcedula,tvehculo,tplaca,tconfirmar;
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


        if (!nombre.isEmpty() || !correo.isEmpty()  || !cedula.isEmpty() || !vehiculo.isEmpty() || !placa.isEmpty()){
            alertDialog.show();

            Conductor conductor=new Conductor();
            conductor.setId(authProvider.getId());
            conductor.setNombre(nombre);
            conductor.setMarcaVehiculo(vehiculo);
            conductor.setPlacaVehiculo(placa);
            conductor.setCedula(cedula);
            conductor.setCorreo(correo);
            actualizar(conductor);
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

                }else{
                    Toast.makeText(RegistroConductorActivity.this,"Este correo ya existe",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void actualizar(Conductor conductor){
        conductorProvider.actualizarRegistro(conductor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(RegistroConductorActivity.this, MapConductorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegistroConductorActivity.this, "Error cliente ya registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}