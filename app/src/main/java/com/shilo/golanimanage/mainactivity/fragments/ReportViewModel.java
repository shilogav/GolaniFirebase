package com.shilo.golanimanage.mainactivity.fragments;

import android.util.Log;

import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.model.Report;
import com.shilo.golanimanage.mainactivity.model.Soldier;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {
    Repository repository;
    Soldier soldier;
    private MutableLiveData<Report> reportMutableLiveData;

    public ReportViewModel() {
        this.repository = Repository.getInstance();

    }

    public void setSoldier(Soldier soldier) {
        this.soldier = soldier;
    }

    public void setReport(Report report) {
        repository.setReport(report, soldier);
    }

    public MutableLiveData<Report> getReport(Soldier soldier, String reportType) {
        Log.i("ReportViewModel", "fetch report from cloud executed");
        return repository.getReport(soldier, reportType);
    }
}
