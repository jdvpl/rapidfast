package com.jdrapid.rapidfast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jdrapid.rapidfast.includes.ToolBar;


public class SeletAuthOptionActivity extends AppCompatActivity {
    Button BtnLogin,BtnRegistro;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selet_auth_option);

        ToolBar.mostrar(this,getString(R.string.seleccionar_accion),true);


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
        Intent intent1=new Intent(SeletAuthOptionActivity.this,RegistroActivity.class);
        startActivity(intent1);
    }
}