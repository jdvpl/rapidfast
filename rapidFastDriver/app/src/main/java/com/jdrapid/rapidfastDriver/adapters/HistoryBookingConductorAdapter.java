package com.jdrapid.rapidfastDriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.activities.HistorialDetalleConductor;
import com.jdrapid.rapidfastDriver.models.HistoryBooking;
import com.jdrapid.rapidfastDriver.providers.ClienteProvider;
import com.squareup.picasso.Picasso;

public class HistoryBookingConductorAdapter extends FirebaseRecyclerAdapter <HistoryBooking, HistoryBookingConductorAdapter.ViewHolder>{

    private ClienteProvider clienteProvider;
    private Context mContext;
    public HistoryBookingConductorAdapter(FirebaseRecyclerOptions<HistoryBooking> options, Context context){
        super(options);
        clienteProvider=new ClienteProvider();
        mContext=context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HistoryBooking historyBooking) {
        String id=getRef(position).getKey();
        holder.txtOrigen.setText(historyBooking.getOrigen());
        holder.TxtDestino.setText(historyBooking.getDestino());
        holder.txtCalificaion.setText(String.valueOf(historyBooking.getCalificacionConductor()));
        clienteProvider.getCliente(historyBooking.getIdCliente()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre=snapshot.child("Nombre").getValue().toString();
                    if (snapshot.hasChild("imagen")){
                        String imagen=snapshot.child("imagen").getValue().toString();
                        Picasso.with(mContext).load(imagen).into(holder.imgHistorial);
                    }

                    holder.txtNombre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.txtNombre.setText(historyBooking.getOrigen());
        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, HistorialDetalleConductor.class);
                intent.putExtra("idHistorialSolicitud",id);
                mContext.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_resrvaciones,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNombre,txtOrigen,TxtDestino,txtCalificaion;
        private ImageView imgHistorial;
        private View mview;
        public ViewHolder(View view){
            super(view);
            mview=view;
            txtNombre=view.findViewById(R.id.Txtnombre);
            txtOrigen=view.findViewById(R.id.TxtOrigen);
            TxtDestino=view.findViewById(R.id.TxtDestino);
            txtCalificaion=view.findViewById(R.id.TxtCalificacion);
            imgHistorial=view.findViewById(R.id.ImgHistorialSolicitud);
        }
    }
}
