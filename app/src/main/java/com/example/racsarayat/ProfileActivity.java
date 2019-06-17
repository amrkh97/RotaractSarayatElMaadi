package com.example.racsarayat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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
import java.util.Objects;


public class ProfileActivity extends AppCompatActivity implements GreenAdapter.ListItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private TextView mUserName;
    private ImageView mUserImage;
    private RecyclerView mPositions;
    private GreenAdapter mAdapter;
    private ArrayList<PreviousPositionModel> mDataModels;

    /* SideBar Views */
    ImageView sb_userPhoto;
    TextView sb_userName;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mUserImage = findViewById(R.id.iv_user_image);
        mUserName = findViewById(R.id.tv_user_name);
        mPositions = findViewById(R.id.rv_previous_position);

        JSONObject mJSON = new JSONObject();
        try {
            mJSON.put("token", FirebaseAuth.getInstance().getUid());
            Log.d("AMR", "Token:" + FirebaseAuth.getInstance().getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////////////////////////////////
        /* ToolBar and SideBar SetUp */
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /* Get Header Items */
        View mHeader = navigationView.getHeaderView(0);
        sb_userName = mHeader.findViewById(R.id.UserNameTxt);
        sb_userPhoto = mHeader.findViewById(R.id.UserPhoto);
        sb_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        sb_userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //////////////////////////////////////////////////////////////////////////
        // Calling Async Task with my server url

        String UrlService = APIs.API_GET_USER_INFO;
        ProfileActivity.GetProfileDetails MyProfile = new ProfileActivity.GetProfileDetails();
        MyProfile.execute(UrlService, mJSON.toString());

        UrlService = APIs.API_GET_PREVIOUS_POSITIONS;
        ProfileActivity.GetDetails MyPositions = new ProfileActivity.GetDetails();
        MyPositions.execute(UrlService, mJSON.toString());

    }


    @Override
    public void onListItemClick(int clickedItemIndex) {

        /*
        DO NOTHING
         */
    }


    /**
     * Overrided Function to decide what to do on pressing "Back" key.
     */
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    /**
     * @param menuItem : item in menu of the toolbar
     * @return boolean "true"
     * Overrided Function to create sidebar and decide what to on clicking on it's menu items.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.Feed) {

            Intent myIntent = new Intent(ProfileActivity.this, FeedActivity.class);
            startActivity(myIntent);

        } else if (id == R.id.Profile) {
            //Do Nothing, you are already here.

        } else if (id == R.id.History) {
            Intent myIntent = new Intent(ProfileActivity.this, HistoryActivity.class);
            startActivity(myIntent);

        } else if (id == R.id.Signout) {
            Intent myIntent = new Intent(ProfileActivity.this, SignOutActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
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
                /*
                //To Be Used with POSTMAN:
                http.setRequestProperty("content-type", "application/json");
                http.setRequestProperty("x-auth-token", String.valueOf(FirebaseAuth.getInstance().getUid()));
                */

                /* A Stream object to hold the sent data to API Call */
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                writer.write(JSONString);
                writer.flush();
                writer.close();
                ops.close();
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

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(ProfileActivity.this, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("AMR", "Result= " + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                HelperFunctions.GetUserPicture Pic = new HelperFunctions.GetUserPicture(mUserImage);
                Pic.execute(jsonObject.getString("Photo"));
                HelperFunctions.GetUserPicture Pic2 = new HelperFunctions.GetUserPicture(sb_userPhoto);
                Pic2.execute(jsonObject.getString("Photo"));

                mUserName.setText(jsonObject.getString("UserName"));
                //Side Bar View
                sb_userName.setText(jsonObject.getString("UserName"));

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
