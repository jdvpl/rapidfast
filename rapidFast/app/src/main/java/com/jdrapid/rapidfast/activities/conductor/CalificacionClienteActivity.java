package com.jdrapid.rapidfast.activities.conductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.jdrapid.rapidfast.activities.cliente.CalificarConductorActivity;
import com.jdrapid.rapidfast.activities.cliente.MapClienteActivity;
import com.jdrapid.rapidfast.models.ClientBooking;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.HistoryBookingProvider;

import java.util.Date;

public class CalificacionClienteActivity extends AppCompatActivity {
    private TextView mOrigenCalif,mDestinoCalif,califPrecio;
    private RatingBar mRatingBar;
    private Button mButtonCalificacion;
    private ClienteReservaProvider clienteReservaProvider;
    private String mExtraCleinteId;
    private HistoryBooking historyBooking;
    private HistoryBookingProvider historyBookingProvider;

    private float mCalificacion=0;
    private double mExtraPrecio=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion_cliente);


        mOrigenCalif=findViewById(R.id.OrigenCalificacionConductor);
        mDestinoCalif=findViewById(R.id.DestinoCalificacionConductor);
        califPrecio=findViewById(R.id.CalificacionPrecio);

        mRatingBar=findViewById(R.id.RtCalificarCliente);
        mButtonCalificacion=findViewById(R.id.CalificarCliente);
        mRatingBar=findViewById(R.id.RtCalificarCliente);
        mExtraCleinteId=getIntent().getStringExtra("idCliente");
        mExtraPrecio=getIntent().getDoubleExtra("precio",0);
        clienteReservaProvider=new ClienteReservaProvider();
        historyBookingProvider=new HistoryBookingProvider();

        califPrecio.setText("$ "+String.format("%.1f",mExtraPrecio));

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
        clienteReservaProvider.getClienteSolicitud(mExtraCleinteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ClientBooking clientBooking=snapshot.getValue(ClientBooking.class);


                    if (clientBooking != null) {
                        mOrigenCalif.setText(clientBooking.getOrigen());
                        mDestinoCalif.setText(clientBooking.getDestino());
                    }


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
            historyBooking.setCalificacionCliente(mCalificacion);
            historyBooking.setTimestamp(new Date().getTime());
            historyBookingProvider.getHistorialReserva(historyBooking.getIdHistorialSolicitud()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        historyBookingProvider.ActualizarCalificacionCliente(historyBooking.getIdHistorialSolicitud(),mCalificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificacionClienteActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CalificacionClienteActivity.this, MapConductorActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });;
                    }else {
                        historyBookingProvider.Crear(historyBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificacionClienteActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CalificacionClienteActivity.this,MapConductorActivity.class);
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