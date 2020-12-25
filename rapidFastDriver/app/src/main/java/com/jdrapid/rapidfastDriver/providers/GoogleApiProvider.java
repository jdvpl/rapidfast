package com.jdrapid.rapidfastDriver.providers;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.retrofit.IGoogleApi;
import com.jdrapid.rapidfastDriver.retrofit.RetrofitCliente;

import retrofit2.Call;

public class GoogleApiProvider {
    private Context context;


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
