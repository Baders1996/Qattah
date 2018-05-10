package com.example.bader.qattah;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RatingActivity extends AppCompatActivity {

    float numOFstar;
    DatabaseReference refDone;
    FirebaseAuth auth;
    TextView textView;
    int price;
    String DoneKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        textView = findViewById(R.id.textView5);
        refDone = FirebaseDatabase.getInstance().getReference("Inactive Ride Record");
        auth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        Bundle bundle1 = getIntent().getExtras();
        price = bundle.getInt("price");
        DoneKey = bundle1.getString("key");
        textView.setText("Total Amount is: " + price + " SAR");
    }

    public void Done(View view){
        RatingBar rating = findViewById(R.id.ratingBar);
        numOFstar = rating.getRating();
        refDone.child(DoneKey).child("Rating").setValue(numOFstar);
        startActivity(new Intent(RatingActivity.this, WelcomeActivity.class));
        finish();
    }
}
