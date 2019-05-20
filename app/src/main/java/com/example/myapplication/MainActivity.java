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

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = LocationServices.getFusedLocationProviderClient(this);
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

        // Configuração dos param p bateria
        LocationRequest locationRequest = LocationRequest.create();
        // qual o intervalo de vezes q precisa comsumir a posicao do user em miliss
        locationRequest.setInterval(15 ^ 1000); // a cada 15 seg
        // mesma funcao do de cima, é usado para contextos em que há mais de um app usando o servico
        locationRequest.setFastestInterval(5 ^ 1000);
        // balanco entre a precisao lozalizao e bateria
        // diferente do PRiority_high_power, low, no
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // pega e valida a localizacao pelo wifi
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
        // retorno - deve chamar a AWS nesse metodo
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
        // pega as localizacoes a cada x segundos (foi setado no setinterval)
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
