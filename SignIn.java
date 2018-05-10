package com.example.bader.qattah;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    EditText email, password;
    TextView textView;
    Button SignInButton;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        SignInButton = findViewById(R.id.SignUpButton);
        textView = findViewById(R.id.textView6);
        SignInButton.setOnClickListener(this);
        textView.setOnClickListener(this);
        switch1 = findViewById(R.id.switch1);
    }

    SignIn(){

    }

    public boolean issChecked() throws Exception {
        try {
            boolean SwitchState = switch1.isChecked();
            if (SwitchState == true)
                return true;
            else
                return false;
        } catch (Exception e){
            e.getMessage();
        }
        return true;
    }

    public void Signin() {
        String text1 = email.getText().toString().trim();
        String text2 = password.getText().toString().trim();

        if (TextUtils.isEmpty(text1)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(text2)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Signing in... ");
        progressDialog.show();

        auth.signInWithEmailAndPassword(text1, text2).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                boolean switchstate = switch1.isChecked();

                    if (switchstate == true) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignIn.this, WelcomeActivity.class));
                            progressDialog.cancel();
                            finish();
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(SignIn.this, "E-Mail/Password invalid", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignIn.this, WelcomeDActivity.class));
                            progressDialog.cancel();
                            finish();
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(SignIn.this, "E-Mail/Password invalid", Toast.LENGTH_LONG).show();
                        }
                    }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == SignInButton){
            Signin();
        }

        if (view == textView) {
            boolean switchstate = switch1.isChecked();
            if (switchstate == true) {
                startActivity(new Intent(SignIn.this, CreateNewAccountpActivity.class));
            } else{
                startActivity(new Intent(SignIn.this, CreateNewAccountDActivity.class));
            }
        }
    }
}
