package com.jdrapid.rapidfastDriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.includes.ToolBar;
import com.jdrapid.rapidfastDriver.models.Afiliados;
import com.jdrapid.rapidfastDriver.providers.AfiliadoPrivider;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class AfiliadosActivity extends AppCompatActivity {
    Button BtnAgregarAfi;
    EditText TxtCedulaAfi,TxtNombre,TxtCelular;
    AfiliadoPrivider afiliadoPrivider;
    AlertDialog alertDialog;
    AuthProvider authProvider;
    DatabaseReference reference;
    Date hoy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afiliados);

        ToolBar.mostrar(this,"Afiliados",true);
        alertDialog=new SpotsDialog.Builder().setContext(AfiliadosActivity.this).setMessage("Agregando un nuevo usuario").build();

        BtnAgregarAfi=findViewById(R.id.BtnAgregar);
        TxtCedulaAfi=findViewById(R.id.txtCedulaafi);
        TxtNombre=findViewById(R.id.txtNombrecompleto);
        TxtCelular=findViewById(R.id.txtCelularafi);
        reference= FirebaseDatabase.getInstance().getReference().child("Afiliados");
        authProvider=new AuthProvider();
        afiliadoPrivider=new AfiliadoPrivider();

        BtnAgregarAfi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarAfiliado();
            }
        });
        
    }

    private void AgregarAfiliado() {
        final  String Cedula=TxtCedulaAfi.getText().toString();
        final  String nombre=TxtNombre.getText().toString();
        final  String celular=TxtCelular.getText().toString();
        String idAfiliado=reference.push().getKey();

        hoy=new Date();
        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

        if (!nombre.isEmpty() && !Cedula.isEmpty() && celular.isEmpty()){
            alertDialog.show();
            Afiliados afiliado=new Afiliados();
            afiliado.setIdAfiliado(idAfiliado);
            afiliado.setIdConductor(authProvider.getId());
            afiliado.setNombre(nombre);
            afiliado.setCedula(Cedula);
            afiliado.setCelular(celular);
            afiliado.setEstado("Creado");
            afiliado.setFecha(dateFormat.format(hoy));
            Crear(afiliado);
        }else{
            Toast.makeText(this, "Por favor completar todos los campos",Toast.LENGTH_LONG).show();
        }
    }

    private void Crear(Afiliados afiliado){
        afiliadoPrivider.create(authProvider.getId(),afiliado).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    finish();
                    Toast.makeText(AfiliadosActivity.this, "Afliliado registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}