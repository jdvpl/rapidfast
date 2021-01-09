package com.jdrapid.rapidfastDriver.providers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoCelularProvider {
    DatabaseReference mDatabase;

    public InfoCelularProvider() {
        mDatabase= FirebaseDatabase.getInstance().getReference().child("InfoCelulares");
    }
    public DatabaseReference getInfo(){
        return mDatabase;
    }
}
