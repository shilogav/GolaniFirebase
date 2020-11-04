package com.shilo.golanimanage.mainactivity;

import android.app.Activity;
import android.app.Application;

import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.livedata.TeamLiveData;
import com.shilo.golanimanage.model.LoggedInUser;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainActivityVM extends AndroidViewModel {
    private Repository repository;
    private TeamLiveData teamLiveData;


    public MainActivityVM(@NonNull Application application) {
        super(application);
        this.repository = new Repository();
        this.teamLiveData = null;
        toCloud();
    }

    public void init(){
        if(teamLiveData != null){
            return;
        }
        //repository = NoteRepository.getInstance();

        //teamLiveData = repository.getTeams();
    }

    public MutableLiveData<LoggedInUser> getUser(Activity activity){
        return repository.getUser(activity);
    }

    private void toCloud(){
        repository.toCloud();
    }

    public TeamLiveData getTeamlist(){
        return repository.getTeams();
    }

    public void getCloud() {
        repository.getCloud();
    }

}
