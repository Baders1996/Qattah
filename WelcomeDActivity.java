package com.example.bader.qattah;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeDActivity extends AppCompatActivity {

    ImageButton request, history, mange, Signout;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_d);

        request = findViewById(R.id.CheckRequest);
        history = findViewById(R.id.RideHistory);
        mange = findViewById(R.id.MangeAccount);
        Signout = findViewById(R.id.Signout);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    public void CheckRequest(View view){
        startActivity(new Intent(WelcomeDActivity.this, ViewRequestsActivity.class));
    }
    public void RideHistory(View view){

    }
    public void MangeAccount(View view){
        startActivity(new Intent(WelcomeDActivity.this, MangeAccountDActivity.class));
    }
    public void Signout(View view){
        auth.signOut();
        progressDialog.setMessage("Signing out...");
        progressDialog.show();
        startActivity(new Intent(WelcomeDActivity.this, SignIn.class));
        progressDialog.cancel();
        finish();
    }
}
