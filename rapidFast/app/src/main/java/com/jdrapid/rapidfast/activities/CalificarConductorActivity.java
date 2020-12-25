package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;

import com.jdrapid.rapidfast.models.ClientBooking;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.HistoryBookingProvider;

import java.util.Date;

public class CalificarConductorActivity extends AppCompatActivity {
    private TextView mOrigenCalif,mDestinoCalif,califPrecio;
    private RatingBar mRatingBar;
    private Button mButtonCalificacion;
    private ClienteReservaProvider clienteReservaProvider;
    private HistoryBooking historyBooking;
    private HistoryBookingProvider historyBookingProvider;

    private float mCalificacion=0;
    private AuthProvider authProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar_conductor);


        mOrigenCalif=findViewById(R.id.OrigenCalificacionCliente);
        mDestinoCalif=findViewById(R.id.DestinoCalificacionCliente);
        califPrecio=findViewById(R.id.CalificacionPreciocon);
        mRatingBar=findViewById(R.id.RtCalificarConductor);
        mButtonCalificacion=findViewById(R.id.CalificarCondcutor);
        mRatingBar=findViewById(R.id.RtCalificarConductor);

        authProvider=new AuthProvider();
        clienteReservaProvider=new ClienteReservaProvider();
        historyBookingProvider=new HistoryBookingProvider();

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float calificacion, boolean b) {
                mCalificacion=calificacion;
            }
        });

        mButtonCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calificar();
            }
        });

        obtenerClienteSolicitud();
    }
    private void obtenerClienteSolicitud(){
        clienteReservaProvider.getClienteSolicitud(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ClientBooking clientBooking=snapshot.getValue(ClientBooking.class);


                    if (clientBooking != null) {
                        mOrigenCalif.setText(clientBooking.getOrigen());
                        mDestinoCalif.setText(clientBooking.getDestino());
                    }
                    califPrecio.setText("$ "+String.format("%.1f",clientBooking.getPrecio()));


                    historyBooking=new HistoryBooking(
                            clientBooking.getIdHistorialSolicitud(),
                            clientBooking.getIdCliente(),
                            clientBooking.getIdConductor(),
                            clientBooking.getDestino(),
                            clientBooking.getOrigen(),
                            clientBooking.getTiempo(),
                            clientBooking.getDistanciaKm(),
                            clientBooking.getEstado(),
                            clientBooking.getOrigenLat(),
                            clientBooking.getOrigenLong(),
                            clientBooking.getDestinoLat(),
                            clientBooking.getDestinoLong()
                    );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calificar() {
        if (mCalificacion>0){
            historyBooking.setCalificacionConductor(mCalificacion);
            historyBooking.setTimestamp(new Date().getTime());
            historyBookingProvider.getHistorialReserva(historyBooking.getIdHistorialSolicitud()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        historyBookingProvider.ActualizarCalificacionConducotor(historyBooking.getIdHistorialSolicitud(),mCalificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificarConductorActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CalificarConductorActivity.this, MapClienteActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else {
                        historyBookingProvider.Crear(historyBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificarConductorActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CalificarConductorActivity.this, MapClienteActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else {
            Toast.makeText(this, "Por Favor ingresa la calificacion", Toast.LENGTH_SHORT).show();
        }
    }
}