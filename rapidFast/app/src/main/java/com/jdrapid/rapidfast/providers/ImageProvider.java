package com.jdrapid.rapidfast.providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jdrapid.rapidfast.utils.CompressorBitmapImage;

import java.io.File;

public class ImageProvider {
    private StorageReference storageReference;

    public ImageProvider(String ref) {
        storageReference= FirebaseStorage.getInstance().getReference().child(ref);
    }
    public UploadTask guardarImagen(Context context, File imagen, String IdUsuario){
        byte[] imagenByte = CompressorBitmapImage.getImage(context, imagen.getPath(), 500, 500);
        StorageReference storage = storageReference.child(IdUsuario + ".jpg");
        storageReference=storage;
        UploadTask uploadTask = storage.putBytes(imagenByte);
        return uploadTask;
    }

    public StorageReference getStorage(){
        return  storageReference;
    }
}
