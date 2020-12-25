package com.jdrapid.rapidfast.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.jdrapid.rapidfast.channel.NotificationHelper;


import java.util.Map;

public class MyFirebaseNotificacionCliente  extends FirebaseMessagingService {
    private static final int NOTIFICACION_CODIGO = 100;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String titulo = data.get("title");
        String body = data.get("body");

        if (titulo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (titulo.contains("VIAJE CANCELADO")) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(2);
                    MostrarNotificacionesOreo(titulo, body);
                } else {
                    MostrarNotificacionesOreo(titulo, body);
                }
            } else {
                if (titulo.contains("VIAJE CANCELADO")) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(2);
                    mostrarNotioficacion(titulo, body);
                } else {
                    mostrarNotioficacion(titulo, body);
                }
            }
        }
    }

    private void mostrarNotioficacion(String Titulo, String Contenido) {
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationVersionVieja(Titulo, Contenido, intent, sonido);
        notificationHelper.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void MostrarNotificacionesOreo(String Titulo, String Contenidoo) {
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotification(Titulo, Contenidoo, intent, sonido);
        notificationHelper.getManager().notify(1, builder.build());
    }
}
