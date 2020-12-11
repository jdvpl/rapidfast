package com.jdrapid.rapidfast.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.providers.GeofireProvider;
import com.jdrapid.rapidfast.providers.GoogleApiProvider;
import com.jdrapid.rapidfast.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallePedirConductor extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap nMap;
    private SupportMapFragment mapFragment;
//
    private double mExtraorigenLat,mExtraorigenLong,mExtraDestinoLat,mExtraDestinoLon;
    private String mExtraOrigen,mExtraDestino;
    private LatLng mOriginLatlng,mDestinoLatlng;

    TextView TxtOrigen,TxtDestino,TxtTiempo,TxtDistancia;
    private Button BtnPedirConductor;

    private GoogleApiProvider googleApiProvider;
    private List<LatLng> listaPoligonos;
    private PolylineOptions polylineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedir_conductor);

        ToolBar.mostrar(this, "Tus Datos", true);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        obtener los datos del activity anterior
        mExtraorigenLat=getIntent().getDoubleExtra("origin_lat",0);
        mExtraorigenLong=getIntent().getDoubleExtra("origin_lon",0);
        mExtraDestinoLat=getIntent().getDoubleExtra("destino_lat",0);
        mExtraDestinoLon=getIntent().getDoubleExtra("destino_lon",0);
//        origen y destino
        mExtraOrigen=getIntent().getStringExtra("Origen");
        mExtraDestino=getIntent().getStringExtra("Destino");

        mOriginLatlng=new LatLng(mExtraorigenLat,mExtraorigenLong);
        mDestinoLatlng=new LatLng(mExtraDestinoLat,mExtraDestinoLon);
//        isntancioando el googleapiprovider
        googleApiProvider=new GoogleApiProvider(DetallePedirConductor.this);

        TxtOrigen=findViewById(R.id.TxtVieOrigen);
        TxtDestino=findViewById(R.id.TxtVieDestino);
        TxtTiempo=findViewById(R.id.TxtTiempo);
        TxtDistancia=findViewById(R.id.TxtDistancia);
        BtnPedirConductor=findViewById(R.id.btnPedirAhora);

        TxtOrigen.setText(mExtraOrigen);
        TxtDestino.setText(mExtraDestino);

        BtnPedirConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SolicitarConductor();
            }
        });
    }

    private void SolicitarConductor() {
        Intent intent=new Intent(DetallePedirConductor.this,SoliciarConductorActivity.class);
        intent.putExtra("origin_lat",mOriginLatlng.latitude);
        intent.putExtra("origin_lon",mOriginLatlng.longitude);
        intent.putExtra("origen",mExtraOrigen);
//        destino
        intent.putExtra("destino",mExtraDestino);
        intent.putExtra("destino_lat",mDestinoLatlng.latitude);
        intent.putExtra("destino_lon",mDestinoLatlng.longitude);
        startActivity(intent);
        finish();
    }

    private void DibujarRuta(){
        googleApiProvider.getDirecciones(mOriginLatlng,mDestinoLatlng).enqueue(new Callback<String>() {
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

//                    colocar el tiempo
                    TxtTiempo.setText(Duracion);
                    TxtDistancia.setText(Distancia);

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
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        nMap.getUiSettings().setZoomControlsEnabled(true);
        nMap.addMarker(new MarkerOptions().position(mOriginLatlng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinrojo)));
        nMap.addMarker(new MarkerOptions().position(mDestinoLatlng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinverde)));
        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(mOriginLatlng).zoom(15f).build()
        ));
        DibujarRuta();

    }
}