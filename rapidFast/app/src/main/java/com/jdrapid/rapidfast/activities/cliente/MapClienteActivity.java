package com.jdrapid.rapidfast.activities.cliente;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.MainActivity;
import com.jdrapid.rapidfast.activities.conductor.MapConductorActivity;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.NotificationProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapClienteActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap nMap;
    private SupportMapFragment mapFragment;

    private AuthProvider authProvider;
    //    ubicacion
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker marker;
    private GeofireProvider geofireProvider;
    private  LatLng latLngUbicacionActual;
    List<Marker> markersConductores=new ArrayList<>();
    private boolean esPrimerVez=true;
//    para el el fragmente buscador
    private AutocompleteSupportFragment mautocomplete,mautocompleteDestino;
    private PlacesClient placesClient;
//    ubicacion y recogida del cliente
    private String mOrigin,mDestino;
    private LatLng mOriginLtg,mDestinoLtg;
//    camera listener
    private GoogleMap.OnCameraIdleListener cameraIdleListener;
//    bton solicitasr
    private Button BtnSolcitarViaje;
//    token
    private TokenProvider tokenProvider;


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    latLngUbicacionActual=new LatLng(location.getLatitude(),location.getLongitude());

//                    if (marker !=null){
//                        marker.remove();
//                    }
//
//                    marker=nMap.addMarker(new MarkerOptions().position(new LatLng(
//                                    location.getLatitude(),location.getLongitude())
//                            ).title("Mi Ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
//                    );
//                    obtener la ubicacion del usuario en tiempo real
                    nMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(17f).build()

                    ));
                    if (esPrimerVez){
                        esPrimerVez=false;
                        MostrarConductoresActivos();
                        LimiteBusqueda();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cliente);
        authProvider = new AuthProvider();

        ToolBar.mostrar(this, "Cliente", false);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofireProvider=new GeofireProvider("Conductores_Activos");
        tokenProvider=new TokenProvider();

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        BtnSolcitarViaje=findViewById(R.id.BtnSolicitarViaje);



        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }
        placesClient=Places.createClient(this);
        InstanceAutocompleteOrigen();
        InstanceAutocompleteDestino();
        OnCameraMoved();

        BtnSolcitarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PedirConductor();
            }
        });


        GenerarToken();

    }

    private void PedirConductor() {
        if (mOriginLtg!=null &&mDestinoLtg!=null) {
            Intent intent=new Intent(MapClienteActivity.this,DetallePedirConductor.class);
            intent.putExtra("origin_lat",mOriginLtg.latitude);
            intent.putExtra("origin_lon",mOriginLtg.longitude);
            intent.putExtra("destino_lat",mDestinoLtg.latitude);
            intent.putExtra("destino_lon",mDestinoLtg.longitude);
            intent.putExtra("Origen",mOrigin);
            intent.putExtra("Destino",mDestino);
            startActivity(intent);
        }else {
            Toast.makeText(this,"Debe seleccionar el lugar de recogida y destino",Toast.LENGTH_LONG).show();
        }
    }

    private void LimiteBusqueda(){
        LatLng northSide = SphericalUtil.computeOffset(latLngUbicacionActual, 20000,0);
        LatLng southSide = SphericalUtil.computeOffset(latLngUbicacionActual, 20000,180);
        mautocomplete.setCountry("COL");
        mautocomplete.setLocationBias(RectangularBounds.newInstance(southSide,northSide));
        mautocompleteDestino.setCountry("COL");
        mautocompleteDestino.setLocationBias(RectangularBounds.newInstance(southSide,northSide));
    }

    private void OnCameraMoved(){
        cameraIdleListener=new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder=new Geocoder(MapClienteActivity.this);
                    mOriginLtg=nMap.getCameraPosition().target;
                    List<Address> addressList=geocoder.getFromLocation(mOriginLtg.latitude,mOriginLtg.longitude,1);
                    String ciudad=addressList.get(0).getLocality();
                    String pais=addressList.get(0).getCountryName();
                    String direccion=addressList.get(0).getAddressLine(0);
                    mOrigin=direccion+" "+ciudad;
                    mautocomplete.setText(direccion+" "+ciudad);
                }catch (Exception e){
                    Log.d("Error: "," Mensaje Eror"+e.getMessage());
                }
            }
        };
    }
    private void InstanceAutocompleteOrigen(){


        mautocomplete=(AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autocompletado);
        mautocomplete.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        mautocomplete.setHint("Lugar de origen");
        mautocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin=place.getName();
                mOriginLtg=place.getLatLng();
                Log.d("Place","Name: "+mOrigin);
                Log.d("Place","Lat: "+mOriginLtg.latitude);
                Log.d("Place","Lat: "+mOriginLtg.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


    }
    private void InstanceAutocompleteDestino(){
        //        autocomopletado destino
        mautocompleteDestino=(AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autocompletadodestino);
        mautocompleteDestino.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        mautocompleteDestino.setHint("Lugar de destino");
        mautocompleteDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDestino=place.getName();
                mDestinoLtg=place.getLatLng();
                Log.d("Place","Name: "+mDestino);
                Log.d("Place","Lat: "+mDestinoLtg.latitude);
                Log.d("Place","Lat: "+mDestinoLtg.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }
    private void MostrarConductoresActivos(){
        geofireProvider.ConductoresActivos(latLngUbicacionActual,28).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
//                se anade los marcadores de los conductores que se conectan en la apicacion
                for (Marker marker:markersConductores){
                    if (marker.getTag()!=null){
                        if (marker.getTag().equals(key)){
                            return;
                        }
                    }
                }
                LatLng lngConductorPosicion=new LatLng(location.latitude,location.longitude);
                Marker markeer=nMap.addMarker(new MarkerOptions().position(lngConductorPosicion).title("Conductor Disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                markeer.setTag(key);
                markersConductores.add(markeer);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker:markersConductores){
                    if (marker.getTag()!=null){
                        if (marker.getTag().equals(key)){
                            marker.remove();
                            markersConductores.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
//                actualizar la ubicacion del conductor
                for (Marker marker:markersConductores){
                    if (marker.getTag()!=null){
                        if (marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude,location.longitude));

                        }
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        nMap.getUiSettings().setZoomControlsEnabled(false);
        nMap.setOnCameraIdleListener(cameraIdleListener);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivado()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActivado()){
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
                        ActivityCompat.requestPermissions(MapClienteActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                    }
                }).create().show();
            }else {
                ActivityCompat.requestPermissions(MapClienteActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
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
        return super.onOptionsItemSelected(item);
    }
    void logout(){
        tokenProvider.deleteToken(authProvider.getId());
        authProvider.logout();
        Intent intent=new Intent(MapClienteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    void GenerarToken(){
        tokenProvider.Crear(authProvider.getId());
    }
}