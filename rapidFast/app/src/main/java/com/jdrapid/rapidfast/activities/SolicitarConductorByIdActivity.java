package com.jdrapid.rapidfast.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.jdrapid.rapidfast.R;

import androidx.annotation.NonNull;

import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.models.ClientBooking;
import com.jdrapid.rapidfast.models.FCMBody;
import com.jdrapid.rapidfast.models.FCMResponse;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.GoogleApiProvider;
import com.jdrapid.rapidfast.providers.NotificationProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SolicitarConductorByIdActivity extends AppCompatActivity {
    private LottieAnimationView mAnimation;
    private TextView mTextViewLookingFor;
    private Button mButtonCancelRequest;

    private String mExtraOrigin;
    private String mExtraDestination;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private double mExtraDriverLat;
    private double mExtraDriverLng;
    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;
    double precio;
    private String  mIdDriverFound = "";
    private LatLng mDriverFoundLatLng;
    private NotificationProvider mNotificationProvider;
    private TokenProvider mTokenProvider;

    private ClienteReservaProvider mClientBookingProvider;
    private AuthProvider mAuthProvider;
    private GoogleApiProvider mGoogleApiProvider;

    private ValueEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soliciar_conductor);

        mAnimation = findViewById(R.id.animacionBuscando);
        mTextViewLookingFor = findViewById(R.id.TxtBuscandoCOnductor);
        mButtonCancelRequest = findViewById(R.id.BtncancelarVije);

        mAnimation.playAnimation();

        mExtraOrigin = getIntent().getStringExtra("origen");
        mExtraDestination = getIntent().getStringExtra("destino");

        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lon", 0);

        mExtraDestinationLat = getIntent().getDoubleExtra("destino_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destino_lon", 0);
        precio=getIntent().getDoubleExtra("precio",0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng= new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mTokenProvider = new TokenProvider();
        mNotificationProvider = new NotificationProvider();
        mClientBookingProvider = new ClienteReservaProvider();
        mAuthProvider = new AuthProvider();
        mGoogleApiProvider = new GoogleApiProvider(SolicitarConductorByIdActivity.this);

        mButtonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequest();
            }
        });


        mIdDriverFound = getIntent().getStringExtra("idConductor");
        mExtraDriverLat = getIntent().getDoubleExtra("Conductor_lat", 0);
        mExtraDriverLng = getIntent().getDoubleExtra("Conductor_lon", 0);

        mDriverFoundLatLng = new LatLng(mExtraDriverLat, mExtraDriverLng);
        mTextViewLookingFor.setText("CONDUCTOR ENCONTRADO\nESPERANDO RESPUESTA");
        createClientBooking();
    }

    private void cancelRequest() {

        mClientBookingProvider.borrar(mAuthProvider.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotificationCancel();
            }
        });

    }

    private void createClientBooking() {

        mGoogleApiProvider.getDirecciones(mOriginLatLng, mDriverFoundLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    JSONArray legs =  route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");
                    sendNotification(durationText, distanceText);

                } catch(Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    private void sendNotification(final String time, final String km) {
        mTokenProvider.getToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    List<String> mTokenList=new ArrayList<>();
                    mTokenList.add(token);
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "SOLICITUD DE SERVICIO A " + time + " DE TU POSICION");
                    map.put("body",
                            "Un cliente esta solicitando un servicio a una distancia de " + km + "\n" +
                                    "Recoger en: " + mExtraOrigin + "\n" +
                                    "Destino: " + mExtraDestination
                    );
                    map.put("idCliente", mAuthProvider.getId());
                    map.put("origen", mExtraOrigin);
                    map.put("destino", mExtraDestination);
                    map.put("tiempo", time);
                    map.put("distancia", km);
                    map.put("searchById", "true");
                    FCMBody fcmBody = new FCMBody(mTokenList, "high", "450s", map);
                    mNotificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getSuccess() == 1) {
                                    ClientBooking clientBooking = new ClientBooking(

                                            mAuthProvider.getId(),
                                            mIdDriverFound,
                                            mExtraDestination,
                                            mExtraOrigin,
                                            time,
                                            precio,
                                            km,
                                            "Creado",
                                            mExtraOriginLat,
                                            mExtraOriginLng,
                                            mExtraDestinationLat,
                                            mExtraDestinationLng
                                    );

                                    mClientBookingProvider.Crear(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            checkStatusClientBooking();
                                        }
                                    });
                                    //Toast.makeText(RequestDriverActivity.this, "La notificacion se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(SolicitarConductorByIdActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(SolicitarConductorByIdActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "Error " + t.getMessage());
                        }
                    });
                }
                else {
                    Toast.makeText(SolicitarConductorByIdActivity.this, "No se pudo enviar la notificacion porque el conductor no tiene un token de sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotificationCancel() {

        if (mIdDriverFound != null) {
            mTokenProvider.getToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.hasChild("token")) {
                            String token = dataSnapshot.child("token").getValue().toString();
                            List<String> mTokenList=new ArrayList<>();
                            mTokenList.add(token);
                            Map<String, String> map = new HashMap<>();
                            map.put("title", "VIAJE CANCELADO");
                            map.put("body",
                                    "El cliente cancelo la solicitud"
                            );

                            FCMBody fcmBody = new FCMBody(mTokenList, "high", "450s", map);
                            mNotificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                                @Override
                                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                    if (response.body() != null) {
                                        if (response.body().getSuccess() == 1) {
                                            Toast.makeText(SolicitarConductorByIdActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SolicitarConductorByIdActivity.this, MapClienteActivity.class);
                                            startActivity(intent);
                                            finish();
                                            //Toast.makeText(RequestDriverActivity.this, "La notificacion se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(SolicitarConductorByIdActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(SolicitarConductorByIdActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<FCMResponse> call, Throwable t) {
                                    Log.d("Error", "Error " + t.getMessage());
                                }
                            });
                        }
                        else {
                            Toast.makeText(SolicitarConductorByIdActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SolicitarConductorByIdActivity.this, MapClienteActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        Toast.makeText(SolicitarConductorByIdActivity.this, "No se pudo enviar la notificacion porque el conductor no tiene un token de sesion", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(SolicitarConductorByIdActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SolicitarConductorByIdActivity.this, MapClienteActivity.class);
            startActivity(intent);
            finish();
        }

    }



    private void checkStatusClientBooking() {
        mListener = mClientBookingProvider.getEstado(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String status = dataSnapshot.getValue().toString();
                    if (status.equals("Aceptado")) {
                        Intent intent = new Intent(SolicitarConductorByIdActivity.this, MapClienteReservaActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (status.equals("Cancelado")) {
                        Toast.makeText(SolicitarConductorByIdActivity.this, "El conductor no acepto el viaje", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SolicitarConductorByIdActivity.this, MapClienteActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mClientBookingProvider.getEstado(mAuthProvider.getId()).removeEventListener(mListener);
        }
    }
}