package com.shilo.golanimanage.mainactivity.model;

public class Question {
    String title;
    int rate;
    boolean mutable;

    public Question() {
    }

    public Question(String title, int rate) {
        this.title = title;
        this.rate = rate;
        mutable = true;
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

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable(boolean mutuable) {
        this.mutable = mutuable;
    }
}
