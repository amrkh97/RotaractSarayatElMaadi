package com.example.racsarayat;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity implements GreenAdapter.ListItemClickListener {

    private TextView mUserName;
    private ImageView mUserImage;
    private RecyclerView mPositions;
    private GreenAdapter mAdapter;
    private ArrayList<PreviousPositionModel> mDataModels;
    private FirebaseFirestore db;


    @Override
    public void onListItemClick(int clickedItemIndex) {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        db = FirebaseFirestore.getInstance();

        mUserImage = findViewById(R.id.iv_user_image);
        mUserName = findViewById(R.id.tv_user_name);
        mPositions = findViewById(R.id.rv_previous_position);

        JSONObject mJSON = new JSONObject();

        try {
            mJSON.put("token", FirebaseAuth.getInstance().getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Calling Async Task with my server url

        String UrlService = APIs.API_GET_USER_INFO;
        ProfileActivity.GetProfileDetails MyProfile = new ProfileActivity.GetProfileDetails();
        MyProfile.execute(UrlService, mJSON.toString());

        UrlService = APIs.API_GET_PREVIOUS_POSITIONS;
        ProfileActivity.GetDetails MyPositions = new ProfileActivity.GetDetails();
        MyPositions.execute(UrlService, mJSON.toString());


    }


    @SuppressLint("StaticFieldLeak")
    public class GetProfileDetails extends AsyncTask<String, Void, String> {
        static final String REQUEST_METHOD = "POST";

        @Override
        protected void onPreExecute() {

        }

        /**
         * doInBackground: Returns result string through sending and HTTP request and receiving the response.
         *
         * @param params
         * @return result
         */

        //////////////////////////////////////////////////////////////////////////////////////////
        @Override
        protected String doInBackground(String... params) {
            String UrlString = params[0];
            String JSONString = params[1];
            StringBuilder result = new StringBuilder();

            try {
                /* Create a URL object holding our url */
                URL url = new URL(UrlString);
                /* Create an HTTP Connection and adjust its options */
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod(REQUEST_METHOD);
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setRequestProperty("content-type", "application/json");
                http.setRequestProperty("x-auth-token", String.valueOf(FirebaseAuth.getInstance().getUid()));
                Log.d("MyFirebaseMsgService", String.valueOf(FirebaseAuth.getInstance().getAccessToken(true)));
                /* A Stream object to hold the sent data to API Call */
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                writer.write(JSONString);
                writer.flush();
                writer.close();
                ops.close();
                Log.d("AMR", "Res: " + http.getResponseCode());
                if ("200".equals(String.valueOf(http.getResponseCode()))) {/* A Stream object to get the returned data from API Call */
                    InputStream ips = http.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ips, StandardCharsets.ISO_8859_1));
                    String line = "";
                    //boolean started = false;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    ips.close();
                } else {
                    Log.d("Test", "String.valueOf(http.getResponseCode()): " + http.getResponseCode());
                    result = new StringBuilder("{\"ReturnMsg\":\"An Error Occurred!\"}");
                }

                http.disconnect();
                Log.d("AMR", result.toString());
                return result.toString();

            }
            /* Handling Exceptions */ catch (MalformedURLException e) {
                result = new StringBuilder(e.getMessage());
            } catch (IOException e) {
                result = new StringBuilder(e.getMessage());
            }
            return result.toString();
        }


        /////////////////////////////////////////////////////////////////////////////////////////

        /**
         * onPostExecute: Takes the string result and treates it as a json object
         * to set data of:
         * -Followers Count
         * -Following Count
         * -Books Count
         * -Profile Picture
         *
         * @param result
         */
        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(ProfileActivity.this, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                HelperFunctions.GetUserPicture Pic = new HelperFunctions.GetUserPicture(mUserImage);
                Pic.execute(jsonObject.getString("Photo"));
                mUserName.setText(jsonObject.getString("UserName"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class GetDetails extends AsyncTask<String, Void, String> {
        static final String REQUEST_METHOD = "POST";

        AlertDialog dialog;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String UrlString = params[0];
            String JSONString = params[1];
            StringBuilder result = new StringBuilder();

            try {
                /* Create a URL object holding our url */
                URL url = new URL(UrlString);
                /* Create an HTTP Connection and adjust its options */
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod(REQUEST_METHOD);
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setRequestProperty("content-type", "application/json");
                http.setRequestProperty("x-auth-token", String.valueOf(FirebaseAuth.getInstance().getUid()));
                /* A Stream object to hold the sent data to API Call */
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                writer.write(JSONString);
                writer.flush();
                writer.close();
                ops.close();
                switch (String.valueOf(http.getResponseCode())) {
                    case "200":
                        /* A Stream object to get the returned data from API Call */
                        InputStream ips = http.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(ips, StandardCharsets.ISO_8859_1));
                        String line = "";
                        //boolean started = false;
                        while ((line = reader.readLine()) != null) {
                            //   if ()
                            result.append(line);
                        }
                        reader.close();
                        ips.close();
                        break;
                    case "400":
                        result = new StringBuilder("{\"ReturnMsg\":\"Invalid email or password.\"}");
                        break;
                    case "401":
                        result = new StringBuilder("{\"ReturnMsg\":\"Your account has not been verified.\"}");
                        break;
                    default:
                        break;
                }


                http.disconnect();
                return result.toString();

            }
            /* Handling Exceptions */ catch (MalformedURLException e) {
                result = new StringBuilder(e.getMessage());
            } catch (IOException e) {
                result = new StringBuilder(e.getMessage());
            }
            return result.toString();
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(ProfileActivity.this, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONArray jsonArr = new JSONArray(result);

                mDataModels = PreviousPositionModel.fromJson(jsonArr);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this);
                mPositions.setLayoutManager(layoutManager);
                mPositions.setHasFixedSize(true);
                mAdapter = new GreenAdapter(mDataModels.size(), mDataModels, ProfileActivity.this);
                mPositions.setAdapter(mAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    ////////////// End of ProfileActivity.java //////////////////////
}
