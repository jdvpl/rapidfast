package com.jdrapid.rapidfastDriver.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApi {
    @GET
    Call<String> getDirecciones(@Url String url);
}
