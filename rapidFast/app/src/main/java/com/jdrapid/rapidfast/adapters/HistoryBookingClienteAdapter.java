package com.jdrapid.rapidfast.adapters;

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
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.HistorialSolicitudDetalle;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.squareup.picasso.Picasso;

public class HistoryBookingClienteAdapter extends FirebaseRecyclerAdapter <HistoryBooking, HistoryBookingClienteAdapter.ViewHolder>{

    private ConductorProvider conductorProvider;
    private Context mContext;
    public HistoryBookingClienteAdapter(FirebaseRecyclerOptions<HistoryBooking> options,Context context){
        super(options);
        conductorProvider=new ConductorProvider();
        mContext=context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HistoryBooking historyBooking) {
        String id=getRef(position).getKey();
        holder.txtOrigen.setText(historyBooking.getOrigen());
        holder.TxtDestino.setText(historyBooking.getDestino());
        holder.txtCalificaion.setText(String.valueOf(historyBooking.getCalificacionCliente()));
        conductorProvider.getConductor(historyBooking.getIdConductor()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre=snapshot.child("nombre").getValue().toString();
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
                Intent intent=new Intent(mContext, HistorialSolicitudDetalle.class);
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
