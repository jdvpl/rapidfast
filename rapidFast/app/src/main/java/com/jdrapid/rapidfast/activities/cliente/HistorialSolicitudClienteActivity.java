package com.jdrapid.rapidfast.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.adapters.HistoryBookingClienteAdapter;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.AuthProvider;

public class HistorialSolicitudClienteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryBookingClienteAdapter historyBookingClienteAdapter;
    private AuthProvider authProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_solicitud_cliente);
        ToolBar.mostrar(this,"Historial Viajes",true);

        recyclerView=findViewById(R.id.recyclerHistorial);
        LinearLayoutManager linearLayout=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        authProvider=new AuthProvider();
        Query query= FirebaseDatabase.getInstance().getReference().child("HistorialViajes").orderByChild("idCliente").equalTo(authProvider.getId());
        FirebaseRecyclerOptions<HistoryBooking>options=new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query,HistoryBooking.class).build();

        historyBookingClienteAdapter=new HistoryBookingClienteAdapter(options,HistorialSolicitudClienteActivity.this);
        recyclerView.setAdapter(historyBookingClienteAdapter);
        historyBookingClienteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        historyBookingClienteAdapter.stopListening();
    }
}