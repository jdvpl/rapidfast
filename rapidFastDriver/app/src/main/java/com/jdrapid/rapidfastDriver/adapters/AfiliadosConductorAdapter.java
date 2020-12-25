package com.jdrapid.rapidfastDriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.jdrapid.rapidfastDriver.models.Afiliados;
import com.jdrapid.rapidfastDriver.models.HistoryBooking;
import com.jdrapid.rapidfastDriver.providers.ClienteProvider;
import com.jdrapid.rapidfastDriver.providers.ConductorProvider;
import com.squareup.picasso.Picasso;

public class AfiliadosConductorAdapter extends FirebaseRecyclerAdapter <Afiliados, AfiliadosConductorAdapter.ViewHolder>{

    private ConductorProvider conductorProvider;
    private Context mContext;
    public AfiliadosConductorAdapter(FirebaseRecyclerOptions<Afiliados> options, Context context){
        super(options);
        conductorProvider=new ConductorProvider();
        mContext=context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Afiliados afiliados) {
        String id=getRef(position).getKey();
        holder.txtcedula.setText(afiliados.getCedula());
        holder.txtnombre.setText(afiliados.getNombre());
        holder.Txtcelular.setText(afiliados.getCelular());
        if (afiliados.getEstado().equals("Creado")){
            holder.txtestado.setTextColor(Color.RED);
        }else {
            holder.txtestado.setTextColor(Color.GREEN);

        }
        holder.txtestado.setText(afiliados.getEstado());
        holder.txtfecha.setText(afiliados.getFecha());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_afiliados,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtcedula,txtnombre,Txtcelular,txtestado,txtfecha;
        private View mview;
        public ViewHolder(View view){
            super(view);
            mview=view;
            txtcedula=view.findViewById(R.id.TxtcedulaAfiliado);
            txtnombre=view.findViewById(R.id.TxtnombreAfiliado);
            Txtcelular=view.findViewById(R.id.TxtcelularAfiliado);
            txtestado=view.findViewById(R.id.TxtestadoAfiliado);
            txtfecha=view.findViewById(R.id.TxtFechaAfiliado);
        }
    }
}
