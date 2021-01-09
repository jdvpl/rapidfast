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
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.includes.ToolBar;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.ConductoresEncontradosProvider;
import com.jdrapid.rapidfastDriver.providers.GeofireProvider;
import com.jdrapid.rapidfastDriver.providers.TokenProvider;
import com.jdrapid.rapidfastDriver.services.ForegroundService;
import com.jdrapid.rapidfastDriver.utils.CarMoveAnim;

public class MapConductorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap nMap;
    private SupportMapFragment mapFragment;
    private AuthProvider authProvider;
    private GeofireProvider geofireProvider;
    private ConductoresEncontradosProvider conductoresEncontradosProvider;
    //    token
    private TokenProvider tokenProvider;

    //    ubicacion
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker marker;

    private Button BtnConectarse;
    private boolean estaConectado = false;
    //    base de datos para guardar ubicacion
    private LatLng latLngUbicacionActual;
    private ValueEventListener mlistener;
    private boolean mExtraConectado;

    SharedPreferences mPref;

    private GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 0x1;

    private boolean mIsStartLocation = false;
    LatLng mStartLatlng, mEndLatlng;
    LocationManager mLocationManager;

    LocationListener locationListenerGps = new LocationListener() {
        private Location location;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            latLngUbicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
            if (mStartLatlng != null) {
                mEndLatlng = mStartLatlng;
            }
            mStartLatlng = new LatLng(latLngUbicacionActual.latitude, latLngUbicacionActual.longitude);
            if (mEndLatlng != null) {
                CarMoveAnim.carAnim(marker, mEndLatlng, mStartLatlng);
            }
            nMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(18f).build()

            ));
            ActualizarUbicacion();
        }
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
//                    ya reconocion la ubicacion por primera vez
                    if (!mIsStartLocation) {
                        nMap.clear();

                        latLngUbicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
                        mIsStartLocation = true;

                        nMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(16f).build()

                        ));

                        marker = nMap.addMarker(new MarkerOptions().position(new LatLng(
                                        location.getLatitude(), location.getLongitude())
                                ).title("Mi Ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.carrorappid))
                        );

                        ActualizarUbicacion();
                        if (ActivityCompat.checkSelfPermission(MapConductorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapConductorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                                10, locationListenerGps);
                        stopLocation();

                    }


                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_conductor);
        ToolBar.mostrar(this, "Rapidfast Driver", false);

        authProvider = new AuthProvider();
        geofireProvider = new GeofireProvider("Conductores_Activos");
        tokenProvider = new TokenProvider();
        conductoresEncontradosProvider = new ConductoresEncontradosProvider();

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mExtraConectado = getIntent().getBooleanExtra("Conectado", false);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        BtnConectarse = findViewById(R.id.btnConect);
        googleApiClient = getAPIClienteInstance();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        BtnConectarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estaConectado) {
                    desconectado();
                } else {
                    startLocation();
                }
            }
        });
        mPref = getApplicationContext().getSharedPreferences("RideStatus", MODE_PRIVATE);
        String status = mPref.getString("status", "");
        String idCLiente = mPref.getString("idCliente", "");

        if (status.equals("Iniciar") || status.equals("ride")) {
            iraMapCondcutorActivity(idCLiente);
        } else {

            GenerarToken();
            BorrarConductorTrabajando();
            BorrarConductorEncontrado();
        } GenerarToken();
        BorrarConductorTrabajando();
        BorrarConductorEncontrado();



    }


    private GoogleApiClient getAPIClienteInstance() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
        return googleApiClient;
    }

    private void requestGPSSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.SUCCESS) {
                    Toast.makeText(MapConductorActivity.this, "El Gps ya esta avidado", Toast.LENGTH_SHORT).show();
                } else if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(MapConductorActivity.this, REQUEST_CHECK_SETTINGS);
                        if (ActivityCompat.checkSelfPermission(MapConductorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapConductorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    } catch (IntentSender.SendIntentException e) {
                        Toast.makeText(MapConductorActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    ;
                } else if (status.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    Toast.makeText(MapConductorActivity.this, "La configuracion del gps tiene un error o no esta disponible", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void iraMapCondcutorActivity(String idCLiente) {
        Intent intent = new Intent(MapConductorActivity.this, MapConductorSolicitud.class);
        intent.putExtra("idCliente", idCLiente);
        startActivity(intent);
    }

    private void BorrarConductorEncontrado() {
        conductoresEncontradosProvider.Borrar(authProvider.getId());
    }

    private void BorrarConductorTrabajando() {
        geofireProvider.BorrarConductoresTrabajando(authProvider.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ConductorTrabajando();
                if (mExtraConectado) {
                    startLocation();
                }
            }
        });
    }

    private void ValidarConductorActivo() {
        geofireProvider.obteneConductor(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    startLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void stopLocation() {
        if (locationCallback != null && fusedLocation != null) {
            fusedLocation.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
        removerUbicacion();
        if (mlistener != null) {
            if (authProvider.existeSesion()) {
                geofireProvider.ConductoresTrabajando(authProvider.getId()).removeEventListener(mlistener);
            }
        }
    }

    private void ConductorTrabajando() {
        mlistener = geofireProvider.ConductoresTrabajando(authProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    desconectado();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ActualizarUbicacion() {
        if (authProvider.existeSesion() && latLngUbicacionActual != null) {
            geofireProvider.guardarUbicacion(authProvider.getId(), latLngUbicacionActual);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.estilo_mapa));
        nMap.getUiSettings().setZoomControlsEnabled(true);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        ValidarConductorActivo();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivado()) {
                        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                    } else {
                        //ShowAlertGPS();
                        requestGPSSettings();

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
                return;
            }
            fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } else {
            //ShowAlertGPS();
            requestGPSSettings();

        }
    }

    private void ShowAlertGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActivado() {
        boolean estaActivo = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            estaActivo = true;
        } else {
            estaActivo = false;
        }
        return estaActivo;
    }

    private void desconectado() {
        removerUbicacion();
        BtnConectarse.setText("Conectarse");
        mIsStartLocation = false;
        estaConectado = false;
        //fusedLocation.removeLocationUpdates(locationCallback);
        geofireProvider.EliminarUbicacion(authProvider.getId());

    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActivado()) {
                    BtnConectarse.setText("Desconectarse");
                    estaConectado = true;
                    fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                } else {
                    //ShowAlertGPS();
                    requestGPSSettings();
                }

            } else {
                checkLocationPermmisions();
            }

        } else {
            if (gpsActivado()) {
                fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            } else {
//                ShowAlertGPS();
//                p[ara activarlo automaticvamente
                requestGPSSettings();

            }

        }
    }

    private void checkLocationPermmisions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos para utilizarse").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MapConductorActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                    }
                }).create().show();
            } else {
                ActivityCompat.requestPermissions(MapConductorActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conductor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        if (item.getItemId() == R.id.actualizarPerfilConductor) {
            Intent intent = new Intent(MapConductorActivity.this, ActualizarPerfilConductor.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.afiliados) {
            Intent intent = new Intent(MapConductorActivity.this, AfiliadosHistorial.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.historialViajesConductor) {
            Intent intent = new Intent(MapConductorActivity.this, HistorialSolicitudConductorActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    void logout() {
        tokenProvider.deleteToken(authProvider.getId());
        desconectado();
        authProvider.logout();
        Intent intent = new Intent(MapConductorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void GenerarToken() {
        tokenProvider.Crear(authProvider.getId());
    }

    private void removerUbicacion() {
        if (locationListenerGps != null) {
            mLocationManager.removeUpdates(locationListenerGps);
        }
    }


}