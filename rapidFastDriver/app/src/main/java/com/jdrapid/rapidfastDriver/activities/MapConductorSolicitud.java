package com.jdrapid.rapidfastDriver.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.models.FCMBody;
import com.jdrapid.rapidfastDriver.models.FCMResponse;
import com.jdrapid.rapidfastDriver.models.Info;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.ClienteProvider;
import com.jdrapid.rapidfastDriver.providers.ClienteReservaProvider;
import com.jdrapid.rapidfastDriver.providers.GeofireProvider;
import com.jdrapid.rapidfastDriver.providers.GoogleApiProvider;
import com.jdrapid.rapidfastDriver.providers.InfoProvider;
import com.jdrapid.rapidfastDriver.providers.NotificationProvider;
import com.jdrapid.rapidfastDriver.providers.TokenProvider;
import com.jdrapid.rapidfastDriver.utils.DecodePoints;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapConductorSolicitud extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap nMap;
    private SupportMapFragment mapFragment;
    private AuthProvider authProvider;
    private GeofireProvider geofireProvider;
    //    token
    private TokenProvider tokenProvider;

    //    ubicacion
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker marker;
    //    base de datos para guardar ubicacion
    private LatLng latLngUbicacionActual;
    private ValueEventListener mlistener;

    //    textview
    private TextView txtNombreCliente, txtEmailCliente;
    private TextView txtOrigenCliente, txtDestinoCliente, txtTiempo;
    private ImageView FotoClientesolicitud;
    private String mExtraClienteId;
    //    provider del cleinte
    private ClienteProvider clienteProvider;
    private ClienteReservaProvider clienteReservaProvider;

    //    poligonos
    private LatLng mOriginLatlng, mDestinoLatlng;
    private GoogleApiProvider googleApiProvider;
    private List<LatLng> listaPoligonos;
    private PolylineOptions polylineOptions;
    private Button btnInicarViaje, BtnFinalizarViaje;
    private boolean esPrimerVez = true;
    private boolean cercaALCLiente = false;

    //    notificaciones
    private NotificationProvider notificationProvider;
    private InfoProvider infoProvider;
    private Info info;

    double distanciametros = 1;
    boolean segundosterminados = false;
    int minutos = 0;
    int segundos = 0;
    Handler mhandler = new Handler();
    boolean comenzoViaje = false;
    Location ubicacionAnterior = new Location("");


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            segundos++;
            if (!segundosterminados) {
                txtTiempo.setText(segundos + " seg");
            } else {
                txtTiempo.setText(minutos + " min " + segundos);
            }
            if (segundos == 59) {
                segundos = 0;
                segundosterminados = true;
                minutos++;
            }
            mhandler.postDelayed(runnable, 1000);
        }
    };
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    latLngUbicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
                    if (marker != null) {
                        marker.remove();
                    }
                    if (comenzoViaje) {
                        distanciametros = distanciametros + ubicacionAnterior.distanceTo(location);
                        Log.d("Distancia ", "onLocationResult: " + distanciametros);
                    }

                    ubicacionAnterior = location;
                    marker = nMap.addMarker(new MarkerOptions().position(new LatLng(
                                    location.getLatitude(), location.getLongitude())
                            ).title("Mi Ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                    );
//                    obtener la ubicacion del usuario en tiempo real
                    nMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f).build()

                    ));
                    ActualizarUbicacion();

                    if (esPrimerVez) {
                        esPrimerVez = false;
                        obtenerClienteSolicitudInfo();
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_conductor_solicitud);

        authProvider = new AuthProvider();
        geofireProvider = new GeofireProvider("Conductores_trabajando");
        tokenProvider = new TokenProvider();

        clienteProvider = new ClienteProvider();

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtNombreCliente = findViewById(R.id.txtclientenombne);
        txtEmailCliente = findViewById(R.id.txtemailcliente);

        txtOrigenCliente = findViewById(R.id.txtclienteOrigen);
        txtDestinoCliente = findViewById(R.id.txtclienteDestino);
        txtTiempo = findViewById(R.id.texttiempo);
        btnInicarViaje = findViewById(R.id.btnInicarViaje);
        BtnFinalizarViaje = findViewById(R.id.btnFinalizarViaje);
        FotoClientesolicitud = findViewById(R.id.fotoCLienteBooking);

        clienteReservaProvider = new ClienteReservaProvider();
        infoProvider = new InfoProvider();

        mExtraClienteId = getIntent().getStringExtra("idCliente");
