package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.HistoryBookingProvider;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistorialSolicitudDetalle extends AppCompatActivity {

    private TextView txtNombredetalle,txtOrigenDetalle,txtDestinoDetalle,txtCalificacionDetalle,TxtCalifiPrecio,TxtCalifFecha,TxtCalifComen;
    private RatingBar ratingBardetalle;
    private CircleImageView circleImageConductor;
    private String mExtraid;
    private HistoryBookingProvider bookingProvider;
    private ConductorProvider conductorProvider;
    SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_solicitud_detalle);

        txtNombredetalle=findViewById(R.id.nombresolicituddetalle);
        txtOrigenDetalle=findViewById(R.id.OringenDetalle);
        txtDestinoDetalle=findViewById(R.id.DestinoDetalle);
        txtCalificacionDetalle=findViewById(R.id.calificacionDetalle);

        TxtCalifiPrecio=findViewById(R.id.calificacionPrecio);
        TxtCalifFecha=findViewById(R.id.calificacionfecha);
        TxtCalifComen=findViewById(R.id.calificacioncomentario);

        ratingBardetalle=findViewById(R.id.ratingbarSolicitudDetalle);
        circleImageConductor=findViewById(R.id.fotodeSolicituddetalle);


        ToolBar.mostrar(this,"",true);
        mExtraid=getIntent().getStringExtra("idHistorialSolicitud");

        bookingProvider=new HistoryBookingProvider();
        conductorProvider=new ConductorProvider();
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
                    txtCalificacionDetalle.setText("Tu calificacion: "+historyBooking.getCalificacionConductor());

                    TxtCalifiPrecio.setText("$ "+historyBooking.getPrecio());
                    TxtCalifFecha.setText(String.valueOf(formatofecha.format(historyBooking.getTimestamp())));
                    TxtCalifComen.setText(historyBooking.getMensajeCliente());
                    if (snapshot.hasChild("calificacionCliente")){
                        ratingBardetalle.setRating((float) historyBooking.getCalificacionCliente());
                    }

                    conductorProvider.getConductor(historyBooking.getIdConductor()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String nombre=snapshot.child("nombre").getValue().toString();
                                txtNombredetalle.setText(nombre.toUpperCase());
                                if (snapshot.hasChild("imagen")){
                                    String imagen=snapshot.child("imagen").getValue().toString();
                                    Picasso.with(HistorialSolicitudDetalle.this).load(imagen).into(circleImageConductor);
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