package com.example.bader.qattah;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNewAccountDActivity extends AppCompatActivity implements View.OnClickListener{
    EditText username, email, password, phonenum, rePassword;
    Button button;
    FirebaseAuth auth;
    DatabaseReference db;
    Switch aSwitch;
    Switch NSwitch;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account_d);

        auth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        phonenum = findViewById(R.id.phonenum);
        button = findViewById(R.id.button);
        aSwitch = findViewById(R.id.switch2);
        NSwitch = findViewById(R.id.switch4);
        progressDialog = new ProgressDialog(this);
        db = FirebaseDatabase.getInstance().getReference("Driver");
    }

    public void Signup(){
        String text1 = username.getText().toString().trim();
        final String text2 = email.getText().toString().trim();
        final String text3 = password.getText().toString();
        String text33 = rePassword.getText().toString().trim();
        String text4 = phonenum.getText().toString().trim();
        final boolean SwitchState = aSwitch.isChecked();
        final boolean SwitchState1 = NSwitch.isChecked();

        if (text1.isEmpty() || text2.isEmpty() || text3.isEmpty() || text4.isEmpty() || text33.isEmpty()) {
            Toast.makeText(this, "Some field is missing", Toast.LENGTH_LONG).show();
        } else if (!text3.equals(text33)){
            Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show();
        } else{
            progressDialog.setMessage("Registration in progress...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(text2,text3).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                String text1 = username.getText().toString().trim();
                String text4 = phonenum.getText().toString().trim();
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (SwitchState == false && SwitchState1 == false) {
                            db.child(auth.getUid()).child("Username").setValue(text1);
                            db.child(auth.getUid()).child("Phone Number").setValue(text4);
                            db.child(auth.getUid()).child("Gender").setValue("Male");
                            db.child(auth.getUid()).child("Number of passengers").setValue(4);
                            db.child(auth.getUid()).child("E-Mail").setValue(text2);
                            db.child(auth.getUid()).child("Longitude").setValue("");
                            db.child(auth.getUid()).child("Latitude").setValue("");
                            startActivity(new Intent(CreateNewAccountDActivity.this, SignIn.class));
                            finish();
                        }else if (SwitchState == true && SwitchState1 == false){
                            db.child(auth.getUid()).child("Username").setValue(text1);
                            db.child(auth.getUid()).child("Phone Number").setValue(text4);
                            db.child(auth.getUid()).child("Gender").setValue("Female");
                            db.child(auth.getUid()).child("Number of passengers").setValue(4);
                            db.child(auth.getUid()).child("E-Mail").setValue(text2);
                            db.child(auth.getUid()).child("Longitude").setValue("");
                            db.child(auth.getUid()).child("Latitude").setValue("");
                            startActivity(new Intent(CreateNewAccountDActivity.this, SignIn.class));
                            finish();
                        } else if (SwitchState == false && SwitchState1 == true){
                            db.child(auth.getUid()).child("Username").setValue(text1);
                            db.child(auth.getUid()).child("Phone Number").setValue(text4);
                            db.child(auth.getUid()).child("Gender").setValue("Male");
                            db.child(auth.getUid()).child("Number of passengers").setValue(6);
                            db.child(auth.getUid()).child("E-Mail").setValue(text2);
                            db.child(auth.getUid()).child("Longitude").setValue("");
                            db.child(auth.getUid()).child("Latitude").setValue("");
                            startActivity(new Intent(CreateNewAccountDActivity.this, SignIn.class));
                            finish();
                        } else if (SwitchState == true && SwitchState1 == true){
                            db.child(auth.getUid()).child("Username").setValue(text1);
                            db.child(auth.getUid()).child("Phone Number").setValue(text4);
                            db.child(auth.getUid()).child("Gender").setValue("Female");
                            db.child(auth.getUid()).child("Number of passengers").setValue(6);
                            db.child(auth.getUid()).child("E-Mail").setValue(text2);
                            db.child(auth.getUid()).child("Longitude").setValue("");
                            db.child(auth.getUid()).child("Latitude").setValue("");
                            startActivity(new Intent(CreateNewAccountDActivity.this, SignIn.class));
                            finish();
                        }
                    }else if(text3.length() < 6) {
                        progressDialog.cancel();
                        Toast.makeText(CreateNewAccountDActivity.this, "Password should be at least digits", Toast.LENGTH_LONG).show();
                    }else{
                        progressDialog.cancel();
                        Toast.makeText(CreateNewAccountDActivity.this, "Account already exist", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        Signup();
    }
}
