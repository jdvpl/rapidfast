package com.jdrapid.rapidfastDriver.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;


public class MainActivity extends AppCompatActivity {
    Button BtnEnviarCodigo;
    CountryCodePicker codePicker;
    EditText TxtTelefono;
    AuthProvider authProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashScreen);
        setContentView(R.layout.main_activity);
        authProvider=new AuthProvider();


        codePicker=findViewById(R.id.codigoPais);
        TxtTelefono=findViewById(R.id.txtTelefono);
;
        BtnEnviarCodigo=findViewById(R.id.BtnEnviarCodigo);

       BtnEnviarCodigo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               IraLoginconTelefono();
           }
       });



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProvider.existeSesion()){
            Intent intent=new Intent(MainActivity.this, MapConductorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
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
}