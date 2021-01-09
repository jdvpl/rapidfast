package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;

import com.jdrapid.rapidfast.models.FCMBody;
import com.jdrapid.rapidfast.models.FCMResponse;
import com.jdrapid.rapidfast.models.InfoCelular;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.ConductoresEncontradosProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.GoogleApiProvider;
import com.jdrapid.rapidfast.providers.InfoCelularProvider;
import com.jdrapid.rapidfast.providers.InfoProvider;
import com.jdrapid.rapidfast.providers.NotificationProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;
import com.jdrapid.rapidfast.utils.CarMoveAnim;
import com.jdrapid.rapidfast.utils.DecodePoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClienteReservaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap nMap;
    private SupportMapFragment mapFragment;
    private AuthProvider authProvider;
    //    token
    private TokenProvider tokenProvider;
    private ClienteReservaProvider clienteReservaProvider;
    private ConductorProvider conductorProvider;

    private GeofireProvider geofireProvider;
    private FusedLocationProviderClient fusedLocation;
    private LocationRequest locationRequest;
    //    ubicacion y recogida del cliente
    private String mOrigin,mDestino;
    private LatLng mOriginLtg,mDestinoLtg;
    private LatLng mDriverLatlng;

    private Marker markerConductor;
    private boolean esPrimerVez=true;

//    textview
    private TextView txtNombreConductor,TxtMarca,TxtPlaca;
    private TextView txtOrigenConductor,txtDestinoConductor,txtEstadoSolicitud;
    private ImageView FotoConductorsolicitud;

    private Button BtnCancelar,BtnLlamarConductor,BtnLlamarSoporte,BtnLlamarPolicia,BtnCancelarTodo;
    private String TelefonoCOnductor,TelefonoSoporte,TelefonoPolicia;

    private LatLng mOriginLatlng,mDestinoLatlng;
    private GoogleApiProvider googleApiProvider;
    private List<LatLng> listaPoligonos;
    private PolylineOptions polylineOptions;
    private ValueEventListener valueEventListener;
    private String midCondutor;
    private ValueEventListener lsitenerEstado;

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    LatLng mStartLatlng,mEndLatlng;

    private InfoCelularProvider infoCelularProvider;
    private ArrayList<String> mConductoresFounList=new ArrayList<>();
    private ConductoresEncontradosProvider conductoresEncontradosProvider;
    private NotificationProvider notificationProvider;


    private List<String> mTokenList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map_cliente_reserva);

        authProvider = new AuthProvider();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofireProvider=new GeofireProvider("Conductores_trabajando");
        tokenProvider=new TokenProvider();
        clienteReservaProvider=new ClienteReservaProvider();
        googleApiProvider=new GoogleApiProvider(MapClienteReservaActivity.this);
        conductorProvider=new ConductorProvider();
        infoCelularProvider=new InfoCelularProvider();
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
//        inicializqar los txtviews
        txtNombreConductor=findViewById(R.id.txtconductornombne);
        TxtMarca=findViewById(R.id.txtmarcaconductor);
        TxtPlaca=findViewById(R.id.txtplacaconductor);
        txtEstadoSolicitud=findViewById(R.id.txtEstadoSolicitud);
        txtOrigenConductor=findViewById(R.id.txtConductorOrigen);
        txtDestinoConductor=findViewById(R.id.txtConductorDestino);
        FotoConductorsolicitud=findViewById(R.id.fotoConductorBooking);
        BtnCancelar=findViewById(R.id.btnCancelar);
        BtnLlamarConductor=findViewById(R.id.btnLlamarConductor);
        BtnLlamarSoporte=findViewById(R.id.btnLamarSoporte);
        BtnLlamarPolicia=findViewById(R.id.btnLamarPolicia);
        BtnCancelarTodo=findViewById(R.id.btnCancelartodo);

        conductoresEncontradosProvider=new ConductoresEncontradosProvider();
        notificationProvider=new NotificationProvider();


        midCondutor=getIntent().getStringExtra("idConductor");
        mPref=getApplicationContext().getSharedPreferences("RideSatus",MODE_PRIVATE);
        mEditor=mPref.edit();
        ObtenerCelulares();

        BtnCancelarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapClienteReservaActivity.this,MapClienteActivity.class);
                startActivity(intent);
                finish();
                mEditor.clear().commit();
                BorrarConductoresEncontrados();
                CancelarSolicitud();
            }
        });
        BtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(MapClienteReservaActivity.this,MapClienteReservaActivity.class);
