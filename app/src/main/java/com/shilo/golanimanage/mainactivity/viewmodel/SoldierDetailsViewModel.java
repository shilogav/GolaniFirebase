package com.shilo.golanimanage.mainactivity.viewmodel;

import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.model.Soldier;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class SoldierDetailsViewModel extends ViewModel {
    Repository repository;
    Soldier soldier;
    private MutableLiveData <String> commentLiveData;


    public SoldierDetailsViewModel(Soldier soldier) {
        repository = Repository.getInstance();
    }

    public MutableLiveData<String> getComment(Soldier soldier) {
        return repository.getComment(soldier);
    }
}
