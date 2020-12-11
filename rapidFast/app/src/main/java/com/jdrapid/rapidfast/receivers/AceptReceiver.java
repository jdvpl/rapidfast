package com.jdrapid.rapidfast.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jdrapid.rapidfast.activities.conductor.MapConductorSolicitud;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;

public class AceptReceiver  extends BroadcastReceiver {

    private ClienteReservaProvider clienteReservaProvider;
    private GeofireProvider geofireProvider;
    private AuthProvider authProvider;
    @Override
    public void onReceive(Context context, Intent intent) {
        authProvider=new AuthProvider();
        geofireProvider=new GeofireProvider("Conductores_Activos");
        geofireProvider.EliminarUbicacion(authProvider.getId());
        String idCliente=intent.getExtras().getString("idCliente");
        clienteReservaProvider=new ClienteReservaProvider();
        clienteReservaProvider.actualizarEstado(idCliente,"Aceptado");

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);

        Intent intent1=new Intent(context, MapConductorSolicitud.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idCliente",idCliente);
        context.startActivity(intent1);
    }
}
