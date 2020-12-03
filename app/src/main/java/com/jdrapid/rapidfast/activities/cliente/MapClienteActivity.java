package com.jdrapid.rapidfast.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.MainActivity;
import com.jdrapid.rapidfast.providers.AuthProvider;

public class MapClienteActivity extends AppCompatActivity {
    Button btnlogout;
    AuthProvider authProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cliente);
        authProvider=new AuthProvider();
        btnlogout=findViewById(R.id.btnlogout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProvider.logout();
                Intent intent=new Intent(MapClienteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}