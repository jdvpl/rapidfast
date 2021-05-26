package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.Cliente;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;
import com.jdrapid.rapidfast.providers.ImageProvider;
import com.jdrapid.rapidfast.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActualizarPerfilActivity extends AppCompatActivity {

    private CircleImageView FotoPerfil;
    private Button BtnActualizar;
    private TextView TextNomvre;
    private ClienteProvider clienteProvider;
    private AuthProvider authProvider;
    private ImageProvider imageProvider;
    private File mImageFile;
    private String UrlImage;
    private final int CODIGO_GALERIA=1;
    private ProgressDialog progressDialog;
    private String NombreCompleto,Imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_perfil);

        ToolBar.mostrar(this,"Mi perfil",true);
        FotoPerfil=findViewById(R.id.fotoperfil);
        TextNomvre=findViewById(R.id.txtNombre);
        BtnActualizar=findViewById(R.id.btnActualizar);

        clienteProvider=new ClienteProvider();
        authProvider=new AuthProvider();
        imageProvider=new ImageProvider("Cliente_imagenes");
        progressDialog=new ProgressDialog(this);

        FotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirGaleria();
            }
        });

        BtnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarPerfil();
            }
        });
        obtenerInformacionCliente();
    }

    private void AbrirGaleria() {
        Intent intentGaleria=new Intent(Intent.ACTION_GET_CONTENT);
        intentGaleria.setType("image/*");
        startActivityForResult(intentGaleria,CODIGO_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CODIGO_GALERIA && resultCode==RESULT_OK){
            try {
                mImageFile= FileUtil.from(this,data.getData());
                FotoPerfil.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("Error", "Mensaje: "+e.getMessage());
            }
        }
    }
    private void obtenerInformacionCliente(){
        clienteProvider.getCliente(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String imagen="";
                    if (snapshot.hasChild("imagen")){
                        imagen=snapshot.child("imagen").getValue().toString();
                        Imagen=imagen;
                        Picasso.with(ActualizarPerfilActivity.this).load(imagen).into(FotoPerfil);
                    }
                    String nombre=snapshot.child("Nombre").getValue().toString();
                    TextNomvre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void actualizarPerfil() {
        NombreCompleto = TextNomvre.getText().toString();
        if (!NombreCompleto.equals("") && mImageFile !=null){
            progressDialog.setMessage("Espere un momento por favor...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            guardarImagen();
        }else if (mImageFile==null){
            progressDialog.show();
            Cliente cliente = new Cliente();
            cliente.setNombre(NombreCompleto);
            cliente.setImagen(Imagen);
            cliente.setId(authProvider.getId());
            clienteProvider.actualizar(cliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(ActualizarPerfilActivity.this, "Su informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void guardarImagen() {
      imageProvider.guardarImagen(ActualizarPerfilActivity.this,mImageFile,authProvider.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imagen=uri.toString();
                            Cliente cliente=new Cliente();
                            cliente.setImagen(imagen);
                            cliente.setNombre(NombreCompleto);
                            cliente.setCorreo(cliente.getCorreo());
                            cliente.setId(authProvider.getId());
                            clienteProvider.actualizar(cliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ActualizarPerfilActivity.this, "Su informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else {
                    Toast.makeText(ActualizarPerfilActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}