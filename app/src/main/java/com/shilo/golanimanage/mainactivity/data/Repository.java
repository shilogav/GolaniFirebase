package com.shilo.golanimanage.mainactivity.data;

import android.app.Activity;
import android.content.SharedPreferences;

import com.shilo.golanimanage.Utility;
import com.shilo.golanimanage.mainactivity.livedata.TeamLiveData;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.mainactivity.model.Team;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.List;

import androidx.lifecycle.MutableLiveData;

import static android.content.Context.MODE_PRIVATE;

/**
 * user live data is initialize here
 * else of live data values initialize in dataSource class
 */
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
        getUserLiveData().setValue(new LoggedInUser("user1"));
        dataSource.setUserLiveData(getUserLiveData());
        return getUserLiveData();
    }

    public void toCloud() {
        dataSource.toCloud();
    }

    /**
     * deprecated method
     * @return
     */
    public TeamLiveData getTeams(){
        return dataSource.getFirestoreTeamLiveData();
    }

    public void getCloud(){
        dataSource.getFromCloud();
    }

    public MutableLiveData<Team> getTeamLiveData() {
        return dataSource.getTeamLiveData();
    }

    public MutableLiveData<List<Soldier>> getSoldiersLiveData() {
        return dataSource.getSoldiersLiveData();

    }
}
