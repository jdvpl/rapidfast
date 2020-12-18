package com.jdrapid.rapidfast.activities.conductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.cliente.HistorialSolicitudDetalle;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.ClienteProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.HistoryBookingProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistorialDetalleConductor extends AppCompatActivity {

    private TextView txtNombredetalle,txtOrigenDetalle,txtDestinoDetalle,txtCalificacionDetalle;
    private RatingBar ratingBardetalle;
    private CircleImageView circleImageConductor,circleatras;
    private String mExtraid;
    private HistoryBookingProvider bookingProvider;
    private ClienteProvider clienteProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_detalle_conductor);

        txtNombredetalle=findViewById(R.id.nombresolicituddetalle);
        txtOrigenDetalle=findViewById(R.id.OringenDetalle);
        txtDestinoDetalle=findViewById(R.id.DestinoDetalle);
        txtCalificacionDetalle=findViewById(R.id.calificacionDetalle);
        ratingBardetalle=findViewById(R.id.ratingbarSolicitudDetalle);
        circleImageConductor=findViewById(R.id.fotodeSolicituddetalle);
        circleatras=findViewById(R.id.circleimageback);

        mExtraid=getIntent().getStringExtra("idHistorialSolicitud");

        bookingProvider=new HistoryBookingProvider();
        clienteProvider=new ClienteProvider();
        obtenerInformacionHistorial();
        circleatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



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