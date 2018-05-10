package com.example.bader.qattah;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MangeAccountDActivity extends AppCompatActivity {

    EditText email, email1, phoneNum;
    Button submit1;
    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser user;
    //SignIn sign = new SignIn();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mange_account_d);

        email = findViewById(R.id.editText);
        email1 = findViewById(R.id.editText3);
        phoneNum = findViewById(R.id.editText2);
        submit1 = findViewById(R.id.button2);
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
    }

    public void update(View view) throws Exception {
        String text1 = email.getText().toString().trim();
        final String text2 = email1.getText().toString().trim();
        String text3 = phoneNum.getText().toString().trim();

        if (text1.isEmpty() && text2.isEmpty() && text3.isEmpty()){
            Toast.makeText(MangeAccountDActivity.this, "Nothing to update", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MangeAccountDActivity.this, WelcomeDActivity.class));
            finish();

        } else {

            try {
                ref = FirebaseDatabase.getInstance().getReference("Driver");
                if (text1.isEmpty() && text2.isEmpty()) {
                    ref.getRef().child(auth.getUid()).child("Phone Number").setValue(text3);
                    Toast.makeText(MangeAccountDActivity.this, "Account has been updated", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MangeAccountDActivity.this, WelcomeDActivity.class));
                    finish();
                } else if (!text1.equals(text2)) {
                    Toast.makeText(this, "E-Mail does not match", Toast.LENGTH_LONG).show();
                } else {
                    user.updateEmail(text1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        String text1 = email.getText().toString().trim();
                        String text3 = phoneNum.getText().toString().trim();

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (text1.isEmpty() && text2.isEmpty()) {
                                    ref.getRef().child(auth.getUid()).child("Phone Number").setValue(text3);
                                    Toast.makeText(MangeAccountDActivity.this, "Account has been updated", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MangeAccountDActivity.this, WelcomeDActivity.class));
                                    finish();
                                } else {
                                    ref.getRef().child(auth.getUid()).child("E-Mail").setValue(text1);
                                    ref.getRef().child(auth.getUid()).child("Phone Number").setValue(text3);
                                    Toast.makeText(MangeAccountDActivity.this, "Account has been updated", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MangeAccountDActivity.this, WelcomeDActivity.class));
                                    finish();
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }
}
