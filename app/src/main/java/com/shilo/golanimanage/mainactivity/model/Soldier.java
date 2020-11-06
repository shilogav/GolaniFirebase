package com.shilo.golanimanage.mainactivity.model;

public class Soldier {
    private String id;
    private String name;
    private long rate;//summery rate
    private String comment;

    public Soldier() {
    }

    public Soldier(String id, String name, long rate, String comment) {
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.comment = comment;
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

    public long getRate() {
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
