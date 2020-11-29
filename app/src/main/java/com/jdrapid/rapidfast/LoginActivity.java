package com.jdrapid.rapidfast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText InEmail,InContrasena;
    Button BtnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InEmail=findViewById(R.id.txtInputCorrero);
        InContrasena=findViewById(R.id.txtInputContrasena);
        BtnLogin=findViewById(R.id.BtnLogin);
        
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

    }

    private void Login() {
        String correo=InEmail.getText().toString();
        String contrasena=InContrasena.getText().toString();
        if (!correo.isEmpty() && !contrasena.isEmpty()){
            if (contrasena.length()>=6){

            }

        }
    }
}