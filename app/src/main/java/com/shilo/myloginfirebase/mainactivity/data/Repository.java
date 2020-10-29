package com.shilo.myloginfirebase.mainactivity.data;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentReference;
import com.shilo.myloginfirebase.Utility;
import com.shilo.myloginfirebase.mainactivity.livedata.TeamLiveData;
import com.shilo.myloginfirebase.model.LoggedInUser;

import androidx.lifecycle.LiveData;

import static android.content.Context.MODE_PRIVATE;

public class Repository {
    DataSource dataSource;

    public Repository() {
        dataSource = DataSource.getInstance();
    }

    public LoggedInUser getUser(Activity activity){
        SharedPreferences prefs =activity.getSharedPreferences("UserData", MODE_PRIVATE);
        return Utility.fromSharedPreferences(prefs);
    }

    public TeamLiveData getTeams(){
        return dataSource.getFirestoreTeamLiveData();
    }
}
