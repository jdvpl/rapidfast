package com.jdrapid.rapidfast.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jdrapid.rapidfast.R;

import com.jdrapid.rapidfast.models.ClientBooking;
import com.jdrapid.rapidfast.models.HistoryBooking;
import com.jdrapid.rapidfast.providers.AuthProvider;
import com.jdrapid.rapidfast.providers.ClienteReservaProvider;
import com.jdrapid.rapidfast.providers.HistoryBookingProvider;

import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalificarConductorActivity extends AppCompatActivity {
    private TextView mOrigenCalif,mDestinoCalif,califPrecio;
    private RatingBar mRatingBar;
    private Button mButtonCalificacion,BtnContado,BtnContadoOculto,BtnTarjeta,BtnTarjetaOculto;
    private ClienteReservaProvider clienteReservaProvider;
    private HistoryBooking historyBooking;
    private HistoryBookingProvider historyBookingProvider;
    private EditText TxtMensajeConductor;
    String MensajeConductor,precio;
    private float mCalificacion=0;
    private AuthProvider authProvider;


//    payu
    String reference = "payment_test_00000001";
    String value= "20000";

    Map<String, String> parameters = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar_conductor);


        mOrigenCalif=findViewById(R.id.OrigenCalificacionCliente);
        mDestinoCalif=findViewById(R.id.DestinoCalificacionCliente);
        califPrecio=findViewById(R.id.CalificacionPreciocon);
        mRatingBar=findViewById(R.id.RtCalificarConductor);
        mButtonCalificacion=findViewById(R.id.CalificarCondcutor);
        mRatingBar=findViewById(R.id.RtCalificarConductor);
        TxtMensajeConductor=findViewById(R.id.TxtMensajeConductor);

        BtnContado=findViewById(R.id.BtnContado);
        BtnContadoOculto=findViewById(R.id.BtnContadoOculto);
        BtnTarjeta=findViewById(R.id.Btntarjeta);
        BtnTarjetaOculto=findViewById(R.id.BtnTarjetaOculto);



        precio=getIntent().getStringExtra("precio");
        authProvider=new AuthProvider();
        clienteReservaProvider=new ClienteReservaProvider();
        historyBookingProvider=new HistoryBookingProvider();

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float calificacion, boolean b) {
                mCalificacion=calificacion;
            }
        });

        mButtonCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calificar();
            }
        });

        BtnContado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnContado.setVisibility(View.GONE);
                BtnContadoOculto.setVisibility(View.VISIBLE);
            }
        });
        BtnContadoOculto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnContadoOculto.setVisibility(View.GONE);
                BtnContado.setVisibility(View.VISIBLE);
            }
        });

        BtnTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnTarjeta.setVisibility(View.GONE);
                BtnTarjetaOculto.setVisibility(View.VISIBLE);

                PagoPayu();
            }
        });

        BtnTarjetaOculto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnTarjetaOculto.setVisibility(View.GONE);
                BtnTarjeta.setVisibility(View.VISIBLE);

            }
        });

        obtenerClienteSolicitud();
    }

    private void PagoPayu() {

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(precio)
        .setIsProduction(true)
        .setProductInfo("Pago Rapid Fast")
        .setKey("920205")
        .setPhone("3209188638")
        .setTransactionId(String.valueOf(System.currentTimeMillis()))
        .setFirstName("Kakarot")
        .setEmail("Juanda5542424gmail.com")
        .setSurl("https://payuresponse.firebaseapp.com/success")
        .setFurl("https://payuresponse.firebaseapp.com/failure")
                .build();
        PayUPaymentParams payUPaymentParams = builder.build();


        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                new PayUCheckoutProListener() {



                    @Override
                    public void onPaymentSuccess(Object response) {
                        //Cast response object to HashMap
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                    }

                    @Override
                    public void onPaymentFailure(Object response) {
                        //Cast response object to HashMap
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                            String hash = "SHA512(a2cqBC|fa3359f205d621c07383|2|Product Info|Payu-Admin|test@example.com|||||||||||{\"billingAmount\": \"150.00\",\"billingCurrency\": \"INR\",\"billingCycle\": \"WEEKLY\",\"billingInterval\": 1,\"paymentStartDate\": \"2019-09-18\",\"paymentEndDate\": \"2020-10-20\"}|dEvD9ABD)";
                            HashMap<String, String> dataMap = new HashMap<>();
                            dataMap.put(hashName, hash);
                            hashGenerationListener.onHashGenerated(dataMap);
                        }
                    }
                }
        );
    }



    private void obtenerClienteSolicitud(){
        clienteReservaProvider.getClienteSolicitud(authProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ClientBooking clientBooking=snapshot.getValue(ClientBooking.class);
                    MensajeConductor=TxtMensajeConductor.getText().toString();

                    if (clientBooking != null) {
                        mOrigenCalif.setText(clientBooking.getOrigen());
                        mDestinoCalif.setText(clientBooking.getDestino());
                    }
                    califPrecio.setText("$ "+precio);


                    historyBooking=new HistoryBooking(
                            clientBooking.getIdHistorialSolicitud(),
                            clientBooking.getIdCliente(),
                            clientBooking.getIdConductor(),
                            clientBooking.getDestino(),
                            clientBooking.getOrigen(),
                            clientBooking.getTiempo(),
                            clientBooking.getDistanciaKm(),
                            clientBooking.getEstado(),
                            clientBooking.getOrigenLat(),
                            clientBooking.getOrigenLong(),
                            clientBooking.getDestinoLat(),
                            clientBooking.getDestinoLong(),
                            precio,
                            "",
                            MensajeConductor
                    );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calificar() {
        MensajeConductor =TxtMensajeConductor.getText().toString();

        if (mCalificacion>0 && !MensajeConductor.equals("")){
            historyBooking.setCalificacionConductor(mCalificacion);
            Toast.makeText(this, MensajeConductor, Toast.LENGTH_SHORT).show();
            historyBooking.setMensajeConductor(MensajeConductor);
            historyBooking.setTimestamp(new Date().getTime());
            historyBookingProvider.getHistorialReserva(historyBooking.getIdHistorialSolicitud()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        historyBookingProvider.ActualizarCalificacionConducotor(historyBooking.getIdHistorialSolicitud(),mCalificacion,MensajeConductor).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificarConductorActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CalificarConductorActivity.this, MapClienteActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else {
                        historyBookingProvider.Crear(historyBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificarConductorActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CalificarConductorActivity.this, MapClienteActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else {
            Toast.makeText(this, "Por Favor ingresa la calificacion y un comentario", Toast.LENGTH_SHORT).show();
        }
    }
}