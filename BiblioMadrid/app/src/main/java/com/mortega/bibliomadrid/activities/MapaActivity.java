package com.mortega.bibliomadrid.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.Constants;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;
import com.mortega.bibliomadrid.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static com.mortega.bibliomadrid.R.drawable.*;

public class MapaActivity extends Activity implements OnMapReadyCallback, View.OnClickListener {

    private FloatingTextButton rutaButton;
    private FloatingActionButton ubicacion;
    private FloatingTextButton valoracionFab;

    private DirectionsRoute ruta;
    private LocationServices servicioUbicacion;

    private Location userLocation;

    private MapView mapa;
    private MapboxMap mapaBox;
    private double latitud, longitud;
    private String nombre;
    private String informacion;

    private Intent intento;

    private int ACCESO_UBICACION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapboxAccountManager.start(this, "pk.eyJ1IjoibW9ydGVnYXh4IiwiYSI6ImNqcHU2NTNtODA0NWc0OHFydHY2bjhhZjgifQ.7KWXo0sh3qqzLnbeXRIj_Q");

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        informacion = intent.getStringExtra("organization");
        latitud = intent.getDoubleExtra("latitud", 0);
        longitud = intent.getDoubleExtra("longitud", 0);

        if(Build.VERSION.SDK_INT>19)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_mapa);

        rutaButton = (FloatingTextButton) findViewById(R.id.rutaButton);
        rutaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    obtenerRuta(userLocation);
                } catch (ServicesException e) {
                    e.printStackTrace();
                }
            }
        });

        ubicacion = (FloatingActionButton) findViewById(R.id.ubicacionFab);
        ubicacion.setOnClickListener(this);

        valoracionFab = (FloatingTextButton) findViewById(R.id.valoracionFtb);
        valoracionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intento = new Intent(MapaActivity.this, OpinionActivity.class);
                intento.putExtra("lugar",nombre);
                startActivity(intento);
            }
        });

        mapa = (MapView) findViewById(R.id.mapa);
        mapa.onCreate(savedInstanceState);
        mapa.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        mapaBox = mapboxMap;
        mapaBox.addMarker(new MarkerOptions()
            .setIcon(IconFactory.recreate("marker" ,getBitmap(marker)))
            .setPosition(new LatLng(latitud, longitud))
            .setTitle(nombre)
            .setSnippet(informacion));

        CameraPosition posicion = new CameraPosition.Builder()
                .target(new LatLng(latitud, longitud)) // Fija la posición
                .zoom(15) // Fija el nivel de zoom
                .tilt(30) // Fija la inclinación de la cámara
                .build();
        mapaBox.animateCamera(
                CameraUpdateFactory.newCameraPosition (
                        posicion), 3500);
    }

    private void ubicarUsuario() {

        servicioUbicacion = LocationServices.getLocationServices(this);

        if (mapaBox != null) {
            userLocation = servicioUbicacion.getLastLocation();
            if (userLocation != null)
                mapaBox.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation), 16));

            // Resalta la posición del usuario en el mapa
            mapaBox.setMyLocationEnabled(true);

        }
    }

    private void obtenerRuta(Location userLocation) throws ServicesException {

        Toast toast1 =
                Toast.makeText(getApplicationContext(),
                        "Toast por defecto", Toast.LENGTH_SHORT);

        toast1.show();

        Position posicionMarker = Position.fromCoordinates(longitud, latitud);
        Position posicionUsuario = Position.fromCoordinates(userLocation.getLongitude(), userLocation.getLatitude());

        // Obtiene la dirección entre los dos puntos
        MapboxDirections direccion = new MapboxDirections.Builder()
                .setOrigin(posicionMarker)
                .setDestination(posicionUsuario)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
                .setAccessToken(MapboxAccountManager.getInstance().getAccessToken())
                .build();

        direccion.enqueueCall(new Callback<DirectionsResponse>() {

            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                ruta = response.body().getRoutes().get(0);
                Toast.makeText(MapaActivity.this, "Distancia: " + ruta.getDistance() + " metros", Toast.LENGTH_SHORT).show();

                try {
                    pintarRuta(ruta);
                } catch (ServicesException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                // Qué hacer en caso de que falle el cálculo de la ruta
            }
        });
    }

    // Pinta la ruta sobre el mapa
    private void pintarRuta(DirectionsRoute ruta) throws ServicesException{

        // Recoge los puntos de la ruta
        LineString lineString = LineString.fromPolyline(ruta.getGeometry(), Constants.OSRM_PRECISION_V5);
        List<Position> coordenadas = lineString.getCoordinates();
        LatLng[] puntos = new LatLng[coordenadas.size()];
        for (int i = 0; i < coordenadas.size(); i++) {
            puntos[i] = new LatLng(coordenadas.get(i).getLatitude(), coordenadas.get(i).getLongitude());
        }

        // Pinta los puntos en el mapa
        mapaBox.addPolyline(new PolylineOptions()
                .add(puntos)
                .color(Color.parseColor("#144FDA"))
                .width(5));

        // Resalta la posición del usuario si no lo estaba ya
        if (!mapaBox.isMyLocationEnabled())
            mapaBox.setMyLocationEnabled(true);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ubicacionFab:
                dialogPermisos();
                ubicarUsuario();
                break;
        }
    }

    private void dialogPermisos() {

        if (ContextCompat.checkSelfPermission(MapaActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapaActivity.this, "Ya has concedido este permiso",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permisos necesarios")
                    .setMessage("Este permiso es necesario para establecer una ruta con su ubicación")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MapaActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, ACCESO_UBICACION);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, ACCESO_UBICACION);
        }
    }
}
