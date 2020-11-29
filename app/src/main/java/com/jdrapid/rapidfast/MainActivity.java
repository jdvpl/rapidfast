package com.jdrapid.rapidfast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button mConductor;
    Button mUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConductor=findViewById(R.id.BtnConductor);
        mUsuario=findViewById(R.id.BtnUsuario);



        mUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irSelectAuth();
            }
        });
        mConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irSelectAuth();
            }
        });
    }

    private void irSelectAuth() {
        Intent abrirOpcionActivity=new Intent(MainActivity.this,SeletAuthOptionActivity.class);
        startActivity(abrirOpcionActivity);
    }
}