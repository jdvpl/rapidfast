package com.jdrapid.rapidfastDriver.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.activities.NotificacionSolicitudActivity;
import com.jdrapid.rapidfastDriver.channel.NotificationHelper;
import com.jdrapid.rapidfastDriver.receivers.AceptReceiver;
import com.jdrapid.rapidfastDriver.receivers.CancelReceiver;

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
                    String origen=data.get("origen");
                    String destino=data.get("destino");
                    String tiempo=data.get("tiempo");
                    String distancia=data.get("distancia");
                    String searchById=data.get("searchById");
                    String precio=data.get("precio");
                    MostrarNotificacionesOreoAcciones(titulo,body,idCliente,searchById);
                    MostarNotificacionaActivty(idCliente,origen,destino,tiempo,distancia,searchById,precio);
                }else  if (titulo.contains("VIAJE CANCELADO")){
                    NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    eliminando la notificacion de solicitud de viaje
                    notificationManager.cancel(2);
                   // MostrarNotificacionesOreo(titulo, body);
                }
                else {
                    MostrarNotificacionesOreo(titulo, body);
                }
            }else {
                if (titulo.contains("SOLICITUD DE SERVICIO")){
                    String idCliente=data.get("idCliente");
                    String origen=data.get("origen");
                    String destino=data.get("destino");
                    String tiempo=data.get("tiempo");
                    String distancia=data.get("distancia");
                    String searchById=data.get("searchById");
                    String precio=data.get("precio");
                    mostrarNotioficacionAccion(titulo,body,idCliente,searchById);
                    MostarNotificacionaActivty(idCliente,origen,destino,tiempo,distancia,searchById,precio);
                }else  if (titulo.contains("VIAJE CANCELADO")){
                    NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(2);
                   // mostrarNotioficacion(titulo, body);
                }
                else {
                    mostrarNotioficacion(titulo, body);
                }
            }
        }
    }

    private void MostarNotificacionaActivty(String idCliente, String origen, String destino, String tiempo, String distancia,String searchById,String precio) {

        PowerManager powerManager=(PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        boolean estencendiada=powerManager.isScreenOn();
        if (!estencendiada){
            PowerManager.WakeLock wakeLock=powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE,
                            "AppName:MyLock"

            );
            wakeLock.acquire(10000);

        }
        Intent intent=new Intent(getBaseContext(), NotificacionSolicitudActivity.class);
        intent.putExtra("idCliente",idCliente);
        intent.putExtra("origen",origen);
        intent.putExtra("destino",destino);
        intent.putExtra("tiempo",tiempo);
        intent.putExtra("distancia",distancia);
        intent.putExtra("searchById",searchById);
        intent.putExtra("precio",precio);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void mostrarNotioficacion(String Titulo, String Contenido) {
        PendingIntent intent=PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder=notificationHelper.getNotificationVersionVieja(Titulo,Contenido,intent,sonido);
        notificationHelper.getManager().notify(1,builder.build());
    }

    private void mostrarNotioficacionAccion(String Titulo, String Contenido,String IdCliente,String searchById) {
//        aceptar
        Intent AceptarIntet=new Intent(this, AceptReceiver.class);
        AceptarIntet.putExtra("idCliente", IdCliente);
        AceptarIntet.putExtra("searchById", searchById);
        PendingIntent AceptarpendingIntent=PendingIntent.getBroadcast(this, NOTIFICACION_CODIGO,AceptarIntet,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action aceptarAccion=new NotificationCompat.Action.Builder(R.mipmap.ic_launcher,
                "Aceptar",AceptarpendingIntent).build();
//        cancelar
        Intent cancelarIntet=new Intent(this, CancelReceiver.class);
        cancelarIntet.putExtra("idCliente", IdCliente);
        cancelarIntet.putExtra("searchById",searchById);

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
    private void MostrarNotificacionesOreoAcciones(String title, String body,String idCliente,String searchById) {
//        aceptar
        Intent AceptarIntet=new Intent(this, AceptReceiver.class);
        AceptarIntet.putExtra("idCliente", idCliente);
        AceptarIntet.putExtra("searchById", searchById);
        PendingIntent AceptarpendingIntent=PendingIntent.getBroadcast(this, NOTIFICACION_CODIGO,AceptarIntet,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action aceptarAccion=new Notification.Action.Builder(R.mipmap.ic_launcher,
                "Aceptar",AceptarpendingIntent).build();
//        cancekar
        Intent CancelarIntet=new Intent(this, CancelReceiver.class);
        CancelarIntet.putExtra("idCliente", idCliente);
        CancelarIntet.putExtra("searchById",searchById);

        PendingIntent CancelarpendingIntent=PendingIntent.getBroadcast(this, NOTIFICACION_CODIGO,CancelarIntet,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action cancelarAccion=new Notification.Action.Builder(R.mipmap.ic_launcher,
                "Cancelar",CancelarpendingIntent).build();

        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        Notification.Builder builder=notificationHelper.getNotificationAcciones(title,body,sonido,aceptarAccion,cancelarAccion);
        notificationHelper.getManager().notify(2,builder.build());
    }
}
