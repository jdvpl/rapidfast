package com.jdrapid.rapidfast.activities;

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
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.adapters.PopupAdapter;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.ConductorLocation;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.GoogleApiProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;
import com.jdrapid.rapidfast.utils.CarMoveAnim;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ConductorProvider conductorProvider;
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
    private ImageView BtnCambiar;
    private boolean mOrigenSelect =true;
    private ClienteReservaProvider clienteReservaProvider;
    private HashMap<String,String>mImagenMarkers=new HashMap<String, String>();

    private int mCounter=0;
    SharedPreferences mPref;
    Boolean DistanciaBogota=false;
    int finalDistancia;

    private GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS=0x1;

    private ArrayList<ConductorLocation> mconductorLocations =new ArrayList<>();
    private GoogleApiProvider googleApiProvider;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    latLngUbicacionActual=new LatLng(location.getLatitude(),location.getLongitude());

                    if (esPrimerVez){
                        esPrimerVez=false;

                        // COLOCA AQUI EL MOVE CAMERA PARA QUE SOLO SE ACTUALIZE LA POSICION DEL MAPA UNA SOLA VEZ
                    nMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(18f).build()

                    ));
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

        ToolBar.mostrar(this, "Rapidfast", false);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofireProvider=new GeofireProvider("Conductores_Activos");
        tokenProvider=new TokenProvider();
        clienteReservaProvider=new ClienteReservaProvider();
        conductorProvider=new ConductorProvider();

        googleApiProvider=new GoogleApiProvider(MapClienteActivity.this);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        BtnSolcitarViaje=findViewById(R.id.BtnSolicitarViaje);
        BtnCambiar=findViewById(R.id.btnCambiar);

        BtnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOrigenSelect){
                    Toast.makeText(MapClienteActivity.this, "Estas Seleccionando el lugar de destino", Toast.LENGTH_LONG).show();
                    mOrigenSelect=false;
                }else {
                    mOrigenSelect=true;
                    Toast.makeText(MapClienteActivity.this, "Estas Seleccionando el lugar de recogida", Toast.LENGTH_LONG).show();

                }
            }
        });



        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }
        placesClient=Places.createClient(this);
        InstanceAutocompleteOrigen();
        InstanceAutocompleteDestino();

        googleApiClient=getAPIClienteInstance();
        if (googleApiClient!=null){
            googleApiClient.connect();
        }

        mPref=getApplicationContext().getSharedPreferences("RideSatus",MODE_PRIVATE);
        String mStatus=mPref.getString("status","");
        String idConductor=mPref.getString("idConductor","");

        if (mStatus.equals("ride") || mStatus.equals("Iniciar")){
            gotoMapDriverSolicitudActivity(idConductor);
        }else {
            OnCameraMoved();
            BorrarCLienteSolicitud();
            GenerarToken();
        }



        BtnSolcitarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    PedirConductor();
                    mautocompleteDestino.setText("");
            }
        });
    }

    private void ObtenerCiudad() {
        if (mOriginLtg!=null && mDestinoLtg!=null){
            googleApiProvider.getDirecciones(mOriginLtg,mDestinoLtg).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response.body());
                        JSONArray jsonArray=jsonObject.getJSONArray("routes");
                        JSONObject ruta=jsonArray.getJSONObject(0);
                        JSONArray legs=ruta.getJSONArray("legs");
                        JSONObject leg=legs.getJSONObject(0);

                        JSONObject distancia=leg.getJSONObject("distance");
                        String Distancia=distancia.getString("text");
                        String[] numero=Distancia.split(" ");
                        finalDistancia=Integer.parseInt(numero[0]);

                        Toast.makeText(MapClienteActivity.this, "Distancia "+numero[0], Toast.LENGTH_SHORT).show();


                    }catch (Exception e){

                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }

    }


    private GoogleApiClient getAPIClienteInstance(){
        GoogleApiClient googleApiClient=new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
        return googleApiClient;
    }
    private void requestGPSSettings(){
        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult>  result=LocationServices.SettingsApi.checkLocationSettings(googleApiClient,builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status=locationSettingsResult.getStatus();
                if (status.getStatusCode()== LocationSettingsStatusCodes.SUCCESS){
                    Toast.makeText(MapClienteActivity.this, "El Gps ya esta avidado", Toast.LENGTH_SHORT).show();
                }else if (status.getStatusCode()== LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                    try {
                        status.startResolutionForResult(MapClienteActivity.this,REQUEST_CHECK_SETTINGS);
                        if (ActivityCompat.checkSelfPermission(MapClienteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapClienteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }catch (IntentSender.SendIntentException e){
                        Toast.makeText(MapClienteActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
               ;
                }else if (status.getStatusCode()== LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE){
                    Toast.makeText(MapClienteActivity.this, "La configuracion del gps tiene un error o no esta disponible", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void gotoMapDriverSolicitudActivity(String idConductor) {
        Intent intent=new Intent(MapClienteActivity.this,MapClienteReservaActivity.class);
        intent.putExtra("idConductor",idConductor);
        startActivity(intent);
    }

    private void BorrarCLienteSolicitud() {
        clienteReservaProvider.borrar(authProvider.getId());
    }

    private void PedirConductor() {
        if (mOriginLtg!=null && mDestinoLtg!=null) {

            try {
                if (finalDistancia<=60) {
                    Intent intent = new Intent(MapClienteActivity.this, DetallePedirConductor.class);
                    intent.putExtra("origin_lat", mOriginLtg.latitude);
                    intent.putExtra("origin_lon", mOriginLtg.longitude);
                    intent.putExtra("destino_lat", mDestinoLtg.latitude);
                    intent.putExtra("destino_lon", mDestinoLtg.longitude);
                    intent.putExtra("Origen", mOrigin);
                    intent.putExtra("Destino", mDestino);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Ciudad no Disponible");
                    builder.setMessage("Debes seleccionar un lugar en bogota");
                    builder.setPositiveButton("Aceptar", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
            catch (Exception e){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ciudad Erronea");
                builder.setMessage(e.getMessage()+"kisamado");
                builder.setPositiveButton("Aceptar", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            }
        else {
            Toast.makeText(this,"Debe seleccionar el lugar de recogida y destino",Toast.LENGTH_LONG).show();
        }
    }

    private void LimiteBusqueda(){
        LatLng northSide = SphericalUtil.computeOffset(latLngUbicacionActual, 3000,0);
        LatLng southSide = SphericalUtil.computeOffset(latLngUbicacionActual, 3000,180);
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
                    if (mOrigenSelect){
                        mOriginLtg=nMap.getCameraPosition().target;
                        List<Address> addressList=geocoder.getFromLocation(mOriginLtg.latitude,mOriginLtg.longitude,1);
                        String ciudad=addressList.get(0).getLocality();
                        String pais=addressList.get(0).getCountryName();
                        String direccion=addressList.get(0).getAddressLine(0);
                        mOrigin=direccion+ciudad;
                        mautocomplete.setText(direccion+" "+ciudad);
                        ObtenerCiudad();
                    }else {
                        mDestinoLtg=nMap.getCameraPosition().target;
                        List<Address> addressList=geocoder.getFromLocation(mDestinoLtg.latitude,mDestinoLtg.longitude,1);
                        String ciudad=addressList.get(0).getLocality();
                        String pais=addressList.get(0).getCountryName();
                        String direccion=addressList.get(0).getAddressLine(0);
                        mDestino=direccion;
                        mautocompleteDestino.setText(direccion+" "+ciudad);
                        ObtenerCiudad();
                    }
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
                ObtenerCiudad();
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
                ObtenerCiudad();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }
    private void MostrarConductoresActivos(){
        geofireProvider.ConductoresActivos(latLngUbicacionActual,25).addGeoQueryEventListener(new GeoQueryEventListener() {
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
                Marker markeer=nMap.addMarker(new MarkerOptions().position(lngConductorPosicion).title("Conductor Disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.carrorappid)));
                markeer.setTag(key);
                markersConductores.add(markeer);

                ConductorLocation conductorLocation=new ConductorLocation();
                conductorLocation.setId(key);
                mconductorLocations.add(conductorLocation);

                ObtenerConductorInfo();
                nMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (mOriginLtg!=null &&mDestinoLtg!=null) {
                            Intent intent=new Intent(MapClienteActivity.this,DetallePedirConductor.class);
                            intent.putExtra("origin_lat",mOriginLtg.latitude);
                            intent.putExtra("origin_lon",mOriginLtg.longitude);
                            intent.putExtra("destino_lat",mDestinoLtg.latitude);
                            intent.putExtra("destino_lon",mDestinoLtg.longitude);
                            intent.putExtra("Origen",mOrigin);
                            intent.putExtra("Destino",mDestino);

                            intent.putExtra("idConductor",marker.getTag().toString());
                            intent.putExtra("Conductor_lat",marker.getPosition().latitude);
                            intent.putExtra("Conductor_lon",marker.getPosition().longitude);


                            startActivity(intent);
                        }else {
                            Toast.makeText(MapClienteActivity.this,"Debe seleccionar el lugar de recogida y destino",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker:markersConductores){
                    if (marker.getTag()!=null){
                        if (marker.getTag().equals(key)){
                            marker.remove();
                            markersConductores.remove(marker);
                            mconductorLocations.remove(obtenerPosicionConductyor(key));
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
//                actualizar la ubicacion del conductor
                for (Marker marker:markersConductores){
                    LatLng start=new LatLng(location.latitude,location.longitude);
                    LatLng end=null;

                    int posicion=obtenerPosicionConductyor(marker.getTag().toString());

                    if (marker.getTag()!=null){
                        if (marker.getTag().equals(key)){

                            if (mconductorLocations.get(posicion).getLatLng() !=null){
                                end=mconductorLocations.get(posicion).getLatLng();
                            }
                            mconductorLocations.get(posicion).setLatLng(new LatLng(location.latitude,location.longitude));

                            if (end!=null){
                                CarMoveAnim.carAnim(marker,end,start);
                            }

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
    private int obtenerPosicionConductyor(String id){
        int posicion=0;
        for (int i=0; i<mconductorLocations.size(); i++){
            if (id.equals(mconductorLocations.get(i).getId())){
                posicion=i;
                break;
            }
        }
        return posicion;
    }
    private void ObtenerConductorInfo() {
        mCounter=0;
        for (Marker marker: markersConductores){
            conductorProvider.getConductor(marker.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    mCounter=mCounter+1;

                    if (snapshot.exists()){
                        if (snapshot.hasChild("nombre")){
                            String nombre=snapshot.child("nombre").getValue().toString();
                            marker.setTitle(nombre);
                        }
                        if (snapshot.hasChild("imagen")){
                            String imagen=snapshot.child("imagen").getValue().toString();
                            mImagenMarkers.put(marker.getTag().toString(),imagen);
                        }else {
                            mImagenMarkers.put(marker.getTag().toString(),null);

                        }
                    }
                    //termina de traer toda la infromacion de los conductores
                    if (mCounter==mImagenMarkers.size()){
                        nMap.setInfoWindowAdapter(new PopupAdapter(MapClienteActivity.this,getLayoutInflater(),mImagenMarkers));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.getUiSettings().setZoomControlsEnabled(false);
        nMap.setOnCameraIdleListener(cameraIdleListener);
        nMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.estilo_mapa));

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
//                        ShowAlertGPS();
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
                }else {
//                    ShowAlertGPS();
                    requestGPSSettings();

                }

            }else {
                checkLocationPermmisions();
            }

        }else {
            if (gpsActivado()){
                fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

            }else {
                //ShowAlertGPS();
                requestGPSSettings();

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
        getMenuInflater().inflate(R.menu.cliente_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutCliente){
            logout();
        }
        if (item.getItemId() == R.id.actualizarPerfil){
            Intent intent=new Intent(MapClienteActivity.this,ActualizarPerfilActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.Historial){
            Intent intent=new Intent(MapClienteActivity.this,HistorialSolicitudClienteActivity.class);
            startActivity(intent);
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