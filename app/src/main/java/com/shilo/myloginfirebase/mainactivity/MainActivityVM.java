package com.shilo.myloginfirebase.mainactivity;

import android.app.Activity;
import android.app.Application;

import com.google.firebase.firestore.DocumentReference;
import com.shilo.myloginfirebase.mainactivity.data.Repository;
import com.shilo.myloginfirebase.mainactivity.livedata.TeamLiveData;
import com.shilo.myloginfirebase.mainactivity.model.Team;
import com.shilo.myloginfirebase.model.LoggedInUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainActivityVM extends AndroidViewModel {
    private Repository repository;
    private TeamLiveData teamLiveData;


    public MainActivityVM(@NonNull Application application) {
        super(application);
        this.repository = new Repository();
        this.teamLiveData = null;
    }

    public void init(){
        if(teamLiveData != null){
            return;
        }
        //repository = NoteRepository.getInstance();
        teamLiveData = repository.getTeams();
    }

    public MutableLiveData<LoggedInUser> getUser(Activity activity){
        return repository.getUser(activity);
    }

    public TeamLiveData getTeamlist(){
        return repository.getTeams();
    }

    public void getCloud() {
        repository.getCloud();
    }

}
