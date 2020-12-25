package com.jdrapid.rapidfastDriver.providers;


import com.jdrapid.rapidfastDriver.models.FCMBody;
import com.jdrapid.rapidfastDriver.models.FCMResponse;
import com.jdrapid.rapidfastDriver.retrofit.IFCMApi;
import com.jdrapid.rapidfastDriver.retrofit.RetrofitCliente;

import retrofit2.Call;

public class NotificationProvider {
    private String url="https://fcm.googleapis.com";

    public NotificationProvider() {
    }
    public Call<FCMResponse> sendNotificacion(FCMBody body){
         return RetrofitCliente.obtenerClienteObjeto(url).create(IFCMApi.class).send(body);
    }
}
