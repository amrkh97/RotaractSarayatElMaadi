package com.example.racsarayat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

class HelperFunctions {

    @SuppressLint("StaticFieldLeak")
    static
    class GetUserPicture extends AsyncTask<String, Void, Bitmap> {
        private ImageView mUserImage;

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
}
