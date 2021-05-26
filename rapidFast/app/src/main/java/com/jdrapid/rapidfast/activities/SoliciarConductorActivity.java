package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.models.ClientBooking;
import com.jdrapid.rapidfast.models.ConductorEncontrado;
import com.jdrapid.rapidfast.models.FCMBody;
import com.jdrapid.rapidfast.models.FCMResponse;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.ConductoresEncontradosProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
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

public class SoliciarConductorActivity extends AppCompatActivity {
    private TextView Buscandoa;
    private Button BtnCancelar;

    private GeofireProvider geofireProvider;
//    trae variables del otro activity
    private String mExtraOrigen,mExtraDestino;
    private double mExtraLat,mExtraLon,mExtraDestinoLat,mExtraDestinoLon;
    private LatLng destinoLatlng;

    private LatLng origenLatlgn;
//    radio de busqueda de un conductor
    private double RadioBusqueda= 12;

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
    private ConductoresEncontradosProvider conductoresEncontradosProvider;
//    escuhador
    private ValueEventListener mlistener;
    LottieAnimationView animationView;
    private ArrayList<String> mDriverNoAceptaodos=new ArrayList<>();

    private Handler mHandler=new Handler();
    private  int nTiempoLimito=0;
    private boolean busquedafinalizada=false;
    private  boolean estabuscando=false;

