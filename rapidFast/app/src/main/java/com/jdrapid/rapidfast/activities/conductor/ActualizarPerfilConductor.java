package com.jdrapid.rapidfast.activities.conductor;

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
import com.jdrapid.rapidfast.activities.cliente.ActualizarPerfilActivity;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.Cliente;
import com.jdrapid.rapidfast.models.Conductor;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.ImageProvider;
import com.jdrapid.rapidfast.utils.CompressorBitmapImage;
import com.jdrapid.rapidfast.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActualizarPerfilConductor extends AppCompatActivity {

    private CircleImageView FotoPerfil;
    private Button BtnActualizar;
    private TextView TextNomvre,Marca,Placa;
    private ConductorProvider conductorProvider;
    private AuthProvider authProvider;
    private File mImageFile;
    private String UrlImage;
    private final int CODIGO_GALERIA = 1;
    private ProgressDialog progressDialog;
    private String NombreCompleto,MarcaVehiculo,PlacaVehiculo;
    private ImageProvider imageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_perfil_conductor);

        ToolBar.mostrar(this, "Editar Perfil", true);
        FotoPerfil = findViewById(R.id.fotoperfil);
        TextNomvre = findViewById(R.id.txtNombre);
        Marca = findViewById(R.id.TxtInMarca);
        Placa = findViewById(R.id.TxtInPlaca);
        BtnActualizar = findViewById(R.id.btnActualizar);

        conductorProvider = new ConductorProvider();
        authProvider = new AuthProvider();
        progressDialog = new ProgressDialog(this);
        imageProvider=new ImageProvider("Conductor_Imagenes");

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
        obtenerInformacionConductor();

    }

    private void AbrirGaleria() {
        Intent intentGaleria = new Intent(Intent.ACTION_GET_CONTENT);
        intentGaleria.setType("image/*");
        startActivityForResult(intentGaleria, CODIGO_GALERIA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_GALERIA && resultCode == RESULT_OK) {
            try {
                mImageFile = FileUtil.from(this, data.getData());
                FotoPerfil.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

            } catch (Exception e) {
                Log.d("Error", "Mensaje: " + e.getMessage());
            }
        }
    }

    private void obtenerInformacionConductor() {
        conductorProvider.getConductor(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imagen="";
                    if (snapshot.hasChild("imagen")){
                        imagen=snapshot.child("imagen").getValue().toString();
                        Picasso.with(ActualizarPerfilConductor.this).load(imagen).into(FotoPerfil);
                    }
                    String nombre = snapshot.child("nombre").getValue().toString();
                    String marcaVehiculo = snapshot.child("marcaVehiculo").getValue().toString();
                    String placaVehiculo = snapshot.child("placaVehiculo").getValue().toString();
                    TextNomvre.setText(nombre);
                    Marca.setText(marcaVehiculo);
                    Placa.setText(placaVehiculo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void actualizarPerfil() {
        MarcaVehiculo=Marca.getText().toString();
        PlacaVehiculo=Placa.getText().toString();
        NombreCompleto = TextNomvre.getText().toString();
        if (!NombreCompleto.equals("") && mImageFile != null) {
            progressDialog.setMessage("Espere un momento por favor...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            guardarImagen();
        } else {
            Toast.makeText(this, "Ingresa la Imagen y el Nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarImagen() {
       imageProvider.guardarImagen(ActualizarPerfilConductor.this,mImageFile,authProvider.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imagen = uri.toString();
                            Conductor conductor = new Conductor();
                            conductor.setImagen(imagen);
                            conductor.setNombre(NombreCompleto);
                            conductor.setMarcaVehiculo(MarcaVehiculo);
                            conductor.setPlacaVehiculo(PlacaVehiculo);
                            conductor.setId(authProvider.getId());
                            conductorProvider.actualizar(conductor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ActualizarPerfilConductor.this, "Su informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(ActualizarPerfilConductor.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}