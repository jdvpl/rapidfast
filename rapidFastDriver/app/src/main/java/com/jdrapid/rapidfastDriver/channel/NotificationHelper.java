package com.jdrapid.rapidfastDriver.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.jdrapid.rapidfastDriver.R;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANEL_ID="com.jdrapid.rapidfastDriver";
    private static final String CHANEL_NOMBRE="RapidFast";

    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CreateChannels(){
        NotificationChannel notificationChannel=new NotificationChannel(CHANEL_ID,CHANEL_NOMBRE,NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }
    public NotificationManager getManager(){
        if (manager==null){
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body, PendingIntent intent, Uri sonidoUri){
        return new Notification.Builder(getApplicationContext(),CHANEL_ID).setContentTitle(title).
                setContentText(body).setAutoCancel(true).setSound(sonidoUri).setContentIntent(intent).setSmallIcon(R.drawable.logo).
                setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title));

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotificationAcciones(String title, String body, Uri sonidoUri,Notification.Action aceptar,Notification.Action cancelarAccion){
        return new Notification.Builder(getApplicationContext(),CHANEL_ID).setContentTitle(title).
                setContentText(body).setAutoCancel(true).setSound(sonidoUri).setSmallIcon(R.drawable.logo).
                addAction(aceptar).
                addAction(cancelarAccion).
                setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title));

    }
//    para menores a android 8

    public NotificationCompat.Builder getNotificationVersionVieja(String Titulo, String Contenido, PendingIntent intent, Uri sonidoUri){
    return new NotificationCompat.Builder(getApplicationContext(),CHANEL_ID).setContentTitle(Titulo).
            setContentText(Contenido).setAutoCancel(true).setSound(sonidoUri).setContentIntent(intent).setSmallIcon(R.drawable.logo)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(Contenido).setBigContentTitle(Titulo));

}

    public NotificationCompat.Builder getNotificationVersionViejaScciones(String Titulo, String Contenido,  Uri sonidoUri,NotificationCompat.Action aceptar,NotificationCompat.Action cancelarAScion){
        return new NotificationCompat.Builder(getApplicationContext(),CHANEL_ID).setContentTitle(Titulo).
                setContentText(Contenido).setAutoCancel(true).setSound(sonidoUri).setSmallIcon(R.drawable.logo)
                .addAction(aceptar).addAction(cancelarAScion)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Contenido).setBigContentTitle(Titulo));

    }
}
