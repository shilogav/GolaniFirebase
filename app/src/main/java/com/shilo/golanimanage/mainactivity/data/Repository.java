package com.shilo.golanimanage.mainactivity.data;

import android.app.Activity;
import android.content.SharedPreferences;

import com.shilo.golanimanage.Utility;
import com.shilo.golanimanage.mainactivity.livedata.TeamLiveData;
import com.shilo.golanimanage.mainactivity.model.Report;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.mainactivity.model.Team;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import static android.content.Context.MODE_PRIVATE;

/**
 * user live data is initialize here
 * else of live data values initialize in dataSource class
 */
public class Repository {
    DataSource dataSource;
    static Repository instance;
    private MutableLiveData<LoggedInUser> userLiveData;
    Report report;


    public static Repository getInstance(){
        if (instance == null){
            instance = new Repository();
        }
        return instance;
    }

    private Repository() {
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

        getUserLiveData().setValue((LoggedInUser) Utility.fromSharedPreferences(prefs, "user"));
        //getUserLiveData().setValue(new LoggedInUser("user1"));
        dataSource.setUserLiveData(getUserLiveData());
        return getUserLiveData();
    }

    public void toCloud() {
        //dataSource.toCloud();
    }

    public void createSoldiers(ArrayList<Soldier> soldiers) {
        dataSource.createSoldiers(soldiers);
    }

    /**
     * deprecated method
     * @return
     */
    /*public TeamLiveData getTeams(){
        return dataSource.getFirestoreTeamLiveData();
    }*/

    public void getCloud(){
        dataSource.getFromCloud();
    }

    public MutableLiveData<Team> getTeamLiveData() {
        return dataSource.getTeamLiveData();
    }

    public MutableLiveData<List<Soldier>> getSoldiersLiveData() {
        return dataSource.getSoldiersLiveData();
    }

    public MutableLiveData<Report> getReport(Soldier soldier, String reportType) {
        return dataSource.getReportLiveData(soldier, reportType);
    }

    public void setReport(Report report, Soldier soldier) {
        this.report = report;
        dataSource.setReport(report, soldier, userLiveData.getValue());
    }

    public void deleteSoldier(final Soldier soldier, final String reason){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataSource.deleteSoldier(soldier,reason);
            }
        }).start();
    }

    public MutableLiveData<String> getComment(Soldier soldier) {
        return dataSource.getComment(soldier);
    }
}
