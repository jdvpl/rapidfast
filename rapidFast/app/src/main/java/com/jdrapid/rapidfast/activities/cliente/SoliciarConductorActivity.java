package com.jdrapid.rapidfast.activities.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.models.ClientBooking;
import com.jdrapid.rapidfast.models.FCMBody;
import com.jdrapid.rapidfast.models.FCMResponse;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.GoogleApiProvider;
import com.jdrapid.rapidfast.providers.NotificationProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;
import com.jdrapid.rapidfast.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoliciarConductorActivity extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
    private TextView Buscandoa;
    private Button BtnCancelar;

    private GeofireProvider geofireProvider;
//    trae variables del otro activity
    private String mExtraOrigen,mExtraDestino;
    private double mExtraLat,mExtraLon,mExtraDestinoLat,mExtraDestinoLon;
    private LatLng destinoLatlng;

    private LatLng origenLatlgn;
    private double RadioBusqueda=0.1;

    private boolean ConductorEncontrado=false;
    private String IDConductorEncontrado="";

    private LatLng jConductorEncontradolatlng;

    //    notificaciones
    private NotificationProvider notificationProvider;
//    toekn
    private TokenProvider tokenProvider;
    private ClienteReservaProvider clienteReservaProvider;
//    authrovider
    private AuthProvider authProvider;
    private GoogleApiProvider googleApiProvider;
    private ConductorProvider conductorProvider;
    private ClienteProvider clienteProvider;
//    escuhador
    private ValueEventListener mlistener;
    private String NombreCOnductor,NombreCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soliciar_conductor);
        lottieAnimationView=findViewById(R.id.animacionBuscando);
        Buscandoa=findViewById(R.id.TxtBuscandoCOnductor);
        BtnCancelar=findViewById(R.id.BtncancelarVije);
//        obtener la infro del activity anterior
        mExtraOrigen=getIntent().getStringExtra("origen");
        mExtraDestino=getIntent().getStringExtra("destino");
        mExtraLat=getIntent().getDoubleExtra("origin_lat",0);
        mExtraLon=getIntent().getDoubleExtra("origin_lon",0);
        mExtraDestinoLat=getIntent().getDoubleExtra("destino_lat",0);
        mExtraDestinoLon=getIntent().getDoubleExtra("destino_lon",0);

        lottieAnimationView.playAnimation();
        origenLatlgn=new LatLng(mExtraLat,mExtraLon);
        destinoLatlng=new LatLng(mExtraDestinoLat,mExtraDestinoLon);
//        isntanciar
        geofireProvider=new GeofireProvider("Conductores_Activos");
        tokenProvider=new TokenProvider();
        googleApiProvider=new GoogleApiProvider(SoliciarConductorActivity.this);

        notificationProvider=new NotificationProvider();
        clienteReservaProvider=new ClienteReservaProvider();

        conductorProvider=new ConductorProvider();
        clienteProvider=new ClienteProvider();
        authProvider=new AuthProvider();




        ObtenerConductoresCerca();
    }
    private void ObtenerConductoresCerca(){
        geofireProvider.ConductoresActivos(origenLatlgn,RadioBusqueda).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!ConductorEncontrado){
                    ConductorEncontrado=true;
                    IDConductorEncontrado=key;
                    jConductorEncontradolatlng=new LatLng(location.latitude,location.longitude);
                    Buscandoa.setText("Conductor Encontrado\nEsperando Respuesta.");
                    CrearSolicitudCliente();
                    Log.d("driver","Id: "+IDConductorEncontrado);

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
//                ingresa cuando termina la busqueda de lconductor en un radio de 0.1km
                if (!ConductorEncontrado){
                    RadioBusqueda=RadioBusqueda+0.1f;
//                    no encontro un donductor en 20km
//                    preguntar a maykiol en que radio maximo
                    if (RadioBusqueda>15){
                        Buscandoa.setText("No se econtraron conductores");
                        Toast.makeText(SoliciarConductorActivity.this, "No se encontraron conductores", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        ObtenerConductoresCerca();
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void CrearSolicitudCliente(){
        googleApiProvider.getDirecciones(origenLatlgn,jConductorEncontradolatlng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject=new JSONObject(response.body());
                    JSONArray jsonArray=jsonObject.getJSONArray("routes");
                    JSONObject ruta=jsonArray.getJSONObject(0);
                    JSONObject poligonos=ruta.getJSONObject("overview_polyline");
                    String puntos=poligonos.getString("points");

//                    para obtener el tiempo de la api ejemplo: https://maps.googleapis.com/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&origin=1.2034717,-77.2922318&destination=1.2039062,-77.2927302&departure_time=1603143184104&traffic_model=best_guess&key=AIzaSyAKltRdYeGz-VViXHEaYu00KR7dWirLdv8
                    JSONArray legs=ruta.getJSONArray("legs");
                    JSONObject leg=legs.getJSONObject(0);
                    JSONObject distancia=leg.getJSONObject("distance");
                    JSONObject duracion=leg.getJSONObject("duration");
//                    obtener el string de la api
                    String Distancia=distancia.getString("text");
                    String Duracion=duracion.getString("text");

                    EnviarNotificacion(Duracion,Distancia);



                }catch (Exception e){
                    Log.d("Error","Jiren: "+e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void EnviarNotificacion(final String tiempo,String distancia) {
        tokenProvider.getToken(IDConductorEncontrado).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String token=dataSnapshot.child("token").getValue().toString();
                    Map<String,String> map=new HashMap<>();
                    map.put("title","SOLICITUD DE SERVICIO A "+tiempo+" De TU POSICION");
                    map.put("body",
                            "Un Cliente esta solicitando un servicio a una distancia de "+distancia
                            +"\n"+"Recoger en: "+mExtraOrigen+"\n"+
                                    "Destino: " +mExtraDestino

                    );
                    map.put("idCliente",authProvider.getId());

                    FCMBody fcmBody=new FCMBody(token,"high","450s",map);

                    notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body()!=null){
                                if (response.body().getSuccess()==1){
                                    ClientBooking clientBooking=new ClientBooking(
                                            authProvider.getId(),
                                            IDConductorEncontrado,
                                            mExtraDestino,
                                            mExtraOrigen,
                                            tiempo,
                                            distancia,
                                            "Creado",
                                            mExtraLat,
                                            mExtraLon,
                                            mExtraDestinoLat,
                                            mExtraDestinoLon
                                    );
                                    clienteReservaProvider.Crear(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            RevisarEstadoSolicitud();
                                        }
                                    });
                                }else {
                                    Log.d("ErrorKisamado","Error 1024521: "+token);
                                    Toast.makeText(SoliciarConductorActivity.this, "No se pudo enviar notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(SoliciarConductorActivity.this, "No se pudo enviar notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("ErrorKisamado","Error 2527: "+t.getMessage());

                        }
                    });
                }else {
                    Toast.makeText(SoliciarConductorActivity.this, "No se pudo enviar la notificacion por que el conductor se desconecto", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void RevisarEstadoSolicitud() {
        mlistener=clienteReservaProvider.getEstado(authProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String estado=snapshot.getValue().toString();
                    if (estado.equals("Aceptado")){
                        Intent intent=new Intent(SoliciarConductorActivity.this,MapClienteReservaActivity.class);
                        startActivity(intent);
                        finish();
                    }else if (estado.equals("Cancelado")){
                        Toast.makeText(SoliciarConductorActivity.this, "El conductor no acepto el viaje", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(SoliciarConductorActivity.this,MapClienteActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mlistener !=null){
            clienteReservaProvider.getEstado(authProvider.getId()).removeEventListener(mlistener);
        }

    }
}