//        dibujar mapa
        googleApiProvider = new GoogleApiProvider(MapConductorSolicitud.this);
        notificationProvider = new NotificationProvider();
        obtenerCliente();
        obtenerPrecio();

        btnInicarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cercaALCLiente) {
                    inicarBooking();
                } else {
                    Toast.makeText(MapConductorSolicitud.this, "Debes estar mas cerca al cliente", Toast.LENGTH_SHORT).show();
                }
            }
        });
        BtnFinalizarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizarBooking();
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void obtenerPrecioVijae() {
        if (minutos == 0) {
            minutos = 1;
        }
        double precioMin = minutos * info.getMin();
        double prekm = (distanciametros / 1000) * info.getKm();
        final double total = precioMin + prekm;
        clienteReservaProvider.actualizarPrecio(mExtraClienteId, total).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                clienteReservaProvider.actualizarEstado(mExtraClienteId, "Finalizar");
                Intent intent = new Intent(MapConductorSolicitud.this, CalificacionClienteActivity.class);
                intent.putExtra("idCliente", mExtraClienteId);
                intent.putExtra("precio", total);
                startActivity(intent);
                finish();
            }
        });

        Log.d("Precio", "obtenerPrecioVijae: " + precioMin);
        Log.d("Precio", "obtenerPrecioVijae: " + minutos);
        Log.d("Precio", "obtenerPrecioVijae: " + distanciametros / 1000);
        Log.d("Precio", "obtenerPrecioVijae: " + prekm);
        Log.d("Precio", "obtenerPrecioVijae: " + total);


    }

    private void finalizarBooking() {
        clienteReservaProvider.actualizarHistoryBooking(mExtraClienteId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                EnviarNotificacion("Viaje Finalizado");
                if (fusedLocation != null) {
                    fusedLocation.removeLocationUpdates(locationCallback);
                }
                if (mhandler != null) {
                    mhandler.removeCallbacks(runnable);

                }
                geofireProvider.EliminarUbicacion(authProvider.getId());
                obtenerPrecioVijae();
            }
        });


    }

    private void inicarBooking() {
        clienteReservaProvider.actualizarEstado(mExtraClienteId, "Iniciar");
        btnInicarViaje.setVisibility(View.GONE);
        BtnFinalizarViaje.setVisibility(View.VISIBLE);
        nMap.clear();
        nMap.addMarker(new MarkerOptions().position(mDestinoLatlng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        DibujarRuta(mDestinoLatlng);
        EnviarNotificacion("Viaje Iniciado");
        comenzoViaje = true;
        mhandler.postDelayed(runnable, 1000);

    }

    private void obtenerPrecio() {
        infoProvider.getInfo().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    info = snapshot.getValue(Info.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double obtenerDistanciaConClien(LatLng ubicacionCliente, LatLng ubicacionConductor) {
        double distancia = 0;
        Location locationCleinte = new Location("");
        Location locationConductor = new Location("");
        locationCleinte.setLatitude(ubicacionCliente.latitude);
        locationCleinte.setLongitude(ubicacionCliente.longitude);
        locationConductor.setLatitude(ubicacionConductor.latitude);
        locationConductor.setLongitude(ubicacionConductor.longitude);
        distancia = locationCleinte.distanceTo(locationConductor);
        return distancia;

    }


    private void obtenerClienteSolicitudInfo() {
        clienteReservaProvider.getClienteSolicitud(mExtraClienteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String destino = snapshot.child("destino").getValue().toString();
                    String origen = snapshot.child("origen").getValue().toString();
                    double destinoLat = Double.parseDouble(snapshot.child("destinoLat").getValue().toString());
                    double destinoLon = Double.parseDouble(snapshot.child("destinoLong").getValue().toString());
                    double origenLat = Double.parseDouble(snapshot.child("origenLat").getValue().toString());
                    double origenLong = Double.parseDouble(snapshot.child("origenLong").getValue().toString());

                    mOriginLatlng = new LatLng(origenLat, origenLong);
                    mDestinoLatlng = new LatLng(destinoLat, destinoLon);
                    txtOrigenCliente.setText("Recoger en: " + origen);
                    txtDestinoCliente.setText("Destino: " + destino);
                    nMap.addMarker(new MarkerOptions().position(mOriginLatlng).title("Recoger Aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                    DibujarRuta(mOriginLatlng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void obtenerCliente() {
        clienteProvider.getCliente(mExtraClienteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                    String email = snapshot.child("correo").getValue().toString();
                    String nombre = snapshot.child("Nombre").getValue().toString();
                    String imagen = "";
                    if (snapshot.hasChild("imagen")) {
                        imagen = snapshot.child("imagen").getValue().toString();
                        Picasso.with(MapConductorSolicitud.this).load(imagen).into(FotoClientesolicitud);
                    }
                    txtNombreCliente.setText(nombre);
                    txtEmailCliente.setText("kakaroto");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DibujarRuta(LatLng latLng) {
        googleApiProvider.getDirecciones(latLngUbicacionActual, latLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject ruta = jsonArray.getJSONObject(0);
                    JSONObject poligonos = ruta.getJSONObject("overview_polyline");
                    String puntos = poligonos.getString("points");
                    listaPoligonos = DecodePoints.decodePoly(puntos);
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.width(20f);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.jointType(JointType.ROUND);
                    polylineOptions.addAll(listaPoligonos);
                    nMap.addPolyline(polylineOptions);

//                    para obtener el tiempo de la api ejemplo: https://maps.googleapis.com/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&origin=1.2034717,-77.2922318&destination=1.2039062,-77.2927302&departure_time=1603143184104&traffic_model=best_guess&key=AIzaSyAKltRdYeGz-VViXHEaYu00KR7dWirLdv8
                    JSONArray legs = ruta.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distancia = leg.getJSONObject("distance");
                    JSONObject duracion = leg.getJSONObject("duration");

//                    obtener el string de la api

                    String Distancia = distancia.getString("text");
                    String Duracion = duracion.getString("text");


                } catch (Exception e) {
                    Log.d("Error", "Jiren: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void ActualizarUbicacion() {
        if (authProvider.existeSesion() && latLngUbicacionActual != null) {
            geofireProvider.guardarUbicacion(authProvider.getId(), latLngUbicacionActual);
            if (!cercaALCLiente) {
                if (mOriginLatlng != null && latLngUbicacionActual != null) {
                    double distancia = obtenerDistanciaConClien(mOriginLatlng, latLngUbicacionActual);
                    if (distancia <= 300) {
                        btnInicarViaje.setEnabled(true);
                        cercaALCLiente = true;
                        int distanciacon = (int) distancia;
                        Toast.makeText(this, "Esta Cerca a la posicion del cliente estas a " + distanciacon + " metros", Toast.LENGTH_SHORT).show();
                    }

                }


            }

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivado()) {
                        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        nMap.setMyLocationEnabled(true);

                    } else {
                        ShowAlertGPS();
                    }


                } else {
                    checkLocationPermmisions();
                }
            } else {
                checkLocationPermmisions();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivado()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                return;
            }
            fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        }else {
            ShowAlertGPS();
        }
    }

    private void ShowAlertGPS(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActivado(){
        boolean estaActivo=false;
        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            estaActivo=true;
        }else {
            estaActivo=false;
        }
        return estaActivo;
    }


    private void desconectado(){

        if (fusedLocation !=null){

            fusedLocation.removeLocationUpdates(locationCallback);
            if (authProvider.existeSesion()) {

                geofireProvider.EliminarUbicacion(authProvider.getId());
            }
        }else {
            Toast.makeText(this,"No se puede desconectar",Toast.LENGTH_LONG).show();
        }
    }
    private void startLocation(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                if (gpsActivado()){
                    fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    nMap.setMyLocationEnabled(true);
                }else {
                    ShowAlertGPS();
                }

            }else {
                checkLocationPermmisions();
            }

        }else {
            if (gpsActivado()){
                fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                nMap.setMyLocationEnabled(true);
            }else {
                ShowAlertGPS();
            }

        }
    }
    private void checkLocationPermmisions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos para utilizarse").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MapConductorSolicitud.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                    }
                }).create().show();
            }else {
                ActivityCompat.requestPermissions(MapConductorSolicitud.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }

    private void EnviarNotificacion(String estado) {
        tokenProvider.getToken(mExtraClienteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String token=dataSnapshot.child("token").getValue().toString();
                    Map<String,String> map=new HashMap<>();
                    map.put("title","ESTADO DE TU VIAJE");
                    map.put("body",
                            "Tu estado del viaje es: "+estado);
                    FCMBody fcmBody=new FCMBody(token,"high","4500s",map);

                    notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body()!=null){
                                if (response.body().getSuccess()!=1){
                                    Toast.makeText(MapConductorSolicitud.this, "No se pudo enviar notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(MapConductorSolicitud.this, "No se pudo enviar notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("ErrorKisamado","Error 2527: "+t.getMessage());

                        }
                    });
                }else {
                    Toast.makeText(MapConductorSolicitud.this, "No se pudo enviar la notificacion por que el conductor se desconecto", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}