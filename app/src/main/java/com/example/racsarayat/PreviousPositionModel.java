package com.example.racsarayat;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PreviousPositionModel {

    String User_Position;
    String Position_Year;

    /**
     * fromJson: Put Data from a single JSONOBJECT into a UserDataModel
     *
     * @param jsonObject
     * @return Single User Prototype
     */
    public static PreviousPositionModel fromJson(JSONObject jsonObject) {
        PreviousPositionModel DummyUser = new PreviousPositionModel();
        // Deserialize json into object fields
        try {
            DummyUser.User_Position = jsonObject.getString("Position");
            Log.d("AMR", "Position: " + DummyUser.User_Position);
            Log.d("AMR", "Year: " + DummyUser.Position_Year);

            DummyUser.Position_Year = jsonObject.getString("Year");

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return DummyUser;
    }

    /**
     * fromJson: Function Takes jsonArray and divides it into
     * several json objects to be put in the form of UserDataModel
     *
     * @param jsonArray
     * @return ArrayList of Users Prototypes
     */
    public static ArrayList<PreviousPositionModel> fromJson(JSONArray jsonArray) {
        JSONObject UsersJson;
        ArrayList<PreviousPositionModel> AllUsers = new ArrayList<>(jsonArray.length());
        // Process each result in json array, decode and convert to UserModel object
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                UsersJson = jsonArray.getJSONObject(i);

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            PreviousPositionModel Model = PreviousPositionModel.fromJson(UsersJson);

            if (Model != null) {
                AllUsers.add(i, Model);
            }

        }

        return AllUsers;
    }


    public String getUser_Position() {
        return User_Position;
    }

    public String getPosition_Year() {
        return Position_Year;
    }


}
