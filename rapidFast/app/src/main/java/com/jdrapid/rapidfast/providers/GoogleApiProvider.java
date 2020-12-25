package com.jdrapid.rapidfast.providers;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.retrofit.IGoogleApi;
import com.jdrapid.rapidfast.retrofit.RetrofitCliente;

import retrofit2.Call;

public class GoogleApiProvider {
    private final Context context;


    public GoogleApiProvider(Context context) {
        this.context=context;
    }

    public Call<String> getDirecciones(LatLng OrigenlatLng,LatLng DestinolatLng){
        String baseUrl="https://maps.googleapis.com";
        String query="/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&" +
                "origin="+OrigenlatLng.latitude+","+OrigenlatLng.longitude+"&"+
                "destination="+DestinolatLng.latitude+","+DestinolatLng.longitude+"&" +
                "&key="+context.getResources().getString(R.string.google_maps_key);

        return RetrofitCliente.obtenerCliente(baseUrl).create(IGoogleApi.class).getDirecciones(baseUrl+query);
    }
}
