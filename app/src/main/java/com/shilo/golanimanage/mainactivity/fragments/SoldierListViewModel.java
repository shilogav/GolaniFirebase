package com.shilo.golanimanage.mainactivity.fragments;

import android.app.Activity;

import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SoldierListViewModel extends ViewModel {
    private Repository repository;

    public SoldierListViewModel() {
        this.repository = new Repository();
    }

    public MutableLiveData<LoggedInUser> getUser(Activity activity){
        return repository.getUser(activity);
    }

    public void getCloud() {
        repository.getCloud();
    }

    public MutableLiveData<List<Soldier>> getSoldiersLiveData() {
        return repository.getSoldiersLiveData();
    }



    private void toCloud(){
        repository.toCloud();
    }

}
