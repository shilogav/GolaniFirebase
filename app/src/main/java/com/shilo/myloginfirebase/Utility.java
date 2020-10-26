package com.shilo.myloginfirebase;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.shilo.myloginfirebase.model.LoggedInUser;

public class Utility {

    public enum Role {
        A,
        B,
        C
    }

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
