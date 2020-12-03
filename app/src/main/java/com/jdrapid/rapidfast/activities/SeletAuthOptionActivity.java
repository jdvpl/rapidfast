package com.jdrapid.rapidfast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.cliente.RegistroClienteActivity;
import com.jdrapid.rapidfast.activities.conductor.RegistroConductorActivity;
import com.jdrapid.rapidfast.includes.ToolBar;


public class SeletAuthOptionActivity extends AppCompatActivity {
    Button BtnLogin,BtnRegistro;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selet_auth_option);

        ToolBar.mostrar(this,getString(R.string.seleccionar_accion),true);

        preferences=getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);

        BtnLogin=findViewById(R.id.BtnLogin);
        BtnRegistro=findViewById(R.id.BtnRegistro);
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iraLogin();
            }
        });

        BtnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iraRegister();
            }
        });
    }

    private void iraLogin() {
        Intent intent=new Intent(SeletAuthOptionActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    private void iraRegister() {
        String tipoUsuario=preferences.getString("user","");
        if (tipoUsuario.equals("cliente")){
            Intent intent1=new Intent(SeletAuthOptionActivity.this, RegistroClienteActivity.class);
            startActivity(intent1);
        }else {
            Intent intent1=new Intent(SeletAuthOptionActivity.this, RegistroConductorActivity.class);
            startActivity(intent1);
        }

    }
}