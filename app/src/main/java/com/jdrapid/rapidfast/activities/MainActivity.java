package com.jdrapid.rapidfast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jdrapid.rapidfast.R;

public class MainActivity extends AppCompatActivity {
    Button mConductor;
    Button mUsuario;
//    preferencia para pasar datos
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConductor=findViewById(R.id.BtnConductor);
        mUsuario=findViewById(R.id.BtnUsuario);

        preferences=getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);
        final SharedPreferences.Editor editor=preferences.edit();


        mUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putString("user","cliente");
                editor.apply();
                irSelectAuth();
            }
        });
        mConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user","conductor");
                editor.apply();
                irSelectAuth();
            }
        });
    }

    private void irSelectAuth() {
        Intent abrirOpcionActivity=new Intent(MainActivity.this,SeletAuthOptionActivity.class);
        startActivity(abrirOpcionActivity);
    }
}