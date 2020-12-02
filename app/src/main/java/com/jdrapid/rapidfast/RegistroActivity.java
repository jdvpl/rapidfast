package com.jdrapid.rapidfast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
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
import com.jdrapid.rapidfast.models.Usuario;

import static com.jdrapid.rapidfast.R.string.CamposVacios;
import static com.jdrapid.rapidfast.R.string.ContrasenasNoCoinciden;
import static com.jdrapid.rapidfast.R.string.seiscaracteres;

public class RegistroActivity extends AppCompatActivity {
    Toolbar toolbar;
    //    preferencia para pasar datos
    SharedPreferences preferences;
    FirebaseAuth auth;
    DatabaseReference reference;
    Button btnRegistro;
    TextInputEditText tnombre,tcorreo,tontrase,trepetir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
//        toolbar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.select_toolbar_opcion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        instancia de firebase
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
//        para obtener si es usuario o conductor
        preferences=getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);



        tnombre=findViewById(R.id.InNombre);
        tcorreo=findViewById(R.id.InEmail);
        tontrase=findViewById(R.id.InContrasena);
        trepetir=findViewById(R.id.RepetirContra);

        btnRegistro=findViewById(R.id.BtnRegistrar);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarUsuario();
            }
        });
    }

    private void RegistrarUsuario() {
        final String nombre=tnombre.getText().toString();
        final  String correo=tcorreo.getText().toString();
        final String contrasena=tontrase.getText().toString();
         final String repetir=trepetir.getText().toString();

        if (!nombre.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty() && !repetir.isEmpty()){
            if(contrasena.length()>=6){
                if(contrasena!=repetir){
                    auth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                guardarUsuario(nombre,correo);
                            }else{
                                Toast.makeText(RegistroActivity.this,"No se pudo registrar el usuario",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(this, ContrasenasNoCoinciden,Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, seiscaracteres,Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, CamposVacios,Toast.LENGTH_LONG).show();
        }

    }

    private void guardarUsuario(String nombre,String correo) {
        String eleccion=preferences.getString("user","");
        Usuario usuario=new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);

        if (eleccion.equals("conductor")){
            reference.child("Usuarios").child("Conductores").push().setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistroActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(RegistroActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }else if (eleccion.equals("cliente")){
            reference.child("Usuarios").child("Clientes").push().setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistroActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(RegistroActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}