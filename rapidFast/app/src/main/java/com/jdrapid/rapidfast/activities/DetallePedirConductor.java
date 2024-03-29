package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.includes.ToolBar;
import com.jdrapid.rapidfast.models.Info;
import com.jdrapid.rapidfast.providers.GoogleApiProvider;
import com.jdrapid.rapidfast.providers.InfoProvider;
import com.jdrapid.rapidfast.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallePedirConductor extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap nMap;
    private SupportMapFragment mapFragment;
//
    private double mExtraorigenLat,mExtraorigenLong,mExtraDestinoLat,mExtraDestinoLon,mExtraConductorLat,mExtraConductorLon;
    private String mExtraOrigen,mExtraDestino,mExtraConductorId,Tiempo,Distanciakm;

    private LatLng mOriginLatlng,mDestinoLatlng;
    private GoogleApiProvider googleApiProvider;
    private List<LatLng> listaPoligonos;
    private PolylineOptions polylineOptions;
    double precio;
    TextView TxtOrigen,TxtDestino,TxtTiempo,TxtPrecio;
    private Button BtnPedirConductor;

    private InfoProvider infoProvider;




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

        mExtraConductorId=getIntent().getStringExtra("idConductor");
        mExtraConductorLat=getIntent().getDoubleExtra("Conductor_lat",0);
        mExtraConductorLon=getIntent().getDoubleExtra("Conductor_lon",0);

//        origen y destino
        mExtraOrigen=getIntent().getStringExtra("Origen");
        mExtraDestino=getIntent().getStringExtra("Destino");

        mOriginLatlng=new LatLng(mExtraorigenLat,mExtraorigenLong);
        mDestinoLatlng=new LatLng(mExtraDestinoLat,mExtraDestinoLon);
//        isntancioando el googleapiprovider
        googleApiProvider=new GoogleApiProvider(DetallePedirConductor.this);
        infoProvider=new InfoProvider();

        TxtOrigen=findViewById(R.id.TxtVieOrigen);
        TxtDestino=findViewById(R.id.TxtVieDestino);
        TxtTiempo=findViewById(R.id.TxtTiempo);
        TxtPrecio=findViewById(R.id.TxtPrecio);
        BtnPedirConductor=findViewById(R.id.btnPedirAhora);

        TxtOrigen.setText(mExtraOrigen);
        TxtDestino.setText(mExtraDestino);

        BtnPedirConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //solo para un condcutor especifico
                if (mExtraConductorId !=null){
                    SolicitarConductorEspecifico();
                }else {
                    SolicitarConductor();
                }
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
        intent.putExtra("precio",precio);
        intent.putExtra("tiempo",Tiempo);
        intent.putExtra("distanciakm",Distanciakm);


        startActivity(intent);
        finish();
    }

    private void SolicitarConductorEspecifico() {
        Intent intent=new Intent(DetallePedirConductor.this,SolicitarConductorByIdActivity.class);
        intent.putExtra("origin_lat",mOriginLatlng.latitude);
        intent.putExtra("origin_lon",mOriginLatlng.longitude);
        intent.putExtra("origen",mExtraOrigen);
//        destino
        intent.putExtra("destino",mExtraDestino);
        intent.putExtra("destino_lat",mDestinoLatlng.latitude);
        intent.putExtra("destino_lon",mDestinoLatlng.longitude);

        intent.putExtra("idConductor",mExtraConductorId);
        intent.putExtra("Conductor_lat",mExtraConductorLat);
        intent.putExtra("Conductor_lon",mExtraConductorLon);
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
                    polylineOptions.color(Color.BLACK);
                    polylineOptions.width(15f);
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
                    TxtTiempo.setText(Duracion + " "+ Distancia);
                    Tiempo=Duracion;
                    Distanciakm=Distancia;
                    String [] distanciaykm=Distancia.split(" ");
                    double distanciaValor=Double.parseDouble(distanciaykm[0]);

                    String [] duracionyMin=Duracion.split(" ");
                    double duracionCValor=Double.parseDouble(duracionyMin[0]);

                    calcularPrecio(distanciaValor,duracionCValor);


                }catch (Exception e){
                    Log.d("Error","Jiren: "+e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    private void calcularPrecio(double distanciaValor, double duracionCValor) {
        infoProvider.getInfo().addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
//                    hora

                    Calendar calendario=Calendar.getInstance();
                    double horas,minutos;
                    horas=calendario.get(Calendar.HOUR_OF_DAY);
                    minutos=calendario.get(Calendar.MINUTE);

                    double HoraActual=horas+(minutos/60);

                    Info info=snapshot.getValue(Info.class);

                    double totalDistancia=distanciaValor*info.getKm();
                    double totalDuracion=duracionCValor*info.getMin();

                    double totalDistancianoche=distanciaValor*info.getKmnoche();
                    double totalDuracionnoche=duracionCValor*info.getMinnoche();


                    int total= (int) (totalDistancia+totalDuracion);
                    int totalpico= (int) (totalDistancianoche+totalDuracionnoche);

                    if (total<4600 || totalpico<4600){
                        precio=4500;
                        TxtPrecio.setText("$ "+4500);
                    }else {
                        if ((HoraActual>=6 && HoraActual<=8)||(HoraActual>=17&&HoraActual<=20)){
                            precio=totalpico;
                            TxtPrecio.setText("$ "+totalpico);
                        }else {
                            precio=total;
                            TxtPrecio.setText("$ "+total);
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.estilo_mapa));
        nMap.getUiSettings().setZoomControlsEnabled(true);
        nMap.addMarker(new MarkerOptions().position(mOriginLatlng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        nMap.addMarker(new MarkerOptions().position(mDestinoLatlng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(mOriginLatlng).zoom(15f).build()
        ));
        DibujarRuta();

    }
}