package com.jdrapid.rapidfast.activities.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.conductor.MapConductorSolicitud;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.ConductorProvider;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.GoogleApiProvider;
import com.jdrapid.rapidfast.providers.TokenProvider;
import com.jdrapid.rapidfast.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    //    ubicacion y recogida del cliente
    private String mOrigin,mDestino;
    private LatLng mOriginLtg,mDestinoLtg;
    private LatLng mDriverLatlng;

    private Marker markerConductor;
    private boolean esPrimerVez=true;

//    textview
    private TextView txtNombreConductor,txtEmailConductor;
    private TextView txtOrigenConductor,txtDestinoConductor,txtEstadoSolicitud;

private LatLng mOriginLatlng,mDestinoLatlng;
    private GoogleApiProvider googleApiProvider;
    private List<LatLng> listaPoligonos;
    private PolylineOptions polylineOptions;
    private ValueEventListener valueEventListener;
    private String midCondutor;
    private ValueEventListener lsitenerEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cliente_reserva);

        authProvider = new AuthProvider();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofireProvider=new GeofireProvider("Conductores_trabajando");
        tokenProvider=new TokenProvider();
        clienteReservaProvider=new ClienteReservaProvider();
        googleApiProvider=new GoogleApiProvider(MapClienteReservaActivity.this);
        conductorProvider=new ConductorProvider();
//        inicializqar los txtviews
        txtNombreConductor=findViewById(R.id.txtconductornombne);
        txtEmailConductor=findViewById(R.id.txtemaiconductor);
        txtEstadoSolicitud=findViewById(R.id.txtEstadoSolicitud);
        txtOrigenConductor=findViewById(R.id.txtConductorOrigen);
        txtDestinoConductor=findViewById(R.id.txtConductorDestino);


        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));
        }
        obtenerEstado();
        obtenerClienteSolicitudInfo();

    }

    private void obtenerEstado() {
        lsitenerEstado= clienteReservaProvider.getEstado(authProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String estado=snapshot.getValue().toString();

                    if (estado.equals("Aceptado")){
                        txtEstadoSolicitud.setText("Estado : Aceptado");
                    }
                    if (estado.equals("Iniciar")){
                        txtEstadoSolicitud.setText("Estado : Viaje Iniciado");
                        comenzarSolicitud();
                    }else if (estado.equals("Finalizar")){
                        txtEstadoSolicitud.setText("Estado : Viaje Finalizado");
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
        Intent intent=new Intent(MapClienteReservaActivity.this,CalificarConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void comenzarSolicitud() {
        nMap.addMarker(new MarkerOptions().position(mDestinoLatlng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinverde)));
        nMap.clear();
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
                    txtOrigenConductor.setText("Recoger en: "+origen);
                    txtDestinoConductor.setText("Destino: "+destino);
                    nMap.addMarker(new MarkerOptions().position(mOriginLatlng).title("Recoger Aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinrojo)));
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
                    String correro=snapshot.child("correo").getValue().toString();
                    txtNombreConductor.setText(nombre);
                    txtEmailConductor.setText(correro);
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
                    if (markerConductor!=null){
                        markerConductor.remove();
                    }
                    markerConductor=nMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)
                    ).title("Tu Conductor").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                    if (esPrimerVez){
                        esPrimerVez=false;
                        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder().target(mDriverLatlng).zoom(15f).build()
                        ));
                        DibujarRuta(mOriginLatlng);
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
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.width(20f);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.jointType(JointType.ROUND);
                    polylineOptions.addAll(listaPoligonos);
                    nMap.addPolyline(polylineOptions);

//                    para obtener el tiempo de la api ejemplo: https://maps.googleapis.com/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&origin=1.2034717,-77.2922318&destination=1.2039062,-77.2927302&departure_time=1603143184104&traffic_model=best_guess&key=AIzaSyAKltRdYeGz-VViXHEaYu00KR7dWirLdv8
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        nMap.getUiSettings().setZoomControlsEnabled(true);
        nMap.setMyLocationEnabled(true);


    }

}