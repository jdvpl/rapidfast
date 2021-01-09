package com.jdrapid.rapidfastDriver.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.ClienteReservaProvider;
import com.jdrapid.rapidfastDriver.providers.ConductoresEncontradosProvider;

public class CancelReceiver extends BroadcastReceiver {
    private ClienteReservaProvider clienteReservaProvider;
    private ConductoresEncontradosProvider conductoresEncontradosProvider;
    private AuthProvider authProvider;
    @Override
    public void onReceive(Context context, Intent intent) {

        String idCliente=intent.getExtras().getString("idCliente");
        clienteReservaProvider=new ClienteReservaProvider();
        conductoresEncontradosProvider=new ConductoresEncontradosProvider();
        authProvider=new AuthProvider();
        String searchById=intent.getExtras().getString("searchById");

        if (searchById.equals("true")){
            clienteReservaProvider.actualizarEstado(idCliente,"Cancelado");
        }

        conductoresEncontradosProvider.Borrar(authProvider.getId());
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
    }
}
