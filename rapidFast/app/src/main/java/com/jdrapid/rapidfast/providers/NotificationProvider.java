package com.jdrapid.rapidfast.providers;


import com.jdrapid.rapidfast.models.FCMBody;
import com.jdrapid.rapidfast.models.FCMResponse;
import com.jdrapid.rapidfast.retrofit.IFCMApi;
import com.jdrapid.rapidfast.retrofit.RetrofitCliente;

import retrofit2.Call;

public class NotificationProvider {
    private String url="https://fcm.googleapis.com";

    public NotificationProvider() {
    }
    public Call<FCMResponse> sendNotificacion(FCMBody body){
         return RetrofitCliente.obtenerClienteObjeto(url).create(IFCMApi.class).send(body);
    }
}
