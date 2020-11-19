package com.shilo.golanimanage.mainactivity.model;

public class Question {
    String title;
    int rate;

    public Question() {
    }

    public Question(String title, int rate) {
        this.title = title;
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
