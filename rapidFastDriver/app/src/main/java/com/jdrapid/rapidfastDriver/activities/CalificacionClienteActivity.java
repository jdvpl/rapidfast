package com.jdrapid.rapidfastDriver.activities;

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
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.models.ClientBooking;
import com.jdrapid.rapidfastDriver.models.HistoryBooking;
import com.jdrapid.rapidfastDriver.providers.ClienteReservaProvider;
import com.jdrapid.rapidfastDriver.providers.HistoryBookingProvider;

import java.util.Date;

public class CalificacionClienteActivity extends AppCompatActivity {
    private TextView mOrigenCalif,mDestinoCalif,califPrecio,TxtMensajeCliente;
    private RatingBar mRatingBar;
    private Button mButtonCalificacion;
    private ClienteReservaProvider clienteReservaProvider;
    private String mExtraCleinteId;
    private HistoryBooking historyBooking;
    private HistoryBookingProvider historyBookingProvider;
    String MensajeCliente;
    private float mCalificacion=0;
    private String mExtraPrecio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion_cliente);


        mOrigenCalif=findViewById(R.id.OrigenCalificacionConductor);
        mDestinoCalif=findViewById(R.id.DestinoCalificacionConductor);
        califPrecio=findViewById(R.id.CalificacionPrecio);
        TxtMensajeCliente=findViewById(R.id.TxtMensajeCliente);

        mRatingBar=findViewById(R.id.RtCalificarCliente);
        mButtonCalificacion=findViewById(R.id.CalificarCliente);
        mRatingBar=findViewById(R.id.RtCalificarCliente);
        mExtraCleinteId=getIntent().getStringExtra("idCliente");
        mExtraPrecio=getIntent().getStringExtra("precio");
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
                    MensajeCliente=TxtMensajeCliente.getText().toString();

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
                            clientBooking.getDestinoLong(),
                            mExtraPrecio,
                            MensajeCliente,
                            ""
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
            historyBooking.setMensajeCliente(MensajeCliente);
            historyBookingProvider.getHistorialReserva(historyBooking.getIdHistorialSolicitud()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        historyBookingProvider.ActualizarCalificacionCliente(historyBooking.getIdHistorialSolicitud(),mCalificacion,MensajeCliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificacionClienteActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CalificacionClienteActivity.this, MapConductorActivity.class);
                                intent.putExtra("Conectado",true);
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
                                intent.putExtra("Conectado",true);
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