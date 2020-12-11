package com.jdrapid.rapidfast.activities.conductor;

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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;

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
    private TextView txtNombreCliente,txtEmailCliente;
    private String mExtraClienteId;
//    provider del cleinte
    private ClienteProvider clienteProvider;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    latLngUbicacionActual=new LatLng(location.getLatitude(),location.getLongitude());
                    if (marker !=null){
                        marker.remove();
                    }
                    marker=nMap.addMarker(new MarkerOptions().position(new LatLng(
                                    location.getLatitude(),location.getLongitude())
                            ).title("Mi Ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                    );
//                    obtener la ubicacion del usuario en tiempo real
                    nMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f).build()

                    ));
                    ActualizarUbicacion();

                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_conductor_solicitud);

        authProvider = new AuthProvider();
        geofireProvider=new GeofireProvider("Conductores_trabajando");
        tokenProvider=new TokenProvider();

        clienteProvider=new ClienteProvider();

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtNombreCliente=findViewById(R.id.txtclientenombne);
        txtEmailCliente=findViewById(R.id.txtemailcliente);

        mExtraClienteId=getIntent().getStringExtra("idCliente");
        obtenerClienteSolicitud();

    }

    private void obtenerClienteSolicitud() {
        clienteProvider.getCliente(mExtraClienteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String email=snapshot.child("Correo").getValue().toString();
                    String nombre=snapshot.child("Nombre").getValue().toString();

                    txtEmailCliente.setText(email);
                    Log.d("usuario", "onDataChange: "+email);
                    Log.d("usuario", "onDataChange: "+nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ActualizarUbicacion(){
        if (authProvider.existeSesion() && latLngUbicacionActual !=null){
            geofireProvider.guardarUbicacion(authProvider.getId(),latLngUbicacionActual);
        }

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        nMap.getUiSettings().setZoomControlsEnabled(true);


        nMap.setMyLocationEnabled(false);
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
                    if (gpsActivado()){
                        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        nMap.setMyLocationEnabled(true);

                    }else {
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

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivado()) {

            fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            nMap.setMyLocationEnabled(true);
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
    @SuppressLint("MissingPermission")
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
}