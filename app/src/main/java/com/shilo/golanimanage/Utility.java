package com.shilo.golanimanage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Utility {


    public static Object fromSharedPreferences(SharedPreferences prefs, String keyName)
    {
        Gson gson = new Gson();
        String json = prefs.getString(keyName, "");
        if (keyName.equals("user")) {
            return gson.fromJson(json, LoggedInUser.class);
        } else if (keyName.equals("repository")) {
            return gson.fromJson(json, Repository.class);
        }
        return null;
    }

    public static void toSharedPreferences(SharedPreferences.Editor editor,Object object, String keyName)
    {
        Gson gson=new Gson();
        String json=gson.toJson(object);
        editor.putString(keyName,json);
        editor.commit();
    }

    public static void saveUserForSharedPref(Activity activity, LoggedInUser user) {
        SharedPreferences prefs = activity.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        toSharedPreferences(editor,user, "user");
        Log.i("Utility -> saveUserForSharedPref ", "executed");
    }



}
