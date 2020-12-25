package com.jdrapid.rapidfastDriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.models.Conductor;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.ConductorProvider;

public class LoginTelefono extends AppCompatActivity {

    String mExtraTelefono;
    EditText TxtCodigo;
    Button BtnCodigoverificacion;
    String mVerificacionId;
    AuthProvider authProvider;
    private ConductorProvider conductorProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_telefono);

        mExtraTelefono=getIntent().getStringExtra("telefono");
        TxtCodigo=findViewById(R.id.txtcodigover);
        BtnCodigoverificacion=findViewById(R.id.BtncondigoVerificacion);

        authProvider=new AuthProvider();
        authProvider.EnviarCodigoverificacion(mExtraTelefono,LoginTelefono.this,mcallbacks);
        conductorProvider=new ConductorProvider();

        BtnCodigoverificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigo=TxtCodigo.getText().toString();
                if (!codigo.equals("") && codigo.length()>=6){
                    IniciarSesion(codigo);
                }else {
                    Toast.makeText(LoginTelefono.this, "Por favor Ingresa el codigo de verificacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            se ejecuta cuando la autenticacion se realiza exitosamente
//            el usuario haya insertado correctamente el codigo de verificacion
//            el telefono coloque el codigo automaticamente

            String codigo=phoneAuthCredential.getSmsCode();
            if (codigo!=null){

                TxtCodigo.setText(codigo);
                IniciarSesion(codigo);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginTelefono.this, "Se produjo un error"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificacionID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            se ejecuuta cuando el codigo de verificacion se envia por sms
            super.onCodeSent(verificacionID, forceResendingToken);
            Toast.makeText(LoginTelefono.this, "Su codigo ha sido enviado", Toast.LENGTH_LONG).show();
            mVerificacionId=verificacionID;
        }
    };
    private void IniciarSesion(String codigo) {
        authProvider.IniciarTelefono(mVerificacionId,codigo).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    conductorProvider.getConductor(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Intent intent=new Intent(LoginTelefono.this,MapConductorActivity.class);
                                startActivity(intent);
                            }else {
                                crearInformacion();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else {
                    Toast.makeText(LoginTelefono.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void crearInformacion() {

        Conductor conductor=new Conductor();
        conductor.setId(authProvider.getId());
        conductor.setTelefono(mExtraTelefono);
        conductorProvider.create(conductor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> taskcreate) {
                if (taskcreate.isSuccessful()){
//                                inicia sesion correctamente
                    Intent intent=new Intent(LoginTelefono.this,RegistroConductorActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(LoginTelefono.this, "No se pudo crear la infromacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}