package com.jdrapid.rapidfast.activities.cliente;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;
import com.jdrapid.rapidfast.utils.CompressorBitmapImage;
import com.jdrapid.rapidfast.utils.FileUtil;

import java.io.File;

public class ActualizarPerfilActivity extends AppCompatActivity {

    private ImageView FotoPerfil;
    private Button BtnActualizar;
    private TextView TextNomvre;
    private ClienteProvider clienteProvider;
    private AuthProvider authProvider;
    private File mImageFile;
    private String UrlImage;
    private final int CODIGO_GALERIA=1;
    private ProgressDialog progressDialog;
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
                    String nombre=snapshot.child("Nombre").getValue().toString();
                    TextNomvre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void actualizarPerfil() {
        String Nombre=TextNomvre.getText().toString();
        if (!Nombre.equals("") && mImageFile !=null){
            progressDialog.setMessage("Espere un momento por favor...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            guardarImagen();
        }else {
            Toast.makeText(this, "Ingresa la Imagen y el Nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarImagen() {
        byte [] imagenByte= CompressorBitmapImage.getImage(this,mImageFile.getPath(),500,500);
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("CLiente_Imagenes").child(authProvider.getId()+".jpg");
        UploadTask uploadTask=storageReference.putBytes(imagenByte);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imagen=uri.toString();

                        }
                    });
                }else {
                    Toast.makeText(ActualizarPerfilActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}