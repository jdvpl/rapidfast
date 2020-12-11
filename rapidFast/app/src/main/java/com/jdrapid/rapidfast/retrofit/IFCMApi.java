package com.jdrapid.rapidfast.retrofit;

import com.jdrapid.rapidfast.models.FCMBody;
import com.jdrapid.rapidfast.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAZCnB1q0:APA91bHcNwolAVXnGQNXKGbxJCDB-GPfIladmrAmaYNuU0jsof8RJQ2AOkFBjxttfGAy4PLV7AYObPiy5_IgxBZadxfxoZvWvT0r3g-ldcqh5WpGHj1Q-Ei7j9kjVHmg7kjWVsSDiWY9"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);

}
