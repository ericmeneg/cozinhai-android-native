package com.example.cozinhai;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private MapView map = null;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private MyLocationNewOverlay locationOverlay;
    private Location lastFetchedLocation = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Botão de mira (opcional, para centralizar se você se perder no mapa)
        FloatingActionButton fab = findViewById(R.id.fabMyLocation);
        fab.setOnClickListener(v -> {
            if (locationOverlay != null && locationOverlay.getMyLocation() != null) {
                map.getController().animateTo(locationOverlay.getMyLocation());
                map.getController().setZoom(17.0);
            }
        });

        // Este callback é chamado sempre que a localização muda
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d(TAG, "GPS atualizado: " + location.getLatitude() + ", " + location.getLongitude());
                        
                        // 1. Se for a primeira vez ou se nos movermos mais de 300 metros, atualiza os mercados
                        if (lastFetchedLocation == null || location.distanceTo(lastFetchedLocation) > 300) {
                            updateMapAndMarkets(location);
                            lastFetchedLocation = location;
                        }
                    }
                }
            }
        };

        checkLocationPermissions();
        setupBottomNavigation();
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        // Overlay do "Bonequinho Azul" que se move sozinho
        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        locationOverlay = new MyLocationNewOverlay(provider, map);
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation(); // Faz o mapa seguir você automaticamente
        map.getOverlays().add(locationOverlay);

        // Pede pro Android atualizar a posição a cada 3 segundos
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(1500)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        
        checkGPS();
    }

    private void updateMapAndMarkets(Location location) {
        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
        // Só centraliza se não estivermos navegando manualmente (opcional)
        map.getController().animateTo(point);
        map.getController().setZoom(17.0);
        
        fetchNearbyMarkets(location.getLatitude(), location.getLongitude());
    }

    private void fetchNearbyMarkets(double lat, double lon) {
        String query = "[out:json];node(around:3000, " + lat + ", " + lon + ")[shop~\"supermarket|convenience|grocery|bakery\"];out;";

        NetworkClient.getOverpassApi().getNearbyShops(query).enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Call<OverpassResponse> call, Response<OverpassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Remove apenas os marcadores de mercados antigos
                    map.getOverlays().removeIf(overlay -> overlay instanceof Marker);
                    
                    List<OverpassResponse.Element> elements = response.body().getElements();
                    if (elements != null) {
                        for (OverpassResponse.Element element : elements) {
                            String name = (element.getTags() != null && element.getTags().getName() != null) 
                                    ? element.getTags().getName() : "Mercado";
                            addMarketMarker(new GeoPoint(element.getLat(), element.getLon()), name);
                        }
                    }
                    map.invalidate();
                }
            }
            @Override public void onFailure(Call<OverpassResponse> call, Throwable t) { Log.e(TAG, "Erro API Overpass"); }
        });
    }

    private void addMarketMarker(GeoPoint point, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        map.getOverlays().add(marker);
    }

    private void checkGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setTitle("GPS Desligado")
                    .setMessage("Para identificar sua localização automaticamente, ative o GPS.")
                    .setPositiveButton("Ativar", (d, w) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Agora não", null).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override protected void onResume() { super.onResume(); map.onResume(); if (locationOverlay != null) locationOverlay.enableMyLocation(); }
    @Override protected void onPause() { super.onPause(); map.onPause(); if (locationOverlay != null) locationOverlay.disableMyLocation(); fusedLocationClient.removeLocationUpdates(locationCallback); }

    private void setupBottomNavigation() {
        findViewById(R.id.btnNavHome).setOnClickListener(v -> startActivity(new Intent(this, Home.class)));
        findViewById(R.id.btnNavSearch).setOnClickListener(v -> startActivity(new Intent(this, Search.class)));
        findViewById(R.id.btnNavProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}
