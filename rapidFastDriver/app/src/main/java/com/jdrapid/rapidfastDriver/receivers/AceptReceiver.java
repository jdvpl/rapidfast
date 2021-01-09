package com.jdrapid.rapidfastDriver.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.activities.MapConductorActivity;
import com.jdrapid.rapidfastDriver.activities.MapConductorSolicitud;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.ClienteReservaProvider;
import com.jdrapid.rapidfastDriver.providers.GeofireProvider;

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
        String searchById=intent.getExtras().getString("searchById");
        clienteReservaProvider=new ClienteReservaProvider();

        if (searchById.equals("true")){
            clienteReservaProvider.actualizarEstado(idCliente,"Aceptado");
            Intent intent1=new Intent(context, MapConductorSolicitud.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.setAction(Intent.ACTION_RUN);
            intent1.putExtra("idCliente",idCliente);
            context.startActivity(intent1);
        }else {
            validarsiClientesolicitudAceptado(idCliente,context);
        }



        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);


    }

    private void validarsiClientesolicitudAceptado(String idCliente,Context context) {
        clienteReservaProvider.getClienteSolicitud(idCliente).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild("idConductor") && snapshot.hasChild("estado")){
                        String estado=snapshot.child("estado").getValue().toString();
                        String idconductor=snapshot.child("idConductor").getValue().toString();
                        if (estado.equals("Creado") && idconductor.equals("")){
                            clienteReservaProvider.actualizarEstadoandIdDriver(idCliente,"Aceptado",authProvider.getId());

                            Intent intent1=new Intent(context, MapConductorSolicitud.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent1.setAction(Intent.ACTION_RUN);
                            intent1.putExtra("idCliente",idCliente);
                            context.startActivity(intent1);
                        }else {
                           iraMapConductorActivity(context);
                        }
                    }else {
                        iraMapConductorActivity(context);

                    }
                }else {
                    iraMapConductorActivity(context);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void iraMapConductorActivity(Context context) {
        Toast.makeText(context, "Otro conductor ya acepto el viaje", Toast.LENGTH_SHORT).show();
        Intent intent1=new Intent(context, MapConductorActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        context.startActivity(intent1);
    }
}
