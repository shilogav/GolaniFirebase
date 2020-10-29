package com.shilo.myloginfirebase.mainactivity.data;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentReference;
import com.shilo.myloginfirebase.Utility;
import com.shilo.myloginfirebase.mainactivity.livedata.TeamLiveData;
import com.shilo.myloginfirebase.model.LoggedInUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.Context.MODE_PRIVATE;

public class Repository {
    DataSource dataSource;
    private MutableLiveData<LoggedInUser> userLiveData;

    public Repository() {
        dataSource = DataSource.getInstance();
    }

    public MutableLiveData<LoggedInUser> getUserLiveData() {
        if (userLiveData == null) {
            userLiveData = new MutableLiveData<>();
        }
        return userLiveData;
    }

    public MutableLiveData<LoggedInUser> getUser(Activity activity){
        SharedPreferences prefs =activity.getSharedPreferences("UserData", MODE_PRIVATE);
        getUserLiveData().setValue(Utility.fromSharedPreferences(prefs));
        dataSource.setUserLiveData(getUserLiveData());
        return getUserLiveData();
    }

    public TeamLiveData getTeams(){
        return dataSource.getFirestoreTeamLiveData();
    }

    public void getCloud(){
        dataSource.getFromCloud(getUserLiveData().getValue());
    }
}
