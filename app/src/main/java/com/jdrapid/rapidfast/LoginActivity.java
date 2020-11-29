package com.jdrapid.rapidfast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//strings
import static com.jdrapid.rapidfast.R.string.EmailContaseñaobligatorio;
import static com.jdrapid.rapidfast.R.string.LoginActivityemailYpassworsIncorectos;
import static com.jdrapid.rapidfast.R.string.passwordseis;

public class LoginActivity extends AppCompatActivity {
//    variable para forebase
    FirebaseAuth auth;
    DatabaseReference mDatabase;
//    viarable del activyti
    TextInputEditText InEmail,InContrasena;
    Button BtnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InEmail=findViewById(R.id.txtInputCorrero);
        InContrasena=findViewById(R.id.txtInputContrasena);
        BtnLogin=findViewById(R.id.BtnLogin);

//        hacer instancia
        auth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        
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
                auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Login Exitoso",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(LoginActivity.this, LoginActivityemailYpassworsIncorectos,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(LoginActivity.this, passwordseis,Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(LoginActivity.this, EmailContaseñaobligatorio,Toast.LENGTH_LONG).show();
        }
    }
}