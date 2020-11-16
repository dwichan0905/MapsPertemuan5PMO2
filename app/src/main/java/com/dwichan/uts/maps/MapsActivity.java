package com.dwichan.uts.maps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dwichan.uts.maps.model.DirectionFindList;
import com.dwichan.uts.maps.model.DirectionFindPath;
import com.dwichan.uts.maps.model.Route;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, DirectionFindList, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Button btnNavToTarget;
    private LatLng destination;
    private ProgressDialog progressDialog;

    private final static int REQUEST_FIND_AS_MAPS = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Izin lokasi ditolak
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show();
            } else {
                // permintan akses lokasi tanpa izin
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }

        } else {
            // Izin lokasi diberikan
            Toast.makeText(this, "Izin Lokasi diberikan", Toast.LENGTH_SHORT).show();
        }

        // Dasar pembuatan Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // akhir dari dasar pembuatan Maps

        Button btnCreateRoutes = findViewById(R.id.btnCreateRoutes);
        Button btnFindAsMaps = findViewById(R.id.btnFindAsMaps);
        btnNavToTarget = findViewById(R.id.btnNavToTarget);
        etOrigin = findViewById(R.id.etOrigin);
        etDestination = findViewById(R.id.etDestination);

        btnCreateRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        btnFindAsMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, FindAsMapsActivity.class);
                startActivityForResult(i, REQUEST_FIND_AS_MAPS);
            }
        });

        btnNavToTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriGoogleMaps = Uri.parse("http://maps.google.com/maps?daddr=" + destination.latitude + "," + destination.longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uriGoogleMaps);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, " Tolong Masukan Alamat Anda!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Tolong Masukan Tujuan anda !", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFindPath(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                ((EditText) findViewById(R.id.etOrigin))
                        .setText(place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            }  // The user canceled the operation.

        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                ((EditText) findViewById(R.id.etDestination))
                        .setText(place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            }  // The user canceled the operation.

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Menemukan lokasi yang ditentukan
        LatLng amik = new LatLng(-7.400798, 109.231160);
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(amik).title("Universitas AMIKOM Purwokerto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(amik, 16));
        // akhir dari Menemukan lokasi yang ditentukan

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // mengetahui lokasi saat ini
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onDirectionFindStart() {
        btnNavToTarget.setVisibility(View.GONE);
        progressDialog = ProgressDialog.show(this, "Sebentar...",
                "Aku lagi nyari lokasinya, tunggu ya...", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFindSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        // Membuat dan mencari rute tercepat
        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 18));
            String distances = "Jarak tempuh: " + route.distance.text;
            ((TextView) findViewById(R.id.tvDistance)).setText(distances);

            String durations = "Waktu perkiraan sampai: " + route.duration.text;
            ((TextView) findViewById(R.id.tvDuration)).setText(durations);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            destination = route.endLocation;
            btnNavToTarget.setVisibility(View.VISIBLE);

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
        // akhir dari Membuat dan Mencari rute tercepat

        mMap.animateCamera(CameraUpdateFactory.zoomTo(8f));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "My Location clicked!", Toast.LENGTH_SHORT).show();
        return true;
    }

    // mengetahui lokasi saat ini
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }

        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
    }
}


