package com.jdrapid.rapidfast.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdrapid.rapidfast.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN=3000;

//     variables
    Animation TopAnimation,BottomAnimation;
    ImageView imageView;
    TextView logo,textto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        TopAnimation= AnimationUtils.loadAnimation(this,R.anim.top_animacion);
        BottomAnimation= AnimationUtils.loadAnimation(this,R.anim.bottom_animacion);

        imageView=findViewById(R.id.spashcarro);
        logo=findViewById(R.id.txtLogo);
        textto=findViewById(R.id.txtInicio);

        imageView.setAnimation(TopAnimation);
        logo.setAnimation(BottomAnimation);
        textto.setAnimation(BottomAnimation);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);


    }
}