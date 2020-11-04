package com.shilo.golanimanage.mainactivity.data;

import android.app.Activity;
import android.content.SharedPreferences;

import com.shilo.golanimanage.Utility;
import com.shilo.golanimanage.mainactivity.livedata.TeamLiveData;
import com.shilo.golanimanage.model.LoggedInUser;

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

    public void toCloud() {
        dataSource.toCloud();
    }

    public TeamLiveData getTeams(){
        return dataSource.getFirestoreTeamLiveData();
    }

    public void getCloud(){
        dataSource.getFromCloud(getUserLiveData().getValue());
    }
}
