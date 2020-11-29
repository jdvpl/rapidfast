package com.jdrapid.rapidfast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class SeletAuthOptionActivity extends AppCompatActivity {
    Button BtnLogin;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selet_auth_option);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.select_toolbar_opcion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BtnLogin=findViewById(R.id.BtnLogin);
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iraLogin();
            }
        });
    }

    private void iraLogin() {
        Intent intent=new Intent(SeletAuthOptionActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}