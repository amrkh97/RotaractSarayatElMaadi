package com.example.racsarayat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;

class HelperFunctions {


    static class GetUserPicture extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView mUserImage;

        GetUserPicture(ImageView mImage) {
            mUserImage = mImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String photoUrl = params[0];
                URL url = new URL(photoUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDefaultUseCaches(false);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                Log.e("ERROR:", Arrays.toString(e.getStackTrace()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mUserImage.setImageBitmap(result);

        }
    }


    /**
     * Class that get sidebar Data from server
     */
    @SuppressLint("StaticFieldLeak")
    static class GetSideBarDetails extends AsyncTask<String, Void, String> {
        static final String REQUEST_METHOD = "POST";

        Context mContext;
        ImageView UserPicture;
        TextView UserName;

        GetSideBarDetails(Context X, ImageView IV, TextView TV) {
            mContext = X;
            UserPicture = IV;
            UserName = TV;

        }

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
                Toast.makeText(mContext, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            try {

                JSONObject jsonObject = new JSONObject(result);
                UserName.setText(jsonObject.getString("UserName"));
                HelperFunctions.GetUserPicture Pic = new GetUserPicture(UserPicture);
                Pic.execute(jsonObject.getString("Photo"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
