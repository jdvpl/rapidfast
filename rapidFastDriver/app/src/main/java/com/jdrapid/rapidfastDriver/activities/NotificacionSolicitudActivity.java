package com.jdrapid.rapidfastDriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.ClienteReservaProvider;
import com.jdrapid.rapidfastDriver.providers.GeofireProvider;

public class NotificacionSolicitudActivity extends AppCompatActivity {

    private TextView TxtNotificacionOrigen,TxtNotificacionDestino,TxtNotificacionTiempo,TxtNotificacionDistancia,TxtTienCoontador;
    private Button btnAceptarSolicitud,btnCancelarSolicitud;

    private ClienteReservaProvider clienteReservaProvider;
    private GeofireProvider geofireProvider;
    private AuthProvider authProvider;
    private String mExtraClienteId,mExtraOrigen,MextraDestino,MextraDistacia,mExtraTiempo;

    private MediaPlayer mediaPlayer;
    private int contador=25;
    private Handler handler;
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            contador-=1;
            TxtTienCoontador.setText(String.valueOf(contador));
            if (contador>0){

                iniciarTiempo();
            }else {
                CancelarViaje();
            }
        }
    };
    private ValueEventListener listener;

    private void iniciarTiempo() {
        handler=new Handler();
        handler.postDelayed(runnable,1000);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_solicitud);
        TxtNotificacionOrigen=findViewById(R.id.OrigenNotificacion);
        TxtNotificacionDestino=findViewById(R.id.DestinoNotificacion);
        TxtNotificacionTiempo=findViewById(R.id.notificacionTiempo);
        TxtNotificacionDistancia=findViewById(R.id.notificacionDistancia);
        TxtTienCoontador=findViewById(R.id.contadoTiempo);

        btnAceptarSolicitud=findViewById(R.id.btnAceptarSolicitud);
        btnCancelarSolicitud=findViewById(R.id.btnCancelarSolicitud);
        mExtraClienteId=getIntent().getStringExtra("idCliente");

        mExtraOrigen=getIntent().getStringExtra("origen");
        MextraDestino=getIntent().getStringExtra("destino");
        mExtraTiempo=getIntent().getStringExtra("tiempo");
        MextraDistacia=getIntent().getStringExtra("distancia");
        mediaPlayer=MediaPlayer.create(this,R.raw.taxi);
        mediaPlayer.setLooping(true);
        clienteReservaProvider=new ClienteReservaProvider();

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
        iniciarTiempo();
        ValidarsiCanceloCliente();
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
    private void ValidarsiCanceloCliente(){
        listener=clienteReservaProvider.getClienteSolicitud(mExtraClienteId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Toast.makeText(NotificacionSolicitudActivity.this, "El cliente cancelo el viaje", Toast.LENGTH_LONG).show();
                    if (handler!=null)handler.removeCallbacks(runnable);
                    Intent intent=new Intent(NotificacionSolicitudActivity.this,MapConductorActivity.class);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CancelarViaje() {
        if (handler!=null)handler.removeCallbacks(runnable);

        clienteReservaProvider.actualizarEstado(mExtraClienteId,"Cancelado");

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        Intent intent=new Intent(NotificacionSolicitudActivity.this,MapConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void aceptarSolicitud() {
        if (handler!=null)handler.removeCallbacks(runnable);
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer!=null){
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler!=null)handler.removeCallbacks(runnable);

        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }else if (listener!=null){
            clienteReservaProvider.getClienteSolicitud(mExtraClienteId).removeEventListener(listener);

        }
    }
}