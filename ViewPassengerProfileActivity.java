package com.example.bader.qattah;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewPassengerProfileActivity extends AppCompatActivity {


    TextView username_1, PhoneNumber_1;
    Button button;
    String username;
    String PhoneNumber;

    public void ViewProfile(View view){
        String key = getIntent().getStringExtra("key");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Passenger").child(key).child("Username");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database1.getReference().child("Passenger").child(key).child("Phone Number");

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PhoneNumber = dataSnapshot.getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        username_1.setText(username);
        PhoneNumber_1.setText(PhoneNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_passenger_profile);

        username_1 = findViewById(R.id.textView18);
        PhoneNumber_1 = findViewById(R.id.textView23);
        button = findViewById(R.id.button3);
    }
}