//                startActivity(intent);

                startActivity(getIntent());
            }
        });

        BtnLlamarConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MapClienteReservaActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapClienteReservaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }
                if (TelefonoCOnductor.equals("")){
                    Toast.makeText(MapClienteReservaActivity.this, "El Conductor no tiene un numero de telefono asociado", Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + TelefonoCOnductor));
                    startActivity(intent);
                }

            }
        });
        BtnLlamarSoporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MapClienteReservaActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapClienteReservaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 102);
                    return;
                }
                if (TelefonoSoporte.equals("")){
                    Toast.makeText(MapClienteReservaActivity.this, "El Conductor no tiene un numero de telefono asociado", Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + TelefonoSoporte));
                    startActivity(intent);
                }

            }
        });

        BtnLlamarPolicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MapClienteReservaActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapClienteReservaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 103);
                    return;
                }
                if (TelefonoPolicia.equals("")){
                    Toast.makeText(MapClienteReservaActivity.this, "El Conductor no tiene un numero de telefono asociado", Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + TelefonoPolicia));
                    startActivity(intent);
                }

            }
        });
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }
        obtenerEstado();
        obtenerClienteSolicitudInfo();

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
                    Toast.makeText(MapClienteReservaActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MapClienteReservaActivity.this,MapClienteActivity.class);
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
            Toast.makeText(MapClienteReservaActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MapClienteReservaActivity.this,MapClienteActivity.class);
            startActivity(intent);
            finish();
        }

    }



    private void ObtenerCelulares(){
        infoCelularProvider.getInfo().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    InfoCelular infoCelular=snapshot.getValue(InfoCelular.class);
                    String soporte=infoCelular.getSoporteCel();
                    String policia=infoCelular.getPoliciaCel();

                    if (!soporte.equals("")){
                        TelefonoSoporte=soporte;
                    }
                    if (!policia.equals("")){
                        TelefonoPolicia=policia;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void obtenerEstado() {
        lsitenerEstado= clienteReservaProvider.getEstado(authProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String estado=snapshot.getValue().toString();

                    if (estado.equals("Aceptado")){

                        txtEstadoSolicitud.setText("Viaje : Aceptado");
                    }
                    if (estado.equals("Iniciar")){
                        txtEstadoSolicitud.setText("Viaje : Iniciado");
                        String mStatuspref=mPref.getString("status","");
                        if (!mStatuspref.equals("Iniciar")){
                            comenzarSolicitud();
                        }
                    }else if (estado.equals("Finalizar")){
                        txtEstadoSolicitud.setText("Viaje : Finalizado");
                        terminarSolicitud();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void terminarSolicitud() {
        mEditor.clear().commit();
        Intent intent=new Intent(MapClienteReservaActivity.this,CalificarConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void comenzarSolicitud() {
        mEditor.putString("status","Iniciar");
        mEditor.putString("idConductor",midCondutor);
        mEditor.apply();

        nMap.clear();
        nMap.addMarker(new MarkerOptions().position(mDestinoLatlng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

        if (mDriverLatlng !=null){
            markerConductor=nMap.addMarker(new MarkerOptions().position(new LatLng(mDriverLatlng.latitude,mDestinoLatlng.longitude)
            ).title("Mi Conductor").icon(BitmapDescriptorFactory.fromResource(R.drawable.carrorappid)));
        }
        DibujarRuta(mDestinoLatlng);
    }

    private void obtenerClienteSolicitudInfo() {
        clienteReservaProvider.getClienteSolicitud(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String destino=snapshot.child("destino").getValue().toString();
                    String origen=snapshot.child("origen").getValue().toString();
                    String idConductor=snapshot.child("idConductor").getValue().toString();

                    midCondutor=idConductor;
                    double destinoLat= Double.parseDouble(snapshot.child("destinoLat").getValue().toString());
                    double destinoLon= Double.parseDouble(snapshot.child("destinoLong").getValue().toString());
                    double origenLat= Double.parseDouble(snapshot.child("origenLat").getValue().toString());
                    double origenLong= Double.parseDouble(snapshot.child("origenLong").getValue().toString());

                    mOriginLatlng=new LatLng(origenLat,origenLong);
                    mDestinoLatlng=new LatLng(destinoLat,destinoLon);
                    txtOrigenConductor.setText("Recoger en: ".toUpperCase()+origen.toUpperCase());
                    txtDestinoConductor.setText("Destino: ".toUpperCase()+destino.toUpperCase());
                    nMap.addMarker(new MarkerOptions().position(mOriginLatlng).title("Recoger Aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                    obtenerConductor(idConductor);
                    ObtenerUbicacionConductor(idConductor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void obtenerConductor(String idConductor) {
        conductorProvider.getConductor(idConductor).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre=snapshot.child("nombre").getValue().toString();
                    String marca=snapshot.child("marcaVehiculo").getValue().toString();
                    String placa=snapshot.child("placaVehiculo").getValue().toString();

                    String telefono=snapshot.child("telefono").getValue().toString();
                    TelefonoCOnductor=telefono;
                    String imagen="";
                    if (snapshot.hasChild("imagen")){
                        imagen=snapshot.child("imagen").getValue().toString();
                        Picasso.with(MapClienteReservaActivity.this).load(imagen).into(FotoConductorsolicitud);
                    }
                    txtNombreConductor.setText(nombre.toUpperCase());
                    TxtMarca.setText(marca.toUpperCase());
                    TxtPlaca.setText(placa.toUpperCase());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ObtenerUbicacionConductor(String idCondutor) {
        valueEventListener=geofireProvider.obtenerUbicacionConductor(idCondutor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double lat= Double.parseDouble(snapshot.child("0").getValue().toString());
                    double lon= Double.parseDouble(snapshot.child("1").getValue().toString());
                    mDriverLatlng=new LatLng(lat,lon);

                    if (esPrimerVez){

                        markerConductor=nMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)
                        ).title("Mi Conductor").icon(BitmapDescriptorFactory.fromResource(R.drawable.carrorappid)));

                        esPrimerVez=false;
                        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder().target(mDriverLatlng).zoom(18f).build()
                        ));
                        String status=mPref.getString("status","");

                        if (status.equals("Iniciar")){
                            comenzarSolicitud();
                        }

                        else {
                            mEditor.putString("status","ride");
                            mEditor.putString("idConductor",midCondutor);
                            mEditor.apply();

                            DibujarRuta(mOriginLatlng);

                        }
                    }

                    if (mStartLatlng!=null){
                        mEndLatlng=mStartLatlng;

                    }
                    mStartLatlng=new LatLng(lat,lon);

                    if (mEndLatlng !=null){
                        CarMoveAnim.carAnim(markerConductor,mEndLatlng,mStartLatlng);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DibujarRuta(LatLng latLng){

        googleApiProvider.getDirecciones(mDriverLatlng,latLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject=new JSONObject(response.body());
                    JSONArray jsonArray=jsonObject.getJSONArray("routes");
                    JSONObject ruta=jsonArray.getJSONObject(0);
                    JSONObject poligonos=ruta.getJSONObject("overview_polyline");
                    String puntos=poligonos.getString("points");
                    listaPoligonos= DecodePoints.decodePoly(puntos);
                    polylineOptions=new PolylineOptions();
                    polylineOptions.color(Color.BLACK);
                    polylineOptions.width(16f);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.jointType(JointType.ROUND);
                    polylineOptions.addAll(listaPoligonos);
                    nMap.addPolyline(polylineOptions);

//                    para obtener el tiempo de la api ejemplo:
//                    https://maps.googleapis.com/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&origin=1.2034717,-77.2922318&destination=1.2039062,-77.2927302&departure_time=1603143184104&traffic_model=best_guess&key=AIzaSyAKltRdYeGz-VViXHEaYu00KR7dWirLdv8
                    JSONArray legs=ruta.getJSONArray("legs");
                    JSONObject leg=legs.getJSONObject(0);
                    JSONObject distancia=leg.getJSONObject("distance");
                    JSONObject duracion=leg.getJSONObject("duration");



//                    obtener el string de la api

                    String Distancia=distancia.getString("text");
                    String Duracion=duracion.getString("text");


                }catch (Exception e){
                    Log.d("Error","Jiren: "+e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener!=null){
            geofireProvider.obtenerUbicacionConductor(midCondutor).removeEventListener(valueEventListener);
        }
        if (lsitenerEstado !=null){
            clienteReservaProvider.getEstado(authProvider.getId()).removeEventListener(lsitenerEstado);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.estilo_mapa));
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
    }
}