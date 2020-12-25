package com.jdrapid.rapidfastDriver.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jdrapid.rapidfastDriver.R;

public class ToolBar {
    public  static void mostrar(AppCompatActivity activity,String titulo,boolean mostartBoton){
        //        toolbar
        Toolbar toolbar=activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(titulo);
       activity.getSupportActionBar().setDisplayHomeAsUpEnabled(mostartBoton);
    }
}
