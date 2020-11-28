package com.shilo.golanimanage.mainactivity.model;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class Soldier implements Serializable {
    private String id;
    private String name;
    private long rate;//summery rate
    private String comment;
    private ArrayList<Report> reports;

    public Soldier() {
        reports = new ArrayList<>();
    }

    public Soldier(String id, String name, long rate, String comment) {
        new Soldier();
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.comment = comment;
    }

    public ArrayList<Report> getReports() {
        return reports;
    }



    public void setReports(ArrayList<Report> reports) {
        this.reports = reports;
    }

    public Soldier(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return id.equals(((Soldier) obj).getId());
    }
}
