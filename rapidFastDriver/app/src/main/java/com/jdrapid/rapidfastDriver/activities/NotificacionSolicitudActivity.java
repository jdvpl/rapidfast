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
import com.jdrapid.rapidfastDriver.providers.ConductoresEncontradosProvider;
import com.jdrapid.rapidfastDriver.providers.GeofireProvider;

public class NotificacionSolicitudActivity extends AppCompatActivity {

    private TextView TxtNotificacionOrigen,TxtNotificacionDestino,TxtNotificacionTiempo,TxtNotificacionDistancia,TxtTienCoontador,TxtPrecio;
    private Button btnAceptarSolicitud,btnCancelarSolicitud;

    private ClienteReservaProvider clienteReservaProvider;
    private ConductoresEncontradosProvider conductoresEncontradosProvider;
    private GeofireProvider geofireProvider;
    private AuthProvider authProvider;
    private String mExtraClienteId,mExtraOrigen,MextraDestino,MextraDistacia,mExtraTiempo,mExtrasearchById,mExtraPrecio;

    private MediaPlayer mediaPlayer;
    private int contador=30;
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
        TxtPrecio=findViewById(R.id.notificacionPrecio);

        btnAceptarSolicitud=findViewById(R.id.btnAceptarSolicitud);
        btnCancelarSolicitud=findViewById(R.id.btnCancelarSolicitud);
        mExtraClienteId=getIntent().getStringExtra("idCliente");

        mExtraOrigen=getIntent().getStringExtra("origen");
        MextraDestino=getIntent().getStringExtra("destino");
        mExtraTiempo=getIntent().getStringExtra("tiempo");
        MextraDistacia=getIntent().getStringExtra("distancia");
        mExtraPrecio=getIntent().getStringExtra("precio");
        mediaPlayer=MediaPlayer.create(this,R.raw.taxi);
        mediaPlayer.setLooping(true);
        clienteReservaProvider=new ClienteReservaProvider();
        mExtrasearchById=getIntent().getStringExtra("searchById");

        authProvider=new AuthProvider();
        conductoresEncontradosProvider=new ConductoresEncontradosProvider();

//        asignarle los valores
        TxtNotificacionOrigen.setText(mExtraOrigen);
        TxtNotificacionDestino.setText(MextraDestino);
        TxtPrecio.setText("$"+mExtraPrecio);
        ObtenerInfo();


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

    private void ObtenerInfo(){
        clienteReservaProvider.getClienteSolicitud(mExtraClienteId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String Km=snapshot.child("distanciaKm").getValue().toString();
                    String time=snapshot.child("tiempo").getValue().toString();

                    TxtNotificacionTiempo.setText(time);
                    TxtNotificacionDistancia.setText(Km);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ValidarsiCanceloCliente(){
        listener=clienteReservaProvider.getClienteSolicitud(mExtraClienteId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    iraMapConductorActividad();

                }
//                significa que el la solicitud existe
                else if (snapshot.hasChild("idConductor") && snapshot.hasChild("estado")){

                    String idConductor=snapshot.child("idConductor").getValue().toString();
                    String estado=snapshot.child("estado").getValue().toString();
                    if ((estado.equals("Aceptado") || estado.equals("Cancelado")) && !idConductor.equals(authProvider.getId())){
                        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(2);
                        iraMapConductorActividad();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void iraMapConductorActividad(){
        Toast.makeText(NotificacionSolicitudActivity.this, "El cliente ya no esta disponible", Toast.LENGTH_LONG).show();
        if (handler!=null)handler.removeCallbacks(runnable);
        Intent intent=new Intent(NotificacionSolicitudActivity.this,MapConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void CancelarViaje() {
        if (handler!=null)handler.removeCallbacks(runnable);

        if (mExtrasearchById.equals("true")){
            clienteReservaProvider.actualizarEstado(mExtraClienteId,"Cancelado");
        }

        conductoresEncontradosProvider.Borrar(authProvider.getId());

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        Intent intent=new Intent(NotificacionSolicitudActivity.this,MapConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void aceptarSolicitud() {
        if (handler!=null)handler.removeCallbacks(runnable);
        geofireProvider=new GeofireProvider("Conductores_Activos");
        geofireProvider.EliminarUbicacion(authProvider.getId());

        clienteReservaProvider=new ClienteReservaProvider();


        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);

        if (mExtrasearchById.equals("true")){
            clienteReservaProvider.actualizarEstado(mExtraClienteId,"Aceptado");

            Intent intent1=new Intent(NotificacionSolicitudActivity.this, MapConductorSolicitud.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.setAction(Intent.ACTION_RUN);
            intent1.putExtra("idCliente",mExtraClienteId);
            intent1.putExtra("searchById",mExtrasearchById);
            intent1.putExtra("precio",mExtraPrecio);
            startActivity(intent1);
        }else {
            validarsiClientesolicitudAceptado(mExtraClienteId,NotificacionSolicitudActivity.this);
        }

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