    private ArrayList<String> mConductoresFounList=new ArrayList<>();
    private List<String> mTokenList=new ArrayList<>();
    private int mCounter=0;
    private int mCounterConductoresDisponibles=0;
    double precio;
    String Duracion,Distancia,Tiempo,DistanciaKm;
    Runnable mrunnable =new Runnable() {
        @Override
        public void run() {
            if (nTiempoLimito<35){
                nTiempoLimito++;
                mHandler.postDelayed(mrunnable,1000);

            }else {
                BorrarConductoresEncontrados();
                CancelarSolicitud();
                mHandler.removeCallbacks(mrunnable);
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soliciar_conductor);
        Buscandoa=findViewById(R.id.TxtBuscandoCOnductor);
        BtnCancelar=findViewById(R.id.BtncancelarVije);

        animationView=findViewById(R.id.animacionBuscando);
        animationView.playAnimation();
//        obtener la infro del activity anterior
        mExtraOrigen=getIntent().getStringExtra("origen");
        mExtraDestino=getIntent().getStringExtra("destino");
        mExtraLat=getIntent().getDoubleExtra("origin_lat",0);
        mExtraLon=getIntent().getDoubleExtra("origin_lon",0);
        mExtraDestinoLat=getIntent().getDoubleExtra("destino_lat",0);
        mExtraDestinoLon=getIntent().getDoubleExtra("destino_lon",0);
        precio=getIntent().getDoubleExtra("precio",0);

        DistanciaKm=getIntent().getStringExtra("distanciakm");
        Tiempo=getIntent().getStringExtra("tiempo");

        origenLatlgn=new LatLng(mExtraLat,mExtraLon);
        destinoLatlng=new LatLng(mExtraDestinoLat,mExtraDestinoLon);
//        isntanciar
        geofireProvider=new GeofireProvider("Conductores_Activos");
        tokenProvider=new TokenProvider();
        googleApiProvider=new GoogleApiProvider(SoliciarConductorActivity.this);

        notificationProvider=new NotificationProvider();
        clienteReservaProvider=new ClienteReservaProvider();
        conductoresEncontradosProvider=new ConductoresEncontradosProvider();

        conductorProvider=new ConductorProvider();
        clienteProvider=new ClienteProvider();
        authProvider=new AuthProvider();


        BtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BorrarConductoresEncontrados();
                CancelarSolicitud();
            }
        });



        ObtenerConductoresCerca();
    }

    private void BorrarConductoresEncontrados() {
        for (String idConductor: mConductoresFounList){
            conductoresEncontradosProvider.Borrar(idConductor);

        }
    }

    private void CancelarSolicitud() {
            clienteReservaProvider.borrar(authProvider.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    EnviarNotificacionCancelacion();
                }
            });
            
    }
    /*retornarno si el id del conductor encontrados en el radio que especificamos ya cancelo el viaje*/
    private boolean isDriverCancel(String idDriver){
        for (String id: mDriverNoAceptaodos){
            if (id.equals(idDriver)){
                return  true;
            }
        }
        return false;
    }
    private void RevisarEstadoSolicitud() {

        mlistener=clienteReservaProvider.getClienteSolicitud(authProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String estado=snapshot.child("estado").getValue().toString();
                    String idConductor=snapshot.child("idConductor").getValue().toString();
                    if (estado.equals("Aceptado") && !idConductor.equals("")){

                        EnviarNotificacionCancelacionAConductores(idConductor);

                        Intent intent=new Intent(SoliciarConductorActivity.this,MapClienteReservaActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else if (estado.equals("Cancelado")){

//                        if (estabuscando){
//                            InicarSolicitud();
//                        }

                        Toast.makeText(SoliciarConductorActivity.this, "El conductor no acepto el viaje", Toast.LENGTH_SHORT).show();
//                        Intent intent=new Intent(SoliciarConductorActivity.this,MapClienteActivity.class);
//                        startActivity(intent);
//                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void InicarSolicitud() {
        if (mHandler!=null){
            mHandler.removeCallbacks(mrunnable);
        }
        nTiempoLimito=0;
        estabuscando=false;
        mDriverNoAceptaodos.add(IDConductorEncontrado);
        ConductorEncontrado=false;
        IDConductorEncontrado="";
        RadioBusqueda=0.1f;
        Buscandoa.setText("Buscando Conductor");
        ObtenerConductoresCerca();
    }

    private void ObtenerConductoresCerca(){
        geofireProvider.ConductoresActivos(origenLatlgn,RadioBusqueda).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("COnductor","ID "+key);
                Buscandoa.setText("Buscando Conductor");
                mConductoresFounList.add(key);

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
//                finaliza la busqyeda en un radios de 10 kilometros
                ValidarConductorDisponible();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private void  ObtenerConducotresToken(){
        if (mConductoresFounList.size()==0){
            ObtenerConductoresCerca();
            return;
        }
        Buscandoa.setText("Esperando Respuesta");
        for (String id: mConductoresFounList){
            tokenProvider.getToken(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mCounter=mCounter+1;
                    if (snapshot.exists()){
                        String token=snapshot.child("token").getValue().toString();
                        mTokenList.add(token);
                    }
                    if (mCounter==mConductoresFounList.size()){
                            EnviarNotificacion("","");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void  ValidarConductorDisponible(){
        for (String idConductor: mConductoresFounList){
            conductoresEncontradosProvider.ObtnerConductorEncontradoByID(idConductor).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mCounterConductoresDisponibles=mCounterConductoresDisponibles+1;
                    for (DataSnapshot d: snapshot.getChildren()){
                        if (d.exists()){
                            String idConductor=d.child("idConductor").getValue().toString();
                            //elimino de la lista de conductores encontrados el conductor que ya existe en el node CONductoresEncontrados
//                            para no enviarle la notificacion
                            mConductoresFounList.remove(idConductor);
                            mCounterConductoresDisponibles=mCounterConductoresDisponibles-1;

                        }
                    }
                    //la consulta ya termino
                    //nos aseguramos de no enviarle la notificaciones a los conductores que yta estan recibiendo la notificacion
                    if (mCounterConductoresDisponibles==mConductoresFounList.size()){
                        ObtenerConducotresToken();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

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
                    Distancia=distancia.getString("text");
                    Duracion=duracion.getString("text");

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
                    Map<String,String> map=new HashMap<>();
                    map.put("title","SOLICITUD DE SERVICIO ");
                    map.put("body",
                            "Un Cliente esta solicitando un servicio "
                            +"\n"+"Recoger en: "+mExtraOrigen+"\n"+
                                    "Destino: " +mExtraDestino+"\n"+
                                    "Distancia"+distancia+
                                    "Precio: "+precio
                    );
                    map.put("idCliente",authProvider.getId());
                    map.put("origen",mExtraOrigen);
                    map.put("destino",mExtraDestino);
                    map.put("tiempo",tiempo);
                    map.put("distancia",distancia);
                    map.put("searchById", "false");
                    map.put("precio", String.valueOf(precio));
                    FCMBody fcmBody=new FCMBody(mTokenList,"high","450s",map);

                    notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                            ClientBooking clientBooking=new ClientBooking(
                                    authProvider.getId(),
                                    "",
                                    mExtraDestino,
                                    mExtraOrigen,
                                    Tiempo,
                                    precio,
                                    DistanciaKm,
                                    "Creado",
                                    mExtraLat,
                                    mExtraLon,
                                    mExtraDestinoLat,
                                    mExtraDestinoLon


                            );
//                            recorremos la lista de conductores encontrados para almacenarlos en fireba
                            for (String idConductor: mConductoresFounList){
                                ConductorEncontrado  conductorEncontrado=new ConductorEncontrado(idConductor,authProvider.getId());
                                conductoresEncontradosProvider.Crear(conductorEncontrado);

                            }
                            clienteReservaProvider.Crear(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mHandler.postDelayed(mrunnable,1000);
                                    RevisarEstadoSolicitud();
                                }
                            });
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
    private void EnviarNotificacionCancelacionAConductores(String idConductore){
        if (mTokenList.size()>0){
            Map<String,String> map=new HashMap<>();
            map.put("title","VIAJE CANCELADO");
            map.put("body",
                    "El cliente cancelo la solicitud"
            );
            //elimianr de la lista de token el token que acepto el viaje
            mTokenList.remove(idConductore);

            FCMBody fcmBody=new FCMBody(mTokenList,"high","50s",map);
            notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {
                    Log.d("ErrorKisamado","Error 2527: "+t.getMessage());

                }
            });
        }


    }

    private void EnviarNotificacionCancelacion(){
        if (mTokenList.size()>0){
            Map<String,String> map=new HashMap<>();
            map.put("title","VIAJE CANCELADO");
            map.put("body",
                    "El cliente cancelo la solicitud"
            );
            FCMBody fcmBody=new FCMBody(mTokenList,"high","450s",map);
            notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                    Toast.makeText(SoliciarConductorActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(SoliciarConductorActivity.this,MapClienteActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {
                    Log.d("ErrorKisamado","Error 2527: "+t.getMessage());

                }
            });
        }

        else {
            Toast.makeText(SoliciarConductorActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(SoliciarConductorActivity.this,MapClienteActivity.class);
            startActivity(intent);
            finish();
        }

    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mlistener !=null){
            clienteReservaProvider.getEstado(authProvider.getId()).removeEventListener(mlistener);
        }
        if (mHandler!=null){
            mHandler.removeCallbacks(mrunnable);
        }
        busquedafinalizada=true;
    }
}