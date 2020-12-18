package com.jdrapid.rapidfast.activities.conductor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.cliente.HistorialSolicitudClienteActivity;
import com.jdrapid.rapidfast.adapters.HistoryBookingClienteAdapter;
import com.jdrapid.rapidfast.adapters.HistoryBookingConductorAdapter;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.AuthProvider;

public class HistorialSolicitudConductorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryBookingConductorAdapter historyBookingConductorAdapter;
    private AuthProvider authProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_solicitud_conductor);
        ToolBar.mostrar(this,"Historial Viajes",true);

        recyclerView=findViewById(R.id.recyclerHistorial);
        LinearLayoutManager linearLayout=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        authProvider=new AuthProvider();
        Query query= FirebaseDatabase.getInstance().getReference().child("HistorialViajes").orderByChild("idConductor").equalTo(authProvider.getId());
        FirebaseRecyclerOptions<HistoryBooking> options=new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query,HistoryBooking.class).build();

        historyBookingConductorAdapter=new HistoryBookingConductorAdapter(options, HistorialSolicitudConductorActivity.this);
        recyclerView.setAdapter(historyBookingConductorAdapter);
        historyBookingConductorAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        historyBookingConductorAdapter.stopListening();
    }
}