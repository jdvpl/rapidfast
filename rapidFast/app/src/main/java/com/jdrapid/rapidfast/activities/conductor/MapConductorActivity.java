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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.MainActivity;
import com.jdrapid.rapidfast.activities.cliente.ActualizarPerfilActivity;
import com.jdrapid.rapidfast.activities.cliente.HistorialSolicitudClienteActivity;
import com.jdrapid.rapidfast.activities.cliente.MapClienteActivity;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;

public class MapConductorActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    private Button BtnConectarse;
    private boolean estaConectado =false;
//    base de datos para guardar ubicacion
    private  LatLng latLngUbicacionActual;
    private ValueEventListener mlistener;
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
                    ConductorTrabajando();

                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_conductor);
        authProvider = new AuthProvider();

        ToolBar.mostrar(this, "Rapidfast Driver", false);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        BtnConectarse=findViewById(R.id.btnConect);
        geofireProvider=new GeofireProvider("Conductores_Activos");
        tokenProvider=new TokenProvider();

        BtnConectarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estaConectado){
                    desconectado();
                }else{
                    startLocation();
                }
            }
        });
        GenerarToken();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationCallback !=null && fusedLocation!=null){
            fusedLocation.removeLocationUpdates(locationCallback);
        }
        if (mlistener != null){
            if (authProvider.existeSesion()){
                geofireProvider.ConductoresTrabajando(authProvider.getId()).removeEventListener(mlistener);
            }
        }
    }

    private void ConductorTrabajando() {
        mlistener=geofireProvider.ConductoresTrabajando(authProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    desconectado();
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
        nMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        nMap.getUiSettings().setZoomControlsEnabled(true);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivado()){
                        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

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
            BtnConectarse.setText("Conectarse");
            estaConectado=false;
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
                    BtnConectarse.setText("Desconectarse");
                    estaConectado=true;
                    fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                }else {
                    ShowAlertGPS();
                }

            }else {
                checkLocationPermmisions();
            }

        }else {
            if (gpsActivado()){
                fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
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
                        ActivityCompat.requestPermissions(MapConductorActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                    }
                }).create().show();
            }else {
                ActivityCompat.requestPermissions(MapConductorActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conductor_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            logout();
        }
        if (item.getItemId() == R.id.actualizarPerfilConductor){
            Intent intent=new Intent(MapConductorActivity.this, ActualizarPerfilConductor.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.historialViajesConductor){
            Intent intent=new Intent(MapConductorActivity.this, HistorialSolicitudConductorActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    void logout(){
        tokenProvider.deleteToken(authProvider.getId());
        desconectado();
        authProvider.logout();
        Intent intent=new Intent(MapConductorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    void GenerarToken(){
        tokenProvider.Crear(authProvider.getId());
    }
}