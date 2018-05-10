package com.example.bader.qattah;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class MapsDActivity extends FragmentActivity implements OnMapReadyCallback {

    volatile static LinkedList<String> keys;


    //Setting up
    static double pla, plo, dla, dlo;


    //Location Stuff
    LocationManager locationManager;
    LocationListener locationListener;
    static Location lastKnownLocation;

    ArrayList<LatLng> listPoints;

    //Database Things
    FirebaseAuth auth;
    FirebaseUser user;
    String key;

    private GoogleMap mMap;
    static final double SEARCH_RADIUS = 5;

    public static final int LOCATION_REQUEST = 500;
    //Geofire instantiation
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Pick Up");
    GeoFire geoFire = new GeoFire(ref);




    public void updateWaypoints(Location location) {
        if (location != null) {
            lastKnownLocation = location;
        }
    }

    public void ViewPassengerProfile(View view){
        String key = getIntent().getStringExtra("key");
        Intent intent = new Intent(MapsDActivity.this, ViewPassengerProfileActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    public void done(View view){
        startActivity(new Intent(MapsDActivity.this, WelcomeDActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_d);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listPoints = new ArrayList<>();
        //Keys LinkedList for keys entered calls
        //keys = new LinkedList<>();

        //Database stuff
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        Log.i(" key: ", key + ", ");

        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database1.getReference().child("Active Ride Record").child(key).child("PickUp Latitude");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pla = (double) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database2.getReference().child("Active Ride Record").child(key).child("PickUp Longitude");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plo = (double) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference().child("Active Ride Record").child(key).child("DropOff Latitude");
        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dla = (double) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase database4 = FirebaseDatabase.getInstance();
        DatabaseReference myRef4 = database4.getReference().child("Active Ride Record").child(key).child("DropOff Longitude");
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dlo = (double) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LatLng pickupLocation = new LatLng(pla, plo);
        LatLng dropoffLocation= new LatLng(dla, dlo);

        Log.i("Pick Up: ", pla + ", " + plo);
        Log.i("drop off: ", dla + ", " + dlo);
        mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pick-Up").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.addMarker(new MarkerOptions().position(dropoffLocation).title("Drop-Off").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


        //Set up location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Set up some settings
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateWaypoints(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Do we need to ask user for location permission?
        if (Build.VERSION.SDK_INT < 23) {
            //No
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //URL stuff?
            listPoints.add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            listPoints.add(pickupLocation);
            listPoints.add(dropoffLocation);

            String url = getRequestUrl(listPoints.get(0), listPoints.get(1), listPoints.get(2));
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);

        } else{
            //Yes
            //Do we already have permission?
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //No
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                mMap.setMyLocationEnabled(true);
            } else{
                //Yes
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //URL stuff?
                listPoints.add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                listPoints.add(pickupLocation);
                listPoints.add(dropoffLocation);

                String url = getRequestUrl(listPoints.get(0), listPoints.get(1), listPoints.get(2));
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

                mMap.setMyLocationEnabled(true);

                if(lastKnownLocation != null) {
                    updateWaypoints(lastKnownLocation);
                }
            }
        }
    }

    private String getRequestUrl(LatLng origin, LatLng waypoint, LatLng destination) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String param = str_org + "&" + str_dest + "&" + "waypoints=" + waypoint.latitude + "," + waypoint.longitude;
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
    }

    private  String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Response Result?
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }  catch (Exception e){
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse Json here
            TaskParserDir taskParserDir = new TaskParserDir();
            taskParserDir.execute(s);
        }
    }

    public class TaskParserDir extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);

                JSONArray array = jsonObject.getJSONArray("routes");

                JSONObject jroutes = array.getJSONObject(0);

                JSONArray legs = jroutes.getJSONArray("legs");

                JSONObject steps = legs.getJSONObject(0);

                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);

            } catch (JSONException e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;
            int i = 0;
            try {
                for (i = 0; i < lists.size(); i++) {
                    List<HashMap<String, String>> path = lists.get(i);
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        points.add(new LatLng(lat, lon));
                    }

                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.geodesic(true);
                }
            } catch (Exception e){
                e.printStackTrace();
                Log.i("We reached: ", i + "");
            }

            if(polylineOptions != null){
                mMap.addPolyline(polylineOptions);
            }
            else{
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
