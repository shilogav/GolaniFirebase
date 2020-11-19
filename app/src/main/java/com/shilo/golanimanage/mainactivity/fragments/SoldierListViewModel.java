package com.shilo.golanimanage.mainactivity.fragments;

import android.app.Activity;
import android.util.Log;

import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * used for SoldierListFragment and SoldierDetailsFragment fragments
 */
public class SoldierListViewModel extends ViewModel {
    private Repository repository;

    public SoldierListViewModel() {
        this.repository = Repository.getInstance();
    }

    public MutableLiveData<LoggedInUser> getUser(Activity activity){
        return repository.getUser(activity);
    }

    public void getCloud() {
        Log.i("SoldierListViewModel -> getCloud", "executed");
        repository.getCloud();
    }

    public MutableLiveData<List<Soldier>> getSoldiersLiveData() {
        return repository.getSoldiersLiveData();
    }



    public void toCloud(){
        //TODO: write data to cloud
        //repository.toCloud();
    }

}
