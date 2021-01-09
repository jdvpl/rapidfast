package com.jdrapid.rapidfastDriver.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.jdrapid.rapidfastDriver.R;
import com.jdrapid.rapidfastDriver.providers.AuthProvider;
import com.jdrapid.rapidfastDriver.providers.GeofireProvider;
import com.jdrapid.rapidfastDriver.utils.CarMoveAnim;


public class ForegroundService extends Service {
    public final String NOTIFICATION_CHANNEL_ID = "com.jdrapid.rapidfastDriver";
    Handler handler = new Handler();
    LatLng latLngUbicacionActual;
    GeofireProvider geofireProvider;
    AuthProvider authProvider;
    LocationRequest locationRequest;
    LocationManager mlocationManager;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnable, 1000);

        }
    };

    LocationListener locationListenerGps = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            latLngUbicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
            ActualizarUbicacion();
        }
    };

    private void ActualizarUbicacion() {
        if (authProvider.existeSesion() && latLngUbicacionActual != null) {
            geofireProvider.guardarUbicacion(authProvider.getId(), latLngUbicacionActual);


        }

    }

    private void startLocation() {
        mlocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10,locationListenerGps);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        authProvider=new AuthProvider();
        geofireProvider=new GeofireProvider("Conductores_trabajando");
        startLocation();
    }
    private void stopLocation(){
        if (locationListenerGps!=null){
            mlocationManager.removeUpdates(locationListenerGps);
        }
    }
    @Override
    public void onDestroy() {
        stopLocation();
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification=new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID).setContentTitle("Viaje en Curso").setContentText("Aplicacion corriendo en segundo plano").setSmallIcon(R.drawable.logo).
                setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE).build();
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            InicarMyForeGroundService();
        }else {
            startForeground(50,notification);
        }
        return START_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void InicarMyForeGroundService(){
        String channelName="My Foreground Service";
        NotificationChannel channel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.BLACK);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert  manager!=null;
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        Notification notification=builder.setOngoing(true).setSmallIcon(R.drawable.logo).setContentTitle("Viaje en curso").setContentText("Aplicacion corriendo en segundo plano").setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE).build();
        startForeground(50,notification);

    }
}
