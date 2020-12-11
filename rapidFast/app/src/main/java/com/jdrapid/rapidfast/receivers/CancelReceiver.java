package com.jdrapid.rapidfast.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jdrapid.rapidfast.providers.ClienteReservaProvider;

public class CancelReceiver extends BroadcastReceiver {
    private ClienteReservaProvider clienteReservaProvider;
    @Override
    public void onReceive(Context context, Intent intent) {

        String idCliente=intent.getExtras().getString("idCliente");
        clienteReservaProvider=new ClienteReservaProvider();
        clienteReservaProvider.actualizarEstado(idCliente,"Cancelado");

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
    }
}
