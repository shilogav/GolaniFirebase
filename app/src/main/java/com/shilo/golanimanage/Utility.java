package com.shilo.golanimanage;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.ArrayList;

public class Utility {

    public ArrayList<String> teamsListName;

    public static LoggedInUser fromSharedPreferences(SharedPreferences prefs)
    {
        Gson gson = new Gson();
        String json = prefs.getString("user", "");
        return gson.fromJson(json, LoggedInUser.class);
    }

    public static void toSharedPreferences(SharedPreferences.Editor editor,LoggedInUser user)
    {
        Gson gson=new Gson();
        String json=gson.toJson(user);
        editor.putString("user",json);
        editor.commit();
    }

}