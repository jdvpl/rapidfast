package com.jdrapid.rapidfast.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.channel.NotificationHelper;
import com.jdrapid.rapidfast.receivers.AceptReceiver;
import com.jdrapid.rapidfast.receivers.CancelReceiver;

import java.util.Map;

public class MyFirebaseNotificacionCliente  extends FirebaseMessagingService {
    private static final int NOTIFICACION_CODIGO=100;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        Map<String,String> data=remoteMessage.getData();
        String titulo=data.get("title");
        String body=data.get("body");

        if (titulo !=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if (titulo.contains("SOLICITUD DE SERVICIO")){
                    String idCliente=data.get("idCliente");
                    MostrarNotificacionesOreoAcciones(titulo,body,idCliente);
                }else {
                    MostrarNotificacionesOreo(titulo, body);
                }
            }else {
                if (titulo.contains("SOLICITUD DE SERVICIO")){
                    String idCliente=data.get("idCliente");
                    mostrarNotioficacionAccion(titulo,body,idCliente);
                }else {
                    mostrarNotioficacion(titulo, body);
                }
            }
        }
    }

    private void mostrarNotioficacion(String Titulo, String Contenido) {
        PendingIntent intent=PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder=notificationHelper.getNotificationVersionVieja(Titulo,Contenido,intent,sonido);
        notificationHelper.getManager().notify(1,builder.build());
    }

    private void mostrarNotioficacionAccion(String Titulo, String Contenido,String IdCliente) {
//        aceptar
        Intent AceptarIntet=new Intent(this, AceptReceiver.class);
        AceptarIntet.putExtra("idCliente", IdCliente);
        PendingIntent AceptarpendingIntent=PendingIntent.getBroadcast(this, NOTIFICACION_CODIGO,AceptarIntet,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action aceptarAccion=new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,
                "Aceptar",AceptarpendingIntent).build();
//        cancelar
        Intent cancelarIntet=new Intent(this, CancelReceiver.class);
        cancelarIntet.putExtra("idCliente", IdCliente);
        PendingIntent cancelarPnendin=PendingIntent.getBroadcast(this, NOTIFICACION_CODIGO,cancelarIntet,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action CancelarAccion=new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,
                "Cancelar",cancelarPnendin).build();

        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder=notificationHelper.getNotificationVersionViejaScciones(Titulo,Contenido,sonido,aceptarAccion,CancelarAccion);
        notificationHelper.getManager().notify(2,builder.build());
    }
//version de oreo en adelante
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void MostrarNotificacionesOreo(String Titulo, String Contenidoo) {
        PendingIntent intent=PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        Notification.Builder builder=notificationHelper.getNotification(Titulo,Contenidoo,intent,sonido);
        notificationHelper.getManager().notify(1,builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void MostrarNotificacionesOreoAcciones(String title, String body,String idCliente) {
//        aceptar
        Intent AceptarIntet=new Intent(this, AceptReceiver.class);
        AceptarIntet.putExtra("idCliente", idCliente);
        PendingIntent AceptarpendingIntent=PendingIntent.getBroadcast(this, NOTIFICACION_CODIGO,AceptarIntet,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action aceptarAccion=new Notification.Action.Builder(R.mipmap.ic_launcher,
                "Aceptar",AceptarpendingIntent).build();
//        cancekar
        Intent CancelarIntet=new Intent(this, CancelReceiver.class);
        CancelarIntet.putExtra("idCliente", idCliente);
        PendingIntent CancelarpendingIntent=PendingIntent.getBroadcast(this, NOTIFICACION_CODIGO,CancelarIntet,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action cancelarAccion=new Notification.Action.Builder(R.mipmap.ic_launcher,
                "Cancelar",CancelarpendingIntent).build();

        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        Notification.Builder builder=notificationHelper.getNotificationAcciones(title,body,sonido,aceptarAccion,cancelarAccion);
        notificationHelper.getManager().notify(2,builder.build());
    }
}
