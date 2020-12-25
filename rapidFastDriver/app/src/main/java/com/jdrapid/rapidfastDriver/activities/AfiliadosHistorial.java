package com.jdrapid.rapidfastDriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.adapters.AfiliadosConductorAdapter;
import com.jdrapid.rapidfastDriver.includes.ToolBar;
import com.jdrapid.rapidfastDriver.models.Afiliados;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;

public class AfiliadosHistorial extends AppCompatActivity {
    Button btnagregar;
    private RecyclerView recyclerView;
    private AfiliadosConductorAdapter afiliadosConductorAdapter;
    private AuthProvider authProvider;
    private TextView cantidad;
    DatabaseReference reference;

    int suma=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afiliados_historial);
        ToolBar.mostrar(this,"Mis Afiliados",true);
        btnagregar=findViewById(R.id.BtnAgregar);
        cantidad=findViewById(R.id.CantidadAfiliados);

        reference= FirebaseDatabase.getInstance().getReference().child("Afiliados");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                suma= (int) snapshot.child(authProvider.getId()).getChildrenCount();
                cantidad.setText("Afiliados: "+suma);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView=findViewById(R.id.recyclerHistorialAfiliados);
        LinearLayoutManager linearLayout=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);

        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AfiliadosHistorial.this,AfiliadosActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        authProvider=new AuthProvider();
        Query query= FirebaseDatabase.getInstance().getReference().child("Afiliados").child(authProvider.getId()).orderByChild("idConductor").equalTo(authProvider.getId());
        FirebaseRecyclerOptions<Afiliados> options=new FirebaseRecyclerOptions.Builder<Afiliados>()
                .setQuery(query,Afiliados.class).build();
        afiliadosConductorAdapter=new AfiliadosConductorAdapter(options,AfiliadosHistorial.this);
        recyclerView.setAdapter(afiliadosConductorAdapter);
        afiliadosConductorAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        afiliadosConductorAdapter.stopListening();
    }
}