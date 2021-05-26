package com.jdrapid.rapidfast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;


public class MainActivity extends AppCompatActivity {
    Button BtnRegistro,BtnEnviarCodigo;
    TextView BtnLogin;
    CountryCodePicker codePicker;
    EditText TxtTelefono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashScreen);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ctivity);

        codePicker=findViewById(R.id.codigoPais);
        TxtTelefono=findViewById(R.id.txtTelefono);

        BtnEnviarCodigo=findViewById(R.id.BtnEnviarCodigo);
        BtnLogin=findViewById(R.id.BtnIniciarSesion);
        BtnRegistro=findViewById(R.id.BtnRegistro);

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ss=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(ss);
            }
        });
        BtnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ss=new Intent(MainActivity.this,RegistroClienteActivity.class);
                startActivity(ss);
            }
        });
        BtnEnviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IraLoginconTelefono();
            }
        });

    }

    private void IraLoginconTelefono() {
        String codigo=codePicker.getSelectedCountryCodeWithPlus();
        String telefono=TxtTelefono.getText().toString();

        if (!telefono.equals("")) {
            Intent intenttel = new Intent(MainActivity.this, LoginTelefono.class);
            intenttel.putExtra("telefono",codigo+telefono);
            startActivity(intenttel);
        }else {
            Toast.makeText(this, "Por favor Ingresa un numero de telefono", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent intent=new Intent(MainActivity.this, MapClienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
    }


}