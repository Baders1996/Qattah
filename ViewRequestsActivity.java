package com.example.bader.qattah;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ViewRequestsActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    static Location lastKnownLocation;

    ListView requestListView;
    ArrayList<Request> mapRequests = new ArrayList<Request>();
    ArrayList<Request> listedRequests = new ArrayList<Request>();
    ArrayList<String> requestDistances = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    boolean changeOccured = false;

    //Database Things
    FirebaseAuth auth;
    static String key;

    //GeoQuery
    GeoQuery geoQuery;

    //Geofire instantiation
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Pick Up");
    GeoFire geoFire = new GeoFire(ref);
    static final double SEARCH_RADIUS = 2;  //0.05

    double pickupLatitude;
    double pickupLongitude;
    double dropoffLatitude;
    double dropoffLongitude;


    public void addTheGeoQueryListener(){
        //GeoQuery Events
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Location l = new Location("");
                l.setLatitude(location.latitude); l.setLongitude(location.longitude);
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(ViewRequestsActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String pickupAddress = addresses.get(0).getAddressLine(0);

                geocoder = new Geocoder(ViewRequestsActivity.this, Locale.getDefault());


                Request request = new Request(location, key, (double)((Math.round((double)(ViewRequestsActivity.lastKnownLocation.distanceTo(l))*10)))/10, pickupAddress);
                if(!mapRequests.contains(request)) {
                    mapRequests.add(request);

                    Collections.sort(mapRequests);
                    if (mapRequests.size() > 10)
                        mapRequests.remove(10);

                    changeOccured = true;
                }
            }

            @Override
            public void onKeyExited(String key) {
                for(int i = 0; i < mapRequests.size(); i++){
                    if(mapRequests.get(i).key.equals(key)) {
                        mapRequests.remove(i);
                        break;
                    }
                }
                changeOccured = true;
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    public void updateListView(Location location) {
        if (location != null) {

            //Updating the location, then placing it in a variable
            if (true) {
                lastKnownLocation = location;

                geoQuery.setCenter(new GeoLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));

                /*//GeoQuery Events
                addTheGeoQueryListener();
                */
                //Refreshing the listed requests list
                if(changeOccured) {
                    listedRequests.clear();
                    listedRequests.addAll(mapRequests);

                    requestDistances.clear();

                    for (int i = 0; i < listedRequests.size(); i++) {
                        requestDistances.add("Pick Up Address:\n" + listedRequests.get(i).pickAddress + "\n\n");
                    }
                    //ArrayAdapter update
                    arrayAdapter.notifyDataSetChanged();
                    changeOccured = false;
                    requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            key = listedRequests.get(i).key;

                            Log.i("He says key, I say bull", key);

                            Intent intent = new Intent(ViewRequestsActivity.this, MapsDActivity.class);
                            intent.putExtra("key", key);
                            intent.putExtra("key", key);

                            Log.i("pickup: ", pickupLatitude + "," + pickupLongitude);
                            Log.i("dropoff: ", dropoffLatitude + "," + dropoffLongitude);

                            FirebaseDatabase database5 = FirebaseDatabase.getInstance();
                            DatabaseReference myRef5 = database5.getReference().child("Driver").child(auth.getUid()).child("Longitude");
                            myRef5.setValue(lastKnownLocation.getLongitude());

                            FirebaseDatabase database6 = FirebaseDatabase.getInstance();
                            DatabaseReference myRef6 = database6.getReference().child("Driver").child(auth.getUid()).child("Latitude");
                            myRef6.setValue(lastKnownLocation.getLatitude());

                            FirebaseDatabase database7 = FirebaseDatabase.getInstance();
                            DatabaseReference myRef7 = database7.getReference().child("Active Ride Record").child(key).child("Driver ID");
                            myRef7.setValue(auth.getUid());


                            FirebaseDatabase.getInstance().getReference("PickUp").child(key).removeValue();
                            startActivity(intent);
                        }
                    });
                }
                else if(requestDistances.size()>1 && requestDistances.contains("No Requests within 5 kilometers found")){
                    requestDistances.remove("No Requests within 5 kilometers found");

                    //ArrayAdapter update
                    arrayAdapter.notifyDataSetChanged();
                }
                if(mapRequests.isEmpty()){
                    requestDistances.add("No Requests within 5 kilometers found");

                    //ArrayAdapter update
                    arrayAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    ViewRequestsActivity(){
        String keyAuht = key;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Permission was granted by user
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateListView(lastKnownLocation);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        setTitle("Nearby Requests");

        requestListView = (ListView) findViewById(R.id.requestListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requestDistances);
        requestDistances.add("Getting Nearby Requests...");
        requestListView.setAdapter(arrayAdapter);


        //Database stuff
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("Active Ride Record");
        key = ref.push().getKey();
        ref.keepSynced(true);

        //Set up location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Set up some settings
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateListView(location);

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
            geoQuery = geoFire.queryAtLocation(new GeoLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), SEARCH_RADIUS);
            addTheGeoQueryListener();

        } else{
            //Yes
            //Do we already have permission?
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //No
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else{
                //Yes
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocation != null) {
                    geoQuery = geoFire.queryAtLocation(new GeoLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), SEARCH_RADIUS);
                    addTheGeoQueryListener();
                    updateListView(lastKnownLocation);
                }
            }
        }
    }
}
