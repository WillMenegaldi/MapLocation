package com.example.myapplication;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient client;
    Button botao;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = LocationServices.getFusedLocationProviderClient(this);

        //View
        botao = (Button) findViewById(R.id.botao);
        textView = (TextView)findViewById(R.id.textView);

        final double latPuc = -19.9333371;
        final double longPuc = 43.9371446;

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Location location = findLocation();
                 double distancia = haversine(latPuc, longPuc, location.getLatitude(), location.getLongitude());
                textView.setText("Você está a " + distancia + " metros da puc.");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int errorCode =  GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (errorCode){
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                Log.d("Teste", "show dialog");
                break;
            case ConnectionResult.SUCCESS:
                Log.d("Teste", "Google Play Services up-to-date");
                break;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        }

        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            Log.i("Teste", location.getLatitude() + " " + location.getLongitude());
                        }else{
                            Log.i("Teste", "null");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        // ----------------------------------------------
        // Configuração dos parametros para a bateria
        LocationRequest locationRequest = LocationRequest.create();
        // Qual o intervalo de vezes irá comsumir a posicao do usuário em milissegundos
        locationRequest.setInterval(15 ^ 1000); // a cada 15 seg
        // Mesma funcao da de cima. É usado para contextos em que há mais de um aplicativo usando o servico
        locationRequest.setFastestInterval(5 ^ 1000);
        // Balanco entre a precisão da localização e a bateria. Diferente do Priority_high_power, Priority_low_power Priority_no_power
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // ----------------------------------------------

        // Acessa e valida a localizacao pelo Wifi
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("Teste", locationSettingsResponse.getLocationSettingsStates().isNetworkLocationPresent() + " ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if( e instanceof ResolvableApiException){
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, 10);
                    } catch (IntentSender.SendIntentException e1){

                    }
                }
            }
        });

        // Retorno - chamar a AWS nesse metodo
        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    Log.i("Teste2", "local is null");
                    return;
                }

                for (Location location : locationResult.getLocations()){
                    Log.i("Teste2", location.getLatitude() + "" );
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.i("Teste", locationAvailability.isLocationAvailable() + " ");
            }
        };
        // acessa as localizacoes a cada x segundos (foi setado no setInterval)
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    // Função para ser chamada ao clicar no botão
    protected Location findLocation(){
        int errorCode =  GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (errorCode){
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                Log.d("Teste", "show dialog");
                break;
            case ConnectionResult.SUCCESS:
                Log.d("Teste", "Google Play Services up-to-date");
                break;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        }

        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            Log.i("Teste", location.getLatitude() + " " + location.getLongitude());
                        }else{
                            Log.i("Teste", "null");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        return location;
    }

    // Fórmula de Haversine
    private static double haversine(double lat1, double lon1, double lat2, double lon2){
        final double R = 6372.8; // In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

}