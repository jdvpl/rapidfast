package com.jdrapid.rapidfast.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitCliente {

    public static Retrofit obtenerCliente(String url){
           Retrofit retrofit=new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create()).build();
           return retrofit;
    }

    public static Retrofit obtenerClienteObjeto(String url){
           Retrofit retrofit=new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
            return retrofit;
    }
}
