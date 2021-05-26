package com.jdrapid.rapidfastDriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.includes.ToolBar;
import com.jdrapid.rapidfastDriver.models.HistoryBooking;
import com.jdrapid.rapidfastDriver.providers.ClienteProvider;
import com.jdrapid.rapidfastDriver.providers.HistoryBookingProvider;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistorialDetalleConductor extends AppCompatActivity {

    private TextView txtNombredetalle,txtOrigenDetalle,txtDestinoDetalle,txtCalificacionDetalle,TxtCalifiPrecio,TxtCalifFecha,TxtCalifComen;
    private RatingBar ratingBardetalle;
    private CircleImageView circleImageConductor,circleatras;
    private String mExtraid;
    private HistoryBookingProvider bookingProvider;
    private ClienteProvider clienteProvider;
    SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_detalle_conductor);
        ToolBar.mostrar(HistorialDetalleConductor.this,"",true);

        txtNombredetalle=findViewById(R.id.nombresolicituddetalle);
        txtOrigenDetalle=findViewById(R.id.OringenDetalle);
        txtDestinoDetalle=findViewById(R.id.DestinoDetalle);
        txtCalificacionDetalle=findViewById(R.id.calificacionDetalle);
        ratingBardetalle=findViewById(R.id.ratingbarSolicitudDetalle);
        circleImageConductor=findViewById(R.id.fotodeSolicituddetalle);

        TxtCalifiPrecio=findViewById(R.id.calificacionPrecio);
        TxtCalifFecha=findViewById(R.id.calificacionfecha);
        TxtCalifComen=findViewById(R.id.calificacioncomentario);


        mExtraid=getIntent().getStringExtra("idHistorialSolicitud");

        bookingProvider=new HistoryBookingProvider();
        clienteProvider=new ClienteProvider();
        obtenerInformacionHistorial();


    }

    private void obtenerInformacionHistorial() {
        bookingProvider.getHistorialReserva(mExtraid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HistoryBooking historyBooking=snapshot.getValue(HistoryBooking.class);
                    txtOrigenDetalle.setText(historyBooking.getOrigen());
                    txtDestinoDetalle.setText(historyBooking.getDestino());
                    txtCalificacionDetalle.setText("Tu calificacion: "+historyBooking.getCalificacionCliente());

                    TxtCalifiPrecio.setText("$ "+historyBooking.getPrecio());

                    TxtCalifFecha.setText(String.valueOf(formatofecha.format(historyBooking.getTimestamp())));

                    TxtCalifComen.setText(historyBooking.getMensajeConductor());
                    if (snapshot.hasChild("calificacionConductor")){

                        ratingBardetalle.setRating((float) historyBooking.getCalificacionConductor());

                    }

                    clienteProvider.getCliente(historyBooking.getIdCliente()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String nombre=snapshot.child("Nombre").getValue().toString();
                                txtNombredetalle.setText(nombre.toUpperCase());
                                if (snapshot.hasChild("imagen")){
                                    String imagen=snapshot.child("imagen").getValue().toString();
                                    Picasso.with(HistorialDetalleConductor.this).load(imagen).into(circleImageConductor);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}