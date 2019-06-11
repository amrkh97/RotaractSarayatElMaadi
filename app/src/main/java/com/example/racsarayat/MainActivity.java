package com.example.racsarayat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mSignIn;
    private Button mAmrBtn;
    private TextView mResetPass;
    private FirebaseAuth mAuth;

    /**
     * verifyStaticCredentials: Verify the mail and password to be sent to the backend.
     *
     * @param userEmail
     * @param userPassword
     * @return verificationErrorType
     */
    public static verificationErrorType verifyStaticCredentials(String userEmail, String userPassword) {
        /* If the user entered an invalid Email Address */
        if (!userEmail.matches(".+[@].+[.].+")) {
            return verificationErrorType.INVALID_EMAIL;
        }
        /* If the user entered an empty Password */
        else if (userPassword.isEmpty() || userPassword.length() < 8 || userPassword.contains(" ")) {
            return verificationErrorType.INVALID_PASSWORD;
        } else {
            return verificationErrorType.NO_ERRORS;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.editText_email);
        mPassword = findViewById(R.id.editText_pass);
        mSignIn = findViewById(R.id.btnLogin);
        mResetPass = findViewById(R.id.resetpass_redirect);
        mAmrBtn = findViewById(R.id.amr_btn);


        mAmrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO(1): Determine where to go from here.


            }
        });

        mResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MainActivity.this, ResetPassActivity.class);
                startActivity(myIntent);

            }
        });


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationErrorType ErrorType = verifyStaticCredentials(mEmail.getText().toString(), mPassword.getText().toString());
                if (ErrorType == verificationErrorType.NO_ERRORS) {
                    //Sign In with User Mail and Pass:
                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(MainActivity.this, "Authentication Successful.",
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        Intent myIntent = new Intent(MainActivity.this, FeedActivity.class);
                                        startActivity(myIntent);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        mEmail.setText("");
                                        mPassword.setText("");
                                    }
                                }
                            });

                } else {

                    Toast.makeText(MainActivity.this, "Enter a Valid mail or pass", Toast.LENGTH_SHORT).show();
                    mEmail.setText("");
                    mPassword.setText("");


                }
            }
        });


    }

    /**
     * verificationErrorType: An enum to determine state of data before sending to Firebase.
     */
    enum verificationErrorType {
        NO_ERRORS,
        INVALID_EMAIL,
        INVALID_PASSWORD
    }


////////////////// End of MainActivity.java//////////////////////////
}