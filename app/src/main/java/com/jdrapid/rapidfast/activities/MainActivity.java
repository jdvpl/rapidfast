package com.jdrapid.rapidfast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.cliente.MapClienteActivity;
import com.jdrapid.rapidfast.activities.conductor.MapConductorActivity;

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

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String user=preferences.getString("user","");
            if (user.equals("cliente")){
                Intent intent=new Intent(MainActivity.this, MapClienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                Intent intent=new Intent(MainActivity.this, MapConductorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        }
    }

    private void irSelectAuth() {
        Intent abrirOpcionActivity=new Intent(MainActivity.this,SeletAuthOptionActivity.class);
        startActivity(abrirOpcionActivity);
    }
}