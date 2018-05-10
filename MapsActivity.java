package com.example.bader.qattah;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import java.util.List;

import static com.example.bader.qattah.ViewRequestsActivity.SEARCH_RADIUS;
import static com.example.bader.qattah.ViewRequestsActivity.lastKnownLocation;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    Marker pickUpLocation;
    Marker dropOffLocation;
    Marker currentLocation;
    LatLng p, d;

    GeoFire pickUP, dropOff;

    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;

    Button callQattaButton;
    Button button4;
    boolean requestActive = false;

    TextView InfoTextView;

    Handler handler = new Handler();

    String DriverKey;
    String DriverLongitude;
    String DriverLatitude;


    double DDriverLongitude;
    double DDriverLatitude;

    static int totDistance = 0;

    ValueEventListener listener;


    //Database Things
    DatabaseReference ref;
    DatabaseReference refPickUp;
    DatabaseReference refDropOff;
    DatabaseReference refDone;

    FirebaseAuth auth;
    String key;
    String DoneKey;

    static int price = 0;


    //Radio Buttons for picking pickup and dropoff location
    private RadioGroup radioLocationGroup;

    public void addListenerOnButton() {

        radioLocationGroup = (RadioGroup) findViewById(R.id.radioLocation);
    }

    public void callQatta(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (requestActive) {
                //Remove the request
                ref.child(key).removeValue();
                refPickUp.child(auth.getUid()).removeValue();
                refDropOff.child(auth.getUid()).removeValue();
                callQattaButton.setText("Call Qatta");
                requestActive = false;
                Toast.makeText(this, "Request Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                if (p != null && d != null) {
                        ref = FirebaseDatabase.getInstance().getReference("Active Ride Record");
                        refPickUp = FirebaseDatabase.getInstance().getReference("Pick Up");
                        refDropOff = FirebaseDatabase.getInstance().getReference("Drop Off");

                        pickUP = new GeoFire(refPickUp);
                        dropOff = new GeoFire(refDropOff);
                        key = auth.getUid();

                        //Enter something to get the current user's name from instance (send it over or something), then write it into the database along with the rest of the data as a rideRecord
                        ref.child(key).child("PickUp Longitude").setValue(p.longitude);
                        ref.child(key).child("PickUp Latitude").setValue(p.latitude);
                        ref.child(key).child("DropOff Longitude").setValue(d.longitude);
                        ref.child(key).child("DropOff Latitude").setValue(d.latitude);
                        ref.child(key).child("Passenger ID").setValue(auth.getUid());
                        ref.child(key).child("Driver ID").setValue("null");

                        pickUP.setLocation(auth.getUid(), new GeoLocation(p.latitude, p.longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                } else {
                                    System.out.println("Location saved on server successfully!");
                                }
                            }
                        });

                        dropOff.setLocation(auth.getUid(), new GeoLocation(d.latitude, d.longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                } else {
                                    System.out.println("Location saved on server successfully!");
                                }
                            }
                        });

                        //Button was pressed, change its label now
                        callQattaButton.setText("Cancel Qatta");
                        requestActive = true;
                        Toast.makeText(this, "Request Placed", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                        final DatabaseReference myRef1 = database1.getReference().child("Active Ride Record").child(key).child("Driver ID");

                       listener = myRef1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.getValue().equals("null")) {
                                    callQattaButton.setVisibility(View.INVISIBLE);
                                    button4.setVisibility(View.VISIBLE);
                                    InfoTextView.setText("Your Driver is on the WAY!");
                                    DriverKey = dataSnapshot.getValue().toString();
                                    myRef1.removeEventListener(listener);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        FirebaseDatabase database9 = FirebaseDatabase.getInstance();
                        final DatabaseReference myRef9 = database9.getReference().child("Active Ride Record").child(key).child("Driver ID");

                        myRef9.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                    final DatabaseReference myRef2 = database2.getReference().child("Driver").child(DriverKey).child("Longitude");

                                    myRef2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                DriverLongitude = dataSnapshot.getValue().toString();
                                                DDriverLongitude = Double.parseDouble(DriverLongitude);


                                            } catch (Exception e) {
                                                e.getMessage();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                                    final DatabaseReference myRef3 = database3.getReference().child("Driver").child(DriverKey).child("Latitude");

                                    myRef3.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                DriverLatitude = dataSnapshot.getValue().toString();
                                                DDriverLatitude = Double.parseDouble(DriverLatitude);
                                            } catch (Exception e) {
                                                e.getMessage();
                                            }
                                            TaskParser taskParser = new TaskParser();
                                            taskParser.execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + DDriverLatitude + "," + DDriverLongitude + "&destinations=" + p.latitude + "," + p.longitude);

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TaskParser taskParser = new TaskParser();
                                                    taskParser.execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + DDriverLatitude + "," + DDriverLongitude + "&destinations=" + p.latitude + "," + p.longitude);
                                                }
                                            }, 2000);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                } else {
                    Toast.makeText(this, "Could not find location. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void Done(View view){
        refDone = FirebaseDatabase.getInstance().getReference("Inactive Ride Record");
        refPickUp = FirebaseDatabase.getInstance().getReference("Pick Up");
        refDropOff = FirebaseDatabase.getInstance().getReference("Drop Off");
        DoneKey = refDone.push().getKey();
        refDone.child(DoneKey).child("Passenger ID").setValue(auth.getUid());
        refDone.child(DoneKey).child("Driver ID").setValue(DriverKey);
        refDone.child(DoneKey).child("Distance").setValue(totDistance);
        refDone.child(DoneKey).child("Price").setValue(totDistance * 1);
        ref = FirebaseDatabase.getInstance().getReference("Active Ride Record").child(auth.getUid());
        //ref.child("Driver ID").setValue("null");
        refPickUp.child(auth.getUid()).removeValue();
        refDropOff.child(auth.getUid()).removeValue();

        try {
            ref.removeEventListener(listener);
            ref.removeValue();
        }catch (Exception e){
            e.getMessage();
        }
        price = totDistance * 1;
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("price", price);
        intent.putExtra("key", DoneKey);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission was granted by user
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateMap(lastKnownLocation, true);
                }
            }
        }
    }

    public void updateMap(Location location, boolean flag) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (flag) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
        if (currentLocation != null)
            currentLocation.remove();
        currentLocation = mMap.addMarker(new MarkerOptions().position(userLocation).title("Dropoff").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Instantiate button
        callQattaButton = (Button) findViewById(R.id.callQattaButton);
        button4 = findViewById(R.id.button4);
        button4.setVisibility(View.INVISIBLE);
        InfoTextView = (TextView) findViewById(R.id.textView77);

        DriverKey = "";
        DriverLongitude = "";
        DriverLatitude = "";

        DDriverLatitude = 0.0;
        DDriverLongitude = 0.0;


        //Database stuff
        auth = FirebaseAuth.getInstance();
        //For the radio buttons
        addListenerOnButton();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        //Set up location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Set up some settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap(location, false);
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
        } else{
            //Yes
            //Do we already have permission?
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //No
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else{
                //Yes
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocation != null) {
                    updateMap(lastKnownLocation, true);
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (((RadioButton)findViewById(radioLocationGroup.getCheckedRadioButtonId())).getText().equals("Set pick-up location")) {
            if(pickUpLocation!=null)
                pickUpLocation.remove();
            p = latLng;
            pickUpLocation = mMap.addMarker(new MarkerOptions().position(latLng).title("Pickup").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        else{
            if(dropOffLocation!=null)
                dropOffLocation.remove();
            d = latLng;
            dropOffLocation = mMap.addMarker(new MarkerOptions().position(latLng).title("Dropoff").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    public class TaskParser extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... googleMapUrl) {
            StringBuffer stringBuffer = null;
            BufferedReader bufferedReader = null;
            try {
                URLConnection urlConn = null;
                //Remove
                Log.i("Content of json", googleMapUrl[0]);
                URL url = new URL(googleMapUrl[0]);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (IOException e) {
            }

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(stringBuffer.toString());

                JSONArray array = jsonObject.getJSONArray("rows");

                if(array.length() == 0) {
                    throw new Exception("Why is it empty?!");
                }
                JSONObject routes = array.getJSONObject(0);

                JSONArray elements = routes.getJSONArray("elements");

                JSONObject duration = elements.getJSONObject(0);


                return duration;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }finally{
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject duration) {
            if(duration != null)
            {
                try {
                    JSONObject time = duration.getJSONObject("duration");
                    JSONObject distance = duration.getJSONObject("distance");
                    totDistance = distance.getInt("value") / 1000;
                    Toast.makeText(MapsActivity.this, time.getString("text"), Toast.LENGTH_SHORT).show();
                    InfoTextView.setText("Your Driver is " + time.getString("text") + " AWAY!");

                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                }
            }

        }
    }
}
