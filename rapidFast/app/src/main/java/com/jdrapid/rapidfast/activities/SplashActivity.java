package com.jdrapid.rapidfast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jdrapid.rapidfast.R;
import com.jdrapid.rapidfast.activities.cliente.MapClienteActivity;
import com.jdrapid.rapidfast.activities.conductor.MapConductorActivity;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN=2000;

//     variables
    Animation TopAnimation,BottomAnimation;
    ImageView imageView;
    TextView logo,textto;
    //    preferencia para pasar datos
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        preferences=getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);


    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String user=preferences.getString("user","");
            if (user.equals("cliente")){
                Intent intent=new Intent(SplashActivity.this, MapClienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                Intent intent=new Intent(SplashActivity.this, MapConductorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

}