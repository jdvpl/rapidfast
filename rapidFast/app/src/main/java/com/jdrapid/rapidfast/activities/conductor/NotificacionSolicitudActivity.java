package com.jdrapid.rapidfast.activities.conductor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;

public class NotificacionSolicitudActivity extends AppCompatActivity {

    private TextView TxtNotificacionOrigen,TxtNotificacionDestino,TxtNotificacionTiempo,TxtNotificacionDistancia;
    private Button btnAceptarSolicitud,btnCancelarSolicitud;

    private ClienteReservaProvider clienteReservaProvider;
    private GeofireProvider geofireProvider;
    private AuthProvider authProvider;
    private String mExtraClienteId,mExtraOrigen,MextraDestino,MextraDistacia,mExtraTiempo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_solicitud);
        TxtNotificacionOrigen=findViewById(R.id.OrigenNotificacion);
        TxtNotificacionDestino=findViewById(R.id.DestinoNotificacion);
        TxtNotificacionTiempo=findViewById(R.id.notificacionTiempo);
        TxtNotificacionDistancia=findViewById(R.id.notificacionDistancia);

        btnAceptarSolicitud=findViewById(R.id.btnAceptarSolicitud);
        btnCancelarSolicitud=findViewById(R.id.btnCancelarSolicitud);
        mExtraClienteId=getIntent().getStringExtra("idCliente");

        mExtraOrigen=getIntent().getStringExtra("origen");
        MextraDestino=getIntent().getStringExtra("destino");
        mExtraTiempo=getIntent().getStringExtra("tiempo");
        MextraDistacia=getIntent().getStringExtra("distancia");

//        asignarle los valores
        TxtNotificacionOrigen.setText(mExtraOrigen);
        TxtNotificacionDestino.setText(MextraDestino);
        TxtNotificacionTiempo.setText(mExtraTiempo);
        TxtNotificacionDistancia.setText(MextraDistacia);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON

        );

        btnAceptarSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceptarSolicitud();
            }
        });

        btnCancelarSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelarViaje();
            }
        });

    }

    private void CancelarViaje() {

        clienteReservaProvider=new ClienteReservaProvider();
        clienteReservaProvider.actualizarEstado(mExtraClienteId,"Cancelado");

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        Intent intent=new Intent(NotificacionSolicitudActivity.this,MapConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void aceptarSolicitud() {
        authProvider=new AuthProvider();
        geofireProvider=new GeofireProvider("Conductores_Activos");
        geofireProvider.EliminarUbicacion(authProvider.getId());

        clienteReservaProvider=new ClienteReservaProvider();
        clienteReservaProvider.actualizarEstado(mExtraClienteId,"Aceptado");

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);

        Intent intent1=new Intent(NotificacionSolicitudActivity.this, MapConductorSolicitud.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idCliente",mExtraClienteId);
        startActivity(intent1);
    }
}