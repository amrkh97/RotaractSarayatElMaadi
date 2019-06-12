package com.example.racsarayat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {

    private Button mResetPass;
    private TextView mEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mEmail = findViewById(R.id.resetText_email);
        mResetPass = findViewById(R.id.btnResetPass);


        mResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mEmail.getText().toString().matches(".+[@].+[.].+")) {
                    Toast.makeText(ResetPassActivity.this, "Please Enter a valid mail", Toast.LENGTH_LONG).show();
                    mEmail.setText("");

                } else {

                    FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPassActivity.this, "An Email was Sent to reset your pass", Toast.LENGTH_LONG).show();
                                        Intent myIntent = new Intent(ResetPassActivity.this, MainActivity.class);
                                        startActivity(myIntent);
                                    } else {
                                        Toast.makeText(ResetPassActivity.this, "PassReset Fail, please check the entered mail", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }

            }
        });


    }





}
