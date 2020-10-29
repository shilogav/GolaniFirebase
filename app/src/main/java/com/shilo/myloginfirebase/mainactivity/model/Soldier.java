package com.shilo.myloginfirebase.mainactivity.model;

public class Soldier {
    private String id;
    private String name;
    private int rate;//summery rate
    private String comment;

    public Soldier() {
    }

    public Soldier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
