package com.example.bader.qattah;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    ImageButton request, history, mange, Signout;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        request = findViewById(R.id.CheckRequest);
        history = findViewById(R.id.RideHistory);
        mange = findViewById(R.id.MangeAccount);
        Signout = findViewById(R.id.Signout);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    public void RideRequest(View view){
        startActivity(new Intent(WelcomeActivity.this, MapsActivity.class));
    }
    public void RideHistory(View view){

    }
    public void MangeAccount(View view){
        startActivity(new Intent(WelcomeActivity.this, MangeAccountActivity.class));
    }
    public void Signout(View view){
        auth.signOut();
        progressDialog.setMessage("Signing out...");
        progressDialog.show();
        startActivity(new Intent(WelcomeActivity.this, SignIn.class));
        progressDialog.cancel();
        finish();
    }
